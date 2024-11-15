package com.fastscala.core

import java.net.URLEncoder
import java.nio.file.{ Files, Path }
import java.util.Collections

import scala.collection.mutable as mu
import scala.jdk.CollectionConverters.{ IterableHasAsScala, MapHasAsScala }

import com.typesafe.config.ConfigFactory
import io.prometheus.metrics.model.snapshots.Unit as PUnit
import it.unimi.dsi.util.XoRoShiRo128PlusRandom
import org.eclipse.jetty.http.*
import org.eclipse.jetty.io.Content
import org.eclipse.jetty.server.{ Request, Response as JettyServerResponse }
import org.eclipse.jetty.util.Callback
import org.eclipse.jetty.websocket.api.Callback.Completable
import org.eclipse.jetty.websocket.api.Session
import org.slf4j.LoggerFactory

import com.fastscala.js.Js
import com.fastscala.js.rerenderers.RerendererDebugStatus
import com.fastscala.server.*
import com.fastscala.stats.{ FSStats, StatEvent }
import com.fastscala.utils.{ IdGen, Missing }

class FSFunc(
  val id: String,
  val func: String => Js,
  var keepAliveAt: Long = System.currentTimeMillis(),
  val debugLbl: Option[String] = None,
)(implicit val fsc: FSContext
):
  def fullPath = fsc.fullPath + " => " + debugLbl.getOrElse("anon func")

  def allKeepAlivesIterable: Iterable[Long] = Iterable(keepAliveAt)

  def keepAlive(): Unit =
    keepAliveAt = System.currentTimeMillis()
    fsc.page.keepAlive()

class FSFileUpload(
  val id: String,
  val func: Seq[FSUploadedFile] => Js,
  var keepAliveAt: Long = System.currentTimeMillis(),
)(implicit val fsc: FSContext
):
  def allKeepAlivesIterable: Iterable[Long] = Iterable(keepAliveAt)

  def keepAlive(): Unit =
    keepAliveAt = System.currentTimeMillis()
    fsc.page.keepAlive()

class FSFileDownload(
  val id: String,
  val contentType: String,
  val func: () => Array[Byte],
  var keepAliveAt: Long = System.currentTimeMillis(),
  val debugLbl: Option[String] = None,
)(implicit val fsc: FSContext
):
  def allKeepAlivesIterable: Iterable[Long] = Iterable(keepAliveAt)

  def keepAlive(): Unit =
    keepAliveAt = System.currentTimeMillis()
    fsc.page.keepAlive()

object FSContext:
  val logger = LoggerFactory.getLogger(getClass.getName)

