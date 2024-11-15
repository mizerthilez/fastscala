package dev.ironduck.working.server

import java.nio.charset.StandardCharsets
import java.nio.file.{ Files, Paths }
import java.util.Collections

import scala.jdk.CollectionConverters.ListHasAsScala

import org.eclipse.jetty.server.{ Request, Response as JettyServerResponse }
import org.eclipse.jetty.util.Callback
import org.slf4j.LoggerFactory

import com.fastscala.core.{ FSSession, FSSystem }
import dev.ironduck.working.db.{ CurrentUser, FakeDB }
import dev.ironduck.working.*
import dev.ironduck.working.pages.BootstrapModalPage
import com.fastscala.routing.method.Get
import com.fastscala.routing.resp.{ Ok, Redirect, Response }
import com.fastscala.routing.{ FilterUtils, RoutingHandlerHelper }
import com.fastscala.xml.scala_xml.FSScalaXmlEnv

class RoutingHandler(implicit fss: FSSystem) extends RoutingHandlerHelper:
  val logger = LoggerFactory.getLogger(getClass.getName)

  override def handlerNoSession(
    response: JettyServerResponse,
    callback: Callback,
  )(implicit req: Request
  ): Option[Response] = Some(req).collect:
    case Get("loaderio-4370139ed4f90c60359531343155344a") =>
      Ok.plain("loaderio-4370139ed4f90c60359531343155344a")
    case Get(".well-known", "acme-challenge", code) =>
      val file = "/opt/certs/.well-known/acme-challenge/" + code
      logger.debug(s"Asked for file $file")
      val contents = new String(Files.readAllBytes(Paths.get(file)), StandardCharsets.UTF_8)
      logger.debug(s"Returning contents $contents")
      Ok.plain(contents)

  override def handlerInSession(
    response: JettyServerResponse,
    callback: Callback,
  )(implicit
    req: Request,
    session: FSSession,
  ): Option[Response] =
    FilterUtils.onlyHandleHtmlRequests:
      if CurrentUser().isEmpty then
        val cookies = Option(Request.getCookies(req)).getOrElse(Collections.emptyList).asScala
        cookies
          .find(_.getName == "user_token")
          .map(_.getValue)
          .filter(_.trim != "")
          .orElse(
            Option(Request.getParameters(req).getValues("user_token"))
              .getOrElse(Collections.emptyList)
              .asScala
              .headOption
              .filter(_.trim != "")
          )
          .foreach: token =>
            FakeDB.users
              .find(_.loginToken == token)
              .foreach: user =>
                CurrentUser() = user

      FSDemoMainMenu
        .serve()
        .map(servePage[FSScalaXmlEnv.type](_))
        .orElse:
          Some(req).collect:
            case Get("demo") => servePage(BootstrapModalPage())
        .orElse(
          Some(Redirect.temporaryRedirect("/demo/"))
        )
