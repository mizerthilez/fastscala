package com.fastscala.js

import java.util.Date

import org.apache.commons.text.StringEscapeUtils
import org.eclipse.jetty.http.{ HttpHeader, MimeTypes }
import org.eclipse.jetty.io.Content
import org.eclipse.jetty.server.Response
import org.eclipse.jetty.util.BufferUtil

import com.fastscala.core.{ FSContext, FSXmlEnv }
import com.fastscala.js.rerenderers.*
import com.fastscala.utils.IdGen

trait Js:
  def cmd: String

  def cmdEscaped: String =
    println("IN: " + cmd)
    println("OUT: " + "\"" + StringEscapeUtils.escapeEcmaScript(cmd.replaceAll("\n", "")) + "\"")
    "'" + StringEscapeUtils.escapeEcmaScript(cmd.replaceAll("\n", "")) + "'"

  def &(js: Js) = RawJs(cmd + ";" + js.cmd)

  def onDOMContentLoaded: Js = Js:
    s"""if (/complete|interactive|loaded/.test(document.readyState)) {$cmd}
       |else { document.addEventListener('DOMContentLoaded', function() {$cmd}, false); }""".stripMargin

  def writeTo(
    resp: Response,
    status: Int = 200,
    contentType: String = "application/javascript; charset=utf-8",
  ): Unit =
    resp.setStatus(status)
    resp.getHeaders.put(HttpHeader.CONTENT_TYPE, contentType)
    val charsetName = Option(MimeTypes.getCharsetFromContentType(contentType)).getOrElse("UTF-8")
    Content.Sink.write(resp, true, BufferUtil.toBuffer(cmd.getBytes(charsetName)))

  override def toString: String = cmd

case class RawJs(js: String) extends Js:
  override def cmd: String = js

object JsOps:
  implicit class RichJs(js: Js):
    def `_==`(other: Js): Js = Js(s"${js.cmd} == ${other.cmd}")

    def `_!=`(other: Js): Js = Js(s"${js.cmd} != ${other.cmd}")

    def `_===`(other: Js): Js = Js(s"${js.cmd} === ${other.cmd}")

    def `_!==`(other: Js): Js = Js(s"${js.cmd} !== ${other.cmd}")

    def `_>`(other: Js): Js = Js(s"${js.cmd} > ${other.cmd}")

    def `_>=`(other: Js): Js = Js(s"${js.cmd} >= ${other.cmd}")

    def `_<`(other: Js): Js = Js(s"${js.cmd} < ${other.cmd}")

    def `_<=`(other: Js): Js = Js(s"${js.cmd} <= ${other.cmd}")

    def `_&&`(other: Js): Js = Js(s"${js.cmd} && ${other.cmd}")

    def `_||`(other: Js): Js = Js(s"${js.cmd} || ${other.cmd}")

    def `_=`(other: Js): Js = Js(s"${js.cmd} = ${other.cmd};")