class FSContext(
  val session: FSSession,
  val page: FSPage,
  val parentFSContext: Option[FSContext] = None,
  val onPageUnload: () => Js = () => Js.void,
  val debugLbl: Option[String] = None,
  var deleted: Boolean = false,
) extends FSHasSession:
  import FSContext.logger

  def depth: Int = parentFSContext.map(_.depth + 1).getOrElse(0)

  given fsc: FSContext = this

  private given Option[FSContext] = Some(this)

  private given Option[FSPage] = Some(page)

  private given Option[FSSession] = Some(session)

  private given FSSystem = session.fsSystem

  val functionsGenerated = mu.Set[String]()
  val functionsFileUploadGenerated = mu.Set[String]()
  val functionsFileDownloadGenerated = mu.Set[String]()
  val children = mu.Set[FSContext]()

  def fullPath = Iterator
    .unfold(Option(this))(_.map(ctx => (ctx, ctx.parentFSContext)))
    .toSeq
    .reverse
    .map(_.debugLbl.getOrElse("anon"))
    .mkString(" => ")

  def sendToPage(js: Js): Unit =
    page.wsLock.synchronized:
      page.wsSession.filter(_.isOpen) match
        case Some(session) =>
          try
            // block until session complete sendText
            Completable
              .`with`(cb => session.sendText((js :: page.wsQueue).reverse.reduce(_ & _).cmd, cb))
              .get()
            page.wsQueue = Nil
          catch
            case ex: Exception =>
              page.wsQueue = js :: page.wsQueue
              throw ex
        case None => page.wsQueue = js :: page.wsQueue

  def createNewChildContextAndGCExistingOne(key: AnyRef, debugLabel: Option[String] = None): FSContext =
    if deleted then throw new Exception("Trying to create child of deleted context")
    page.key2FSContext
      .get(key)
      .foreach: existing =>
        children -= existing
        existing.delete()
    val newContext = new FSContext(session, page, Some(this), debugLbl = debugLabel)
    page.key2FSContext(key) = newContext
    if logger.isTraceEnabled then logger.trace(s"Creating context ${newContext.fullPath} ($newContext)")
    children += newContext
    newContext

  def getOrCreateContext(key: AnyRef, debugLabel: Option[String] = None): FSContext =
    if deleted then throw new Exception("Trying to get child of deleted context")
    page.key2FSContext.getOrElseUpdate(
      key, {
        val newContext = new FSContext(session, page, Some(this), debugLbl = debugLabel)
        if logger.isTraceEnabled then logger.trace(s"Creating context ${newContext.fullPath} ($newContext)")
        children += newContext
        newContext
      },
    )

  def deleteContext(key: AnyRef): Unit =
    page.key2FSContext
      .get(key)
      .foreach: existing =>
        if logger.isTraceEnabled then logger.trace(s"DELETING CONTEXT ${existing.fullPath} ($existing)")
        children -= existing
        existing.delete()

  def delete(): Unit =
    if logger.isTraceEnabled then logger.trace(s"Delete context $fullPath")
    parentFSContext.foreach(_.children -= this)

    page.callbacks --= functionsGenerated
    session.fsSystem.stats.event(StatEvent.GC_CALLBACK)
    session.fsSystem.stats.currentCallbacks.dec(functionsGenerated.size)

    page.fileUploadCallbacks --= functionsFileUploadGenerated
    session.fsSystem.stats.event(StatEvent.GC_FILE_UPLOAD)
    session.fsSystem.stats.currentFileUploadCallbacks.dec(functionsFileUploadGenerated.size)

    page.fileDownloadCallbacks --= functionsFileDownloadGenerated
    session.fsSystem.stats.event(StatEvent.GC_FILE_DOWNLOAD)
    session.fsSystem.stats.currentFileDownloadCallbacks.dec(functionsFileDownloadGenerated.size)
    children.foreach(_.delete())
    deleted = true

  def callback(func: () => Js): Js = callback(Js.void, _ => func())

  def callback(
    arg: Js,
    func: String => Js,
    async: Boolean = true,
    expectReturn: Boolean = true,
    ignoreErrors: Boolean = false,
  ): Js =
    if logger.isTraceEnabled then logger.trace(s"CREATING CALLBACK IN CONTEXT ${fullPath}")
    session.fsSystem.gc()
    val funcId = session.nextID()
    functionsGenerated += funcId

    page.callbacks += funcId -> new FSFunc(
      funcId,
      str => func(str) & session.fsSystem.afterCallBackJs.map(_(this)).getOrElse(Js.void),
    )
    session.fsSystem.stats.event(StatEvent.CREATE_CALLBACK, "callback_type" -> "str")
    session.fsSystem.stats.callbacksTotal.inc()
    session.fsSystem.stats.currentCallbacks.inc()

    Js.fromString(
      session.fsSystem.beforeCallBackJs.map(js => s"""(function() {${js.cmd}})();""").getOrElse("") +
        s"window._fs.callback(${if arg.cmd.trim == "" then "''" else arg.cmd},${Js.asJsStr(page.id).cmd},${Js.asJsStr(funcId).cmd},$ignoreErrors,$async,$expectReturn);"
    )

  def anonymousPageURL[Env <: FSXmlEnv](
    using env: Env
  )(
    render: FSContext => env.NodeSeq,
    name: String,
  ): String =
    session.fsSystem.gc()
    val funcId = session.nextID()
    session.anonymousPages += funcId -> new FSAnonymousPage[env.type](funcId, session, render)

    session.fsSystem.stats.event(StatEvent.CREATE_ANON_PAGE, "page_name" -> name)
    session.fsSystem.stats.anonPagesTotal.inc()
    session.fsSystem.stats.currentAnonPages.inc()

    s"/${session.fsSystem.FSPrefix}/anon/$funcId/$name"

  def createAndRedirectToAnonymousPageJS[Env <: FSXmlEnv](
    using env: Env
  )(
    render: FSContext => env.NodeSeq,
    name: String,
  ): Js =
    fsc.callback(() => Js.redirectTo(anonymousPageURL(render, name)))

  def fileDownloadAutodetectContentType(fileName: String, download: () => Array[Byte]): String =
    fileDownload(fileName, Files.probeContentType(Path.of(fileName)), download)

  def fileDownload(fileName: String, contentType: String, download: () => Array[Byte]): String =
    session.fsSystem.gc()
    val funcId = session.nextID()
    functionsFileDownloadGenerated += funcId

    page.fileDownloadCallbacks += funcId -> new FSFileDownload(funcId, contentType, download)

    session.fsSystem.stats.event(
      StatEvent.CREATE_FILE_DOWNLOAD,
      "file_name" -> fileName,
      "content_type" -> contentType,
    )
    session.fsSystem.stats.fileDownloadCallabacksTotal.inc()
    session.fsSystem.stats.currentFileDownloadCallbacks.inc()

    s"/${session.fsSystem.FSPrefix}/file-download/${page.id}/$funcId/${URLEncoder.encode(fileName, "UTF-8")}"

  def fileUploadActionUrl(func: Seq[FSUploadedFile] => Js): String =
    session.fsSystem.gc()
    val funcId = session.nextID()
    functionsFileUploadGenerated += funcId

    page.fileUploadCallbacks += (funcId -> new FSFileUpload(funcId, func))

    session.fsSystem.stats.event(StatEvent.CREATE_FILE_UPLOAD)
    session.fsSystem.stats.fileUploadCallbacksTotal.inc()
    session.fsSystem.stats.currentFileUploadCallbacks.inc()

    s"/${session.fsSystem.FSPrefix}/file-upload/${page.id}/$funcId"

  def exec(func: () => Js, async: Boolean = true): Js = callback(Js("''"), _ => func(), async)

  def fsPageScript(openWSSessionAtStart: Boolean = false): Js =
    page.fsPageScript(openWSSessionAtStart)(using this)

class FSUploadedFile(
  val name: String,
  val submittedFileName: String,
  val contentType: String,
  val content: Array[Byte],
)

object FSUploadedFile:
  def unapply(file: FSUploadedFile): Option[(String, String, String, Array[Byte])] =
    Some(file.name, file.submittedFileName, file.contentType, file.content)

object FSPage:
  val logger = LoggerFactory.getLogger(getClass.getName)

class FSPage(
  val id: String,
  val session: FSSession,
  val req: Request,
  val createdAt: Long = System.currentTimeMillis(),
  val onPageUnload: () => Js = () => Js.void,
  var keepAliveAt: Long = System.currentTimeMillis(),
  var autoKeepAliveAt: Long = System.currentTimeMillis(),
  var wsSession: Option[Session] = None,
  var wsQueue: List[Js] = Nil,
  var wsLock: AnyRef = new AnyRef,
  val debugLbl: Option[String] = None,
) extends FSHasSession:
  var rerendererDebugStatus: RerendererDebugStatus.Value = RerendererDebugStatus.Disabled

  def periodicKeepAliveEnabled: Boolean =
    session.fsSystem.config.getBoolean("com.fastscala.core.periodic-page-keep-alive.enabled")

  def periodicKeepAlivePeriod: Long =
    session.fsSystem.config.getLong("com.fastscala.core.periodic-page-keep-alive.period-millis")

  def periodicKeepAliveDefunctAfter: Long =
    session.fsSystem.config.getLong("com.fastscala.core.periodic-page-keep-alive.defunct-after-millis")

  def keepAlive(): Unit =
    keepAliveAt = System.currentTimeMillis()
    session.keepAlive()

  def allKeepAlivesIterable: Iterable[Long] =
    callbacks.values.map(_.keepAliveAt) ++
      fileUploadCallbacks.values.map(_.keepAliveAt) ++
      fileDownloadCallbacks.values.map(_.keepAliveAt) ++
      Iterable(keepAliveAt)

  val key2FSContext = mu.Map[AnyRef, FSContext]()

  val callbacks = mu.Map[String, FSFunc]()
  val fileUploadCallbacks = mu.Map[String, FSFileUpload]()
  val fileDownloadCallbacks = mu.Map[String, FSFileDownload]()

  val rootFSContext =
    new FSContext(session, this, onPageUnload = onPageUnload, debugLbl = Some("page_root_context"))

  def deleteOlderThan(ts: Long): Unit =
    val callbacksToRemove = callbacks.filter(_._2.keepAliveAt < ts)
    callbacksToRemove.foreach: (id, f) =>
      callbacks -= id
      f.fsc.functionsGenerated -= f.id
    session.fsSystem.stats.currentCallbacks.dec(callbacksToRemove.size)

    val fileUploadCallbacksToRemove = fileUploadCallbacks.filter(_._2.keepAliveAt < ts)
    fileUploadCallbacksToRemove.foreach: (id, f) =>
      fileUploadCallbacks -= id
      f.fsc.functionsFileUploadGenerated -= f.id
    session.fsSystem.stats.currentFileUploadCallbacks.dec(fileUploadCallbacksToRemove.size)

    val fileDownloadCallbacksToRemove = fileDownloadCallbacks.filter(_._2.keepAliveAt < ts)
    fileDownloadCallbacksToRemove.foreach: (id, f) =>
      fileDownloadCallbacks -= id
      f.fsc.functionsFileDownloadGenerated -= f.id
    session.fsSystem.stats.currentFileDownloadCallbacks.dec(fileDownloadCallbacksToRemove.size)

  def delete(): Unit =
    callbacks.filterInPlace: (_, f) =>
      f.fsc.functionsGenerated -= f.id
      false
    session.fsSystem.stats.currentCallbacks.dec(callbacks.size)

    fileUploadCallbacks.filterInPlace: (_, f) =>
      f.fsc.functionsFileUploadGenerated -= f.id
      false
    session.fsSystem.stats.currentFileUploadCallbacks.dec(fileUploadCallbacks.size)

    fileDownloadCallbacks.filterInPlace: (_, f) =>
      f.fsc.functionsFileDownloadGenerated -= f.id
      false
    session.fsSystem.stats.currentFileDownloadCallbacks.dec(fileDownloadCallbacks.size)

  def fsPageScript(openWSSessionAtStart: Boolean = false)(implicit fsc: FSContext): Js =
    Js {
      s"""window._fs = {
         |  sessionId: ${Js.asJsStr(session.id).cmd},
         |  pageId: ${Js.asJsStr(id).cmd},
         |  callback: function(arg, pageId, funcId, ignoreErrors, async, expectReturn) {
         |    const xhr = new XMLHttpRequest();
         |    xhr.open("POST", "/${session.fsSystem.FSPrefix}/cb/"+pageId+"/"+funcId+"?time=" + new Date().getTime() + (ignoreErrors ? "&ignore_errors=true" : ""), async);
         |    xhr.setRequestHeader("Content-type", "text/plain;charset=utf-8");
         |    if (expectReturn) {
         |      xhr.onload = function() { try {eval(this.responseText);} catch(err) { console.log(err.message); console.log('While runnning the code:\\n' + this.responseText); } };
         |    }
         |    xhr.send(arg);
         |  },
         |  keepAlive: function(pageId) {
         |    const xhr = new XMLHttpRequest();
         |    xhr.open("POST", "/${session.fsSystem.FSPrefix}/ka/"+pageId+"?time=" + new Date().getTime(), true);
         |    xhr.setRequestHeader("Content-type", "text/plain;charset=utf-8");
         |    xhr.onload = function() { try {eval(this.responseText);} catch(err) { console.log(err.message); console.log('While runnning the code:\\n' + this.responseText); } };
         |    xhr.send('');
         |  },
         |  initWebSocket: function() {
         |    if(!window._fs.ws) {
         |      window._fs.ws = new WebSocket(location.origin.replace(/^http/, 'ws') + "/${session.fsSystem.FSPrefix}/ws?sessionId=${URLEncoder.encode(fsc.session.id, "UTF-8")}&pageId=${URLEncoder.encode(fsc.page.id, "UTF-8")}", "fs");
         |      window._fs.ws.onmessage = function(event) {
         |        try {eval(event.data);} catch(err) { console.log(err.message); console.log('While runnning the code:\\n' + event.data); }
         |      };
         |      window._fs.ws.onclose = function(){
         |        window._fs.ws = null
         |        setTimeout(window._fs.initWebSocket, 1000);
         |      };
         |    }
         |  },
         |};
         |${if openWSSessionAtStart then initWebSocket().cmd else Js.void.cmd}
         |${if periodicKeepAliveEnabled then setupKeepAlive().cmd else Js.void.cmd}
         |""".stripMargin
    }

  def isDefunct_? =
    periodicKeepAliveEnabled && (System.currentTimeMillis() - autoKeepAliveAt) > periodicKeepAliveDefunctAfter

  def isAlive_? = !isDefunct_?

  def setupKeepAlive(): Js = Js:
    s"""function sendKeepAlive() {window._fs.keepAlive(${Js.asJsStr(id).cmd});setTimeout(sendKeepAlive, ${periodicKeepAlivePeriod});};sendKeepAlive();"""

  def initWebSocket() = Js("window._fs.initWebSocket();")