trait JsUtils:
  import JsOps.*

  def evalIf(cond: Boolean)(js: => Js): Js = if cond then js else this.void

  def _if(cond: Js, _then: Js, _else: Js = this.void): Js = Js(
    s"if(${cond.cmd}) {${_then}} else {${_else}}"
  )

  def _trenaryOp(cond: Js, _then: Js, _else: Js = this.void): Js = Js(
    s"((${cond.cmd}) ? (${_then}) : (${_else}))"
  )

  def void: Js = Js("")

  def log(js: Js): Js = Js(s"""console.log(eval("${escapeStr(js.cmd)}"));""")

  def void[T](code: () => T): Js =
    code()
    void

  val _null: Js = Js("null")
  val _true: Js = Js("true")
  val _false: Js = Js("false")

  def fromString(s: String): Js = Js(s)

  def escapeStr(s: String) = StringEscapeUtils.escapeEcmaScript(s)

  def asJsStr(s: String): Js = Js(s""""${escapeStr(s)}"""")

  def elementById(id: String) = Js(s"""document.getElementById("${escapeStr(id)}")""")

  def elementValueById(id: String) = Js(s"""document.getElementById("${escapeStr(id)}").value""")

  def checkboxIsCheckedById(id: String) = Js(
    s"""document.getElementById("${escapeStr(id)}").checked"""
  )

  def selectedValues(elem: Js) = Js(
    s"""Array.from(${elem.cmd}.querySelectorAll("option:checked"),e=>e.value)"""
  )

  def withVarStmt(name: String, value: Js)(code: Js => Js) = Js(
    s"""(function ($name) {${code(Js(name)).cmd}})(${value.cmd});"""
  )

  def withVarExpr(name: String, value: Js)(code: Js => Js) = Js(
    s"""(function ($name) {return ${code(Js(name)).cmd};})(${value.cmd});"""
  )

  def valueOrElse(value: Js, default: Js) = withVarExpr("value", value): value =>
    this._trenaryOp(value `_!=` _null, _then = value, _else = default)

  def wrapAsExpr(stmt: Js*)(expr: Js) = Js(
    s"""((function () {${stmt.reduceOption(_ & _).getOrElse(this.void).cmd}; return ${expr.cmd};})())"""
  )

  def varOrElseUpdate(variable: Js, defaultValue: Js) =
    this._trenaryOp(
      variable `_!=` _null,
      _then = variable,
      _else = wrapAsExpr(variable.`_=`(defaultValue))(variable),
    )

  def checkboxIsChecked(id: String) = Js(s"""document.getElementById("${escapeStr(id)}").checked""")

  def alert(text: String): Js = Js(s"""alert("${escapeStr(text)}");""")

  def toClipboard(text: String): Js = Js(s"navigator.clipboard.writeText('${escapeStr(text)}');")

  def consoleLog(js: Js): Js = Js(s"""console.log(${js.cmd});""")

  def consoleLog(str: String): Js = this.consoleLog(this.asJsStr(str))

  def confirm(text: String, js: Js) = Js(s"""if(confirm("${escapeStr(text)}")) {${js.cmd}};""")

  def redirectTo(link: String): Js = Js(s"""window.location.href = "${escapeStr(link)}";""")

  def reloadPageWithQueryParam(key: String, value: String): Js = Js:
    s"""var searchParams = new URLSearchParams(window.location.search);
       |searchParams.set(${this.asJsStr(key)}, ${this.asJsStr(value)});
       |window.location.search = searchParams.toString();""".stripMargin

  def goBack(n: Int = 1): Js = Js(s""";window.history.back();""")

  def onload(js: Js): Js = Js(s"""$$(document).ready(function() { ${js.cmd} });""")

  def onkeypress(codes: Int*)(js: Js): Js = Js(
    s"event = event || window.event; if (${codes.map(code => s"(event.keyCode ? event.keyCode : event.which) == $code").mkString(" || ")}) {${js.cmd}};"
  )

  def reload(): Js = Js(s"""location.reload();""")

  def setTimeout(js: Js, timeout: Long): Js = Js(
    s"""setTimeout(function(){ ${js.cmd} }, $timeout);"""
  )

  def removeId(id: String): Js = Js(s"""document.getElementById("$id").remove();""")

  def show(id: String): Js = Js(
    s"""document.getElementById("${escapeStr(id)}").style.display = "block";"""
  )

  def hide(id: String): Js = Js(
    s"""document.getElementById("${escapeStr(id)}").style.display = "none";"""
  )

  def focus(id: String): Js = Js(s"""document.getElementById("${escapeStr(id)}").focus();""")

  def select(id: String): Js = Js(s"""document.getElementById("${escapeStr(id)}").select();""")

  def blur(id: String): Js = Js(s"""document.getElementById("${escapeStr(id)}").blur();""")

  def addClass(id: String, clas: String): Js = Js(
    s"""document.getElementById("${escapeStr(id)}").classList.add(${this.asJsStr(clas)})"""
  )

  def removeClass(id: String, clas: String): Js = Js(
    s"""document.getElementById("${escapeStr(id)}").classList.remove(${this.asJsStr(clas)})"""
  )

  def setCookie(
    name: String,
    cookie: String,
    expires: Option[Long] = None,
    path: Option[String] = None,
  ): Js =
    Js(
      s"""document.cookie='$name=${escapeStr(cookie)};${expires.map(new Date(_)).map(_.toGMTString).map("; expires=" + _).getOrElse("")}${path.map("; path=" + _).getOrElse("")}'"""
    )

  def deleteCookie(name: String, path: String): Js =
    setCookie(name, "", expires = Some(0), path = Some(path))

object Js extends JsUtils:
  def apply(s: String): Js = RawJs(s)

object JsUtils:
  def generic[Env <: FSXmlEnv](using env: Env) = new JsXmlUtils[env.type]

class JsXmlUtils[Env <: FSXmlEnv](using val env: Env) extends JsUtils:
  def rerenderable(
    render: Rerenderer[env.type] => FSContext => env.Elem,
    idOpt: Option[String] = None,
    debugLabel: Option[String] = None,
    gcOldFSContext: Boolean = true,
  ): Rerenderer[env.type] =
    new Rerenderer(render, idOpt = idOpt, debugLabel = debugLabel, gcOldFSContext = gcOldFSContext)

  def rerenderableP[P](
    render: RerendererP[env.type, P] => FSContext => P => env.Elem,
    idOpt: Option[String] = None,
    debugLabel: Option[String] = None,
    gcOldFSContext: Boolean = true,
  ): RerendererP[env.type, P] =
    new RerendererP(render, idOpt = idOpt, debugLabel = debugLabel, gcOldFSContext = gcOldFSContext)

  def rerenderableContents(
    render: ContentRerenderer[env.type] => FSContext => env.NodeSeq,
    id: Option[String] = None,
    debugLabel: Option[String] = None,
    gcOldFSContext: Boolean = true,
  ): ContentRerenderer[env.type] =
    new ContentRerenderer(render, id = id, debugLabel = debugLabel, gcOldFSContext = gcOldFSContext)

  def rerenderableContentsP[P](
    render: ContentRerendererP[env.type, P] => FSContext => P => env.NodeSeq,
    id: Option[String] = None,
    debugLabel: Option[String] = None,
    gcOldFSContext: Boolean = true,
  ): ContentRerendererP[env.type, P] =
    new ContentRerendererP(
      render,
      id = id,
      debugLabel = debugLabel,
      gcOldFSContext = gcOldFSContext,
    )

  def append2Body(ns: env.NodeSeq): Js =
    val elemId = IdGen.id("template")
    Js(s"document.body.appendChild(${htmlToElement(ns, elemId).cmd})") &
      removeId(elemId)

  def append2(id: String, ns: env.NodeSeq): Js =
    val elemId = IdGen.id("template")
    Js(
      s"""document.getElementById("${escapeStr(id)}").appendChild(${htmlToElement(ns, elemId).cmd})"""
    ) &
      removeId(elemId)

  def prepend2(id: String, ns: env.NodeSeq): Js =
    val elemId = IdGen.id("template")
    Js(
      s"""document.getElementById("${escapeStr(id)}").insertBefore(${htmlToElement(ns, elemId).cmd}, document.getElementById("${escapeStr(id)}").firstChild)"""
    ) &
      removeId(elemId)

  def replace(id: String, by: env.NodeSeq): Js = Js(
    s"""(document.getElementById("${escapeStr(id)}") ? document.getElementById("${escapeStr(id)}").replaceWith(${htmlToElement(by).cmd}) : console.error("Element with id ${escapeStr(id)} not found"));"""
  )

  def setContents(id: String, ns: env.NodeSeq): Js = Js(
    s"""document.getElementById("${escapeStr(id)}").innerHTML = "${StringEscapeUtils.escapeEcmaScript(ns.toString())}"; """
  )

  def htmlToElement(html: env.NodeSeq, templateId: String = IdGen.id): Js = Js:
    s"""(document.body.appendChild((function htmlToElement(html) {var template = document.createElement('template');template.setAttribute("id", "$templateId");template.innerHTML = html.trim(); return template;})("${StringEscapeUtils.escapeEcmaScript(html.toString())}"))).content"""

  def forceHttps: env.Elem =
    env.buildElem("script", "type" -> "text/javascript")(
      env.buildUnparsed(
        """//<![CDATA[
          |if (location.protocol !== 'https:' && location.hostname !== 'localhost') { location.protocol = 'https:'; }
          |//]]>""".stripMargin
      )
    )

  def inScriptTag(js: Js): env.Elem =
    env.buildElem("script", "type" -> "text/javascript")(
      env.buildUnparsed(
        """
// <![CDATA[
""" + js +
          """
// ]]>
"""
      )
    )

  def showIf(b: Boolean)(ns: => env.NodeSeq): env.NodeSeq = if b then ns else env.Empty

class RichJsXmlUtils[Env <: FSXmlEnv](using val env: Env)(js: Js, utils: JsXmlUtils[env.type]):
  def inScriptTag: env.Elem = utils.inScriptTag(js)

  def printBeforeExec: Js = utils.consoleLog(js.cmd) & js