abstract class FSSessionVar[T](default: => T):
  def apply()(implicit hasSession: FSHasSession): T = hasSession.session
    .getDataOpt(this)
    .getOrElse:
      hasSession.session.setData(this, default)
      apply()

  def update(value: T)(implicit hasSession: FSHasSession): Unit = hasSession.session.setData(this, value)

abstract class FSSessionVarOpt[T]():
  def apply()(implicit hasSession: FSHasSession): Option[T] = hasSession.session.getDataOpt[T](this)

  def update(value: T)(implicit hasSession: FSHasSession): Unit = hasSession.session.setData(this, value)

  def setOrClear(valueOpt: Option[T])(implicit hasSession: FSHasSession): Unit = valueOpt match
    case Some(value) => update(value)
    case None => clear()

  def clear()(implicit hasSession: FSHasSession): Unit = hasSession.session.clear(this)

class FSAnonymousPage[Env <: FSXmlEnv](
  using val env: Env
)(
  val id: String,
  val session: FSSession,
  val render: FSContext => env.NodeSeq,
  val createdAt: Long = System.currentTimeMillis(),
  var keepAliveAt: Long = System.currentTimeMillis(),
  var debugLbl: Option[String] = None,
) extends FSHasSession:
  def renderAsString(implicit fsc: FSContext): String = env.render(render(fsc))

  def keepAlive(): Unit =
    keepAliveAt = System.currentTimeMillis()
    session.keepAlive()

  def allKeepAlivesIterable: Iterable[Long] = Iterable(keepAliveAt)

  def onPageUnload(): Js =
    // session.anonymousPages.remove(id)
    Js.void

trait FSHasSession:
  def session: FSSession

object FSSession:
  val logger = LoggerFactory.getLogger(getClass.getName)

class FSSession(
  val id: String,
  val fsSystem: FSSystem,
  data: mu.Map[Any, Any] = mu.Map.empty[Any, Any],
  val createdAt: Long = System.currentTimeMillis(),
  var keepAliveAt: Long = System.currentTimeMillis(),
  val debugLbl: Option[String] = None,
) extends FSHasSession:
  val config = ConfigFactory.load()

  def useRandomIds: Boolean = Option(config.getBoolean("com.fastscala.core.random-ids")).getOrElse(false)

  import FSSession.logger

  private var idSeq = 0L

  private lazy val xoRoShiRo128PlusRandom = new XoRoShiRo128PlusRandom()

  def nextID(prefix: String = ""): String =
    if useRandomIds then prefix + java.lang.Long.toHexString(xoRoShiRo128PlusRandom.nextLong())
    else
      session.synchronized:
        session.idSeq += 1
        prefix + session.idSeq.toString

  override def session: FSSession = this

  def keepAlive(): Unit =
    keepAliveAt = System.currentTimeMillis()

  def allKeepAlivesIterable: Iterable[Long] =
    pages.values.flatMap(_.allKeepAlivesIterable) ++ Iterable(keepAliveAt)

  //  override def finalize(): Unit =
  //    super.finalize()
  //    FSSession.logger.trace(s"GC SESSION lived ${((System.currentTimeMillis() - createdAt) / 1000d).formatted("%.2f")}s session_id=$id #pages=${pages.keys.size} #anonymous_pages=${anonymousPages.keys.size}, evt_type=gc_session")

  val pages = mu.Map[String, FSPage]()
  val anonymousPages = mu.Map[String, FSAnonymousPage[?]]()

  def nPages(): Int = pages.size

  def getData[T](key: Any): T = getDataOpt(key).get

  def getDataOpt[T](key: Any): Option[T] = data.get(key).map(_.asInstanceOf[T])

  def setData[T](key: Any, value: T): Unit = data(key) = value

  def clear[T](key: Any): Unit = data.remove(key)

  def createPage[T](
    generatePage: FSContext => T,
    debugLbl: Option[String] = None,
    onPageUnload: () => Js = () => Js.void,
  )(implicit req: Request
  ): T =
    try
      session.fsSystem.gc()
      val copy = new Request.Wrapper(req)
      val page = new FSPage(nextID(), this, copy, onPageUnload = onPageUnload, debugLbl = debugLbl)

      if logger.isTraceEnabled then logger.trace(s"Created page ${page.id}")
      fsSystem.stats.event(StatEvent.CREATE_PAGE)(using fsSystem, Some(this), None, None)
      session.fsSystem.stats.pagesTotal.inc()
      session.fsSystem.stats.currentPages.inc()

      this.synchronized:
        pages += (page.id -> page)
      generatePage(page.rootFSContext)
    catch
      case ex: Exception =>
        ex.printStackTrace()
        throw ex

  def deletePages(toDelete: Set[FSPage]): Unit =
    val currentPages = pages.values.toSet
    val pagesToRemove = currentPages.intersect(toDelete)
    pagesToRemove.foreach: page =>
      pages -= page.id
      page.delete()
    fsSystem.stats.currentPages.dec(pagesToRemove.size)

  def deletePagesOlderThan(ts: Long): Unit =
    val pagesToRemove = pages.filter(_._2.keepAliveAt < ts)
    pagesToRemove.foreach: (id, page) =>
      pages -= id
      page.delete()
    fsSystem.stats.currentPages.dec(pagesToRemove.size)

    val anonymousPagesToRemove = anonymousPages.filter(_._2.keepAliveAt < ts)
    anonymousPagesToRemove.foreach(anonymousPages -= _._1)
    fsSystem.stats.currentAnonPages.dec(anonymousPagesToRemove.size)

    pages.values.foreach(_.deleteOlderThan(ts))

  def delete(): Unit =
    fsSystem.sessions -= this.id
    fsSystem.stats.currentSessions.dec()

    pages.filterInPlace: (_, page) =>
      page.delete()
      false
    fsSystem.stats.currentPages.dec(pages.size)

    anonymousPages.clear()
    fsSystem.stats.currentAnonPages.dec(anonymousPages.size)

object FSSystem:
  val logger = LoggerFactory.getLogger(getClass.getName)

class FSSystem(
  val beforeCallBackJs: Option[Js] = None,
  val afterCallBackJs: Option[FSContext => Js] = None,
  val appName: String = "app",
  val stats: FSStats = new FSStats(),
) extends RoutingHandlerNoSessionHelper:
  val config = ConfigFactory.load()

  def debug: Boolean = config.getBoolean("com.fastscala.core.debug")

  import FSSystem.logger

  val sessions: mu.Map[String, FSSession] = mu.Map[String, FSSession]()

  private given FSSystem = this

  val FSSessionIdCookieName = "fssid"

  val FSPrefix = "fs"

  def createSession(): FSSession =
    gc()
    val id = IdGen.secureId()
    val session = new FSSession(id, this)
    if FSSession.logger.isTraceEnabled then
      FSSession.logger.trace(s"Created session: session_id=${session.id}, evt_type=create_session")

    stats.event(StatEvent.CREATE_SESSION)(using this, Some(session), None, None)
    session.fsSystem.stats.sessionsTotal.inc()
    session.fsSystem.stats.currentSessions.inc()
    synchronized:
      sessions.put(session.id, session)
    session

  def inSession[T](inSession: FSSession => T)(implicit req: Request): Option[(List[HttpCookie], T)] =
    val cookies = Option(Request.getCookies(req)).getOrElse(Collections.emptyList).asScala
    cookies
      .filter(_.getName == FSSessionIdCookieName)
      .map(_.getValue)
      .flatMap(sessions.get(_))
      .headOption match
      case Some(session) => Option(Nil, inSession(session))
      case None if !req.getHttpURI.getPath.startsWith("/" + FSPrefix + "/ws") =>
        val session = createSession()
        Option(
          List(HttpCookie.build(FSSessionIdCookieName, session.id).path("/").build()),
          inSession(session),
        )
      case None => None

  def handleCallbackException(ex: Throwable): Js =
    ex.printStackTrace()
    stats.callbackErrorsTotal.inc()
    if debug then Js.alert(s"Internal error: $ex (showing because in debug mode)")
    else Js.alert(s"Internal error")

  def handleFileUploadCallbackException(ex: Throwable): Js =
    ex.printStackTrace()
    stats.fileUploadCallbackErrorsTotal.inc()
    if debug then Js.alert(s"Internal error: $ex (showing because in debug mode)")
    else Js.alert(s"Internal error")

  def handleFileDownloadCallbackException(ex: Throwable): Response =
    ex.printStackTrace()
    stats.fileDownloadCallbackErrorsTotal.inc()
    if debug then ServerError.InternalServerError(s"Internal error: $ex (showing because in debug mode)")
    else ServerError.InternalServerError

  override def handlerNoSession(response: JettyServerResponse, callback: Callback)(implicit req: Request)
    : Option[Response] =
    val cookies = Option(Request.getCookies(req)).getOrElse(Collections.emptyList).asScala
    import RoutingHandlerHelper.*

    val sessionIdOpt = cookies
      .find(_.getName == FSSessionIdCookieName)
      .map(_.getValue)
      .orElse(
        Option(Request.getParameters(req).getValue(FSSessionIdCookieName)).filter(_ != "")
      )
    val sessionOpt = sessionIdOpt.flatMap(sessionId => sessions.get(sessionId))
// format: off
    Some(req).collect:
      // Keep alive:
      case Post(FSPrefix, "ka", pageId) =>
        sessionOpt.map: session =>
            session.pages.get(pageId).map: page =>
                stats.keepAliveInvocationsTotal.inc()
                page.autoKeepAliveAt = System.currentTimeMillis()
                Ok.js(Js.void)
              .getOrElse:
                Ok.js(onKeepAliveNotFound(Missing.Page))
          .getOrElse:
            Ok.js(onKeepAliveNotFound(Missing.Session))
      // Callback invocation:
      case Post(FSPrefix, "cb", pageId, funcId) =>
        sessionOpt.map: session =>
            session.pages.get(pageId).map: page =>
                page.callbacks.get(funcId).map: fsFunc =>
                    fsFunc.keepAlive()
                    val start = System.currentTimeMillis()
                    stats.callbackInvocationsTotal.inc()
                    stats.event(StatEvent.USE_CALLBACK, "func_name" -> fsFunc.fullPath)
                      (using this, Some(session), Some(page), Some(fsFunc.fsc))

                    Content.Source.asStringAsync(req, Request.getCharset(req)).whenComplete: (arg, failure) =>
                      val rslt: Js = Option(failure) match
                        case Some(failure) =>
                          handleCallbackException(failure)
                        case None =>
                          val start = System.currentTimeMillis()
                          try
                            stats.callbacksInProcessing.inc()
                            if logger.isTraceEnabled then
                              logger.trace(s"Running callback on thread ${Thread.currentThread()}...")
                            val rslt = transformCallbackResponse(fsFunc.func(arg), fsFunc, page)
                            if logger.isTraceEnabled then
                              logger.trace:
                                s"Finished in ${System.currentTimeMillis() - start}ms (thread ${Thread.currentThread()}) with result JS with ${rslt.cmd.length} chars"
                            rslt
                          catch
                            case trowable =>
                              if logger.isTraceEnabled then
                                logger.trace:
                                  s"Finished with EXCEPTION in ${System.currentTimeMillis() - start}ms (thread ${Thread.currentThread()}): $trowable"
                              handleCallbackException(trowable)
                          finally
                            stats.callbackTimeTotal.inc(PUnit.millisToSeconds(System.currentTimeMillis() - start))
                            stats.callbacksInProcessing.dec()
                      if logger.isTraceEnabled then
                        logger.trace:
                          s"Invoke func: session_id=${session.id}, page_id=$pageId, func_id=$funcId, took_ms=${System.currentTimeMillis() - start}, response_size_bytes=${rslt.cmd.getBytes.size}, evt_type=invk_func"
                      Ok.js(rslt).respond(response, callback)
                    VoidResponse
                  .getOrElse:
                    Ok.js(onCallbackNotFound(Missing.CallbackFunction))
              .getOrElse:
                Ok.js(onCallbackNotFound(Missing.Page))
          .getOrElse:
            Ok.js(onCallbackNotFound(Missing.Session))
      // File uploads:
      case Post(FSPrefix, "file-upload", pageId, funcId) =>
        sessionOpt.map: session =>
            session.pages.get(pageId).map: page =>
                page.fileUploadCallbacks.get(funcId).map: fSFileUpload =>
                    fSFileUpload.keepAlive()
                    val start = System.currentTimeMillis()
                    stats.fileUploadCallbackInvocationsTotal.inc()

                    val contentType = req.getHeaders.get(HttpHeader.CONTENT_TYPE)
                    if MimeTypes.Type.MULTIPART_FORM_DATA == MimeTypes.getBaseType(contentType) then
                      // Extract the multipart boundary.
                      val boundary = MultiPart.extractBoundary(contentType)
                      // Create and configure the multipart parser.
                      val parser = new MultiPartFormData.Parser(boundary)
                      // By default, uploaded files are stored in this directory, to
                      // avoid to read the file content (which can be large) in memory.
                      parser.setFilesDirectory(Path.of(System.getProperty("java.io.tmpdir")))
                      // Convert the request content into parts.
                      parser.parse(req).whenComplete: (parts, failure) =>
                        val rslt: Js = Option(failure) match
                          case Some(failure) =>
                            handleFileUploadCallbackException(failure)
                          case None =>
                            stats.fileUploadCallbacksInProcessing.inc()
                            fSFileUpload.func:
                              parts.asScala
                                .map: part =>
                                  try
                                    new FSUploadedFile(
                                      name = part.getName,
                                      submittedFileName = part.getFileName,
                                      contentType = part.getHeaders.get(HttpHeader.CONTENT_TYPE),
                                      content = Content.Source.asByteBuffer(part.getContentSource).array,
                                    )
                                  catch
                                    case ex: Throwable =>
                                      handleFileUploadCallbackException(ex)
                                      throw ex
                                  finally
                                    stats.fileUploadCallbackTimeTotal.inc(
                                      PUnit.millisToSeconds(System.currentTimeMillis() - start)
                                    )
                                    stats.fileUploadCallbacksInProcessing.dec()
                                    try
                                      part.close()
                                    finally
                                      part.delete()
                                .toSeq
                        Ok.js(rslt).respond(response, callback)
                      VoidResponse
                    else Ok.js(Js.alert("Not multipart form data"))
                  .getOrElse:
                    Ok.js(onFileUploadNotFound(Missing.FileUploadFunction))
              .getOrElse:
                Ok.js(onFileUploadNotFound(Missing.Page))
          .getOrElse:
            Ok.js(onFileUploadNotFound(Missing.Session))
      // File downloads:
      case Get(FSPrefix, "file-download", pageId, funcId, _) =>
        sessionOpt.map: session =>
            session.pages.get(pageId).map: page =>
                page.fileDownloadCallbacks.get(funcId).map: fSFileDownload =>
                    fSFileDownload.keepAlive()
                    val start = System.currentTimeMillis()
                    stats.fileDownloadCallbackInvocationsTotal.inc()

                    try
                      stats.fileDownloadCallbacksInProcessing.inc()
                      val bytes = fSFileDownload.func()
                      Ok.binaryWithContentType(bytes, fSFileDownload.contentType)
                    catch case ex: Exception => handleFileDownloadCallbackException(ex)
                    finally
                      stats.fileDownloadCallbackTimeTotal.inc(
                        PUnit.millisToSeconds(System.currentTimeMillis() - start)
                      )
                      stats.fileDownloadCallbacksInProcessing.dec()
                  .getOrElse:
                    onFileDownloadNotFound(Missing.FileDownloadFunction)
              .getOrElse:
                onFileDownloadNotFound(Missing.Page)
          .getOrElse:
            onFileDownloadNotFound(Missing.Session)
      // Anonymous pages:
      case Get(FSPrefix, "anon", anonymousPageId, _) =>
        sessionOpt.map: session =>
            session.anonymousPages.get(anonymousPageId).map: anonymousPage =>
                anonymousPage.keepAlive()
                val nodeSeq = session.createPage(
                  implicit fsc => anonymousPage.renderAsString(),
                  onPageUnload = () => anonymousPage.onPageUnload(),
                  debugLbl = Some(
                    s"page for anon page ${anonymousPage.debugLbl.getOrElse(s"with id ${anonymousPage.id}")}"
                  ),
                )
                Ok(nodeSeq)
              .getOrElse:
                onAnonymousPageNotFound(Missing.AnonPage)
          .getOrElse:
            onAnonymousPageNotFound(Missing.Session)
// format: on

  def onKeepAliveNotFound(missing: Missing.Value)(implicit req: Request): Js =
    missing.updateStats()
    Js.void

  def onCallbackNotFound(missing: Missing.Value)(implicit req: Request): Js =
    missing.updateStats()
    if Option(Request.getParameters(req).getValue("ignore_errors")).map(_ == "true").getOrElse(false) then
      Js.void
    else Js.confirm(s"Page has expired, please reload", Js.reload())

  def onFileUploadNotFound(missing: Missing.Value)(implicit req: Request): Js =
    missing.updateStats()
    if Option(Request.getParameters(req).getValue("ignore_errors")).map(_ == "true").getOrElse(false) then
      Js.void
    else Js.confirm(s"Page has expired, please reload", Js.reload())

  def onFileDownloadNotFound(missing: Missing.Value)(implicit req: Request): Response =
    missing.updateStats()
    Redirect.temporaryRedirect("/")

  def onAnonymousPageNotFound(missing: Missing.Value)(implicit req: Request): Response =
    missing.updateStats()
    Redirect.temporaryRedirect("/")

  def onWebsocketNotFound(missing: Missing.Value)(implicit wsSession: Session): Js =
    missing.updateStats()
    if wsSession.getUpgradeRequest.getParameterMap.asScala
          .get("ignore_errors")
          .flatMap(_.asScala.headOption)
          .map(_ == "true")
          .getOrElse(false)
    then Js.void
    else Js.confirm(s"Page has expired, please reload", Js.reload())

  def transformCallbackResponse(resp: Js, fsFunc: FSFunc, page: FSPage): Js = resp

  def transformFileUploadCallbackResponse(js: Js)(implicit fsc: FSContext): Js = js

  def gc(): Unit = synchronized:
    val minFreeSpacePercent = config.getDouble("com.fastscala.core.gc.run-when-less-than-mem-free-percent")

    if (Runtime.getRuntime.freeMemory.toDouble / Runtime.getRuntime.totalMemory * 100) < minFreeSpacePercent
    then
      System.gc()
      if (Runtime.getRuntime.freeMemory.toDouble / Runtime.getRuntime.totalMemory * 100) < minFreeSpacePercent
      then
        if logger.isTraceEnabled then
          logger.trace:
            s"Less than ${minFreeSpacePercent.formatted("%.2f%%")} space available, freeing up space..."
        freeUpSpace()
        gc()

  def freeUpSpace(): Unit = synchronized:
    val start = System.currentTimeMillis()

    val percentOfPagesToDelete: Double = config.getDouble("com.fastscala.core.gc.percent-of-pages-to-delete")

    val pagesSortedByInterest =
      sessions.flatMap(_._2.pages.values).toSeq.sortBy(p => (p.isAlive_?, p.keepAliveAt))

    val pagesToDelete: Set[FSPage] = pagesSortedByInterest
      .take(math.max(1, (percentOfPagesToDelete / 100.0) * pagesSortedByInterest.size).toInt)
      .toSet

    // Delete sessions with no more pages:
    val sessionsToDelete = sessions.values
      .filter(s => s.pages.isEmpty || s.pages.values.forall(p => pagesToDelete.contains(p)))
      .toSet
    sessionsToDelete.foreach(_.delete())

    // Delete pages:
    pagesToDelete
      .groupBy(_.session)
      .foreach:
        case (session, toDelete) if !sessionsToDelete.contains(session) => session.deletePages(toDelete)
        case _ =>
    stats.gcTimeTotal.inc(PUnit.millisToSeconds(System.currentTimeMillis() - start))
    stats.gcRunsTotal.inc()
