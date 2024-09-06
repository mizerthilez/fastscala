package com.fastscala.demo.server

import java.nio.charset.StandardCharsets
import java.nio.file.{ Files, Paths }
import java.util.Collections

import scala.jdk.CollectionConverters.ListHasAsScala

import org.eclipse.jetty.server.{ Request, Response as JettyServerResponse }
import org.eclipse.jetty.util.Callback
import org.slf4j.LoggerFactory

import com.fastscala.core.{ FSSession, FSSystem }
import com.fastscala.demo.db.{ CurrentUser, FakeDB }
import com.fastscala.demo.docs.*
import com.fastscala.demo.docs.bootstrap.BootstrapModalPage
import com.fastscala.demo.docs.chartjs.SimpleChartjsPage
import com.fastscala.demo.docs.forms.BasicFormExamplePage
import com.fastscala.demo.docs.tables.*
import com.fastscala.server.*
import com.fastscala.xml.scala_xml.FSScalaXmlEnv

class RoutingHandler(implicit fss: FSSystem) extends RoutingHandlerHelper:
  val logger = LoggerFactory.getLogger(getClass.getName)

  import com.fastscala.server.RoutingHandlerHelper.*

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
    onlyHandleHtmlRequests:
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
          .foreach { token =>
            FakeDB.users.find(_.loginToken == token).foreach { user =>
              CurrentUser() = user
            }
          }

      FSDemoMainMenu
        .serve()
        .map(servePage[FSScalaXmlEnv.type](_))
        .orElse {
          Some(req).collect {
            case Get("demo") => servePage(new SimpleTableExamplePage())
            case Get("demo", "simple_tables") => servePage(new SimpleTableExamplePage())
            case Get("demo", "sortable_tables") => servePage(new SortableTableExamplePage())
            case Get("demo", "paginated_tables") => servePage(new PaginatedTableExamplePage())
            case Get("demo", "selectable_rows_tables") =>
              servePage(new SelectableRowsTableExamplePage())
            case Get("demo", "tables_sel_cols") => servePage(new SelectableColsTableExamplePage())
            case Get("demo", "simple_form") => servePage(new BasicFormExamplePage())
            case Get("demo", "simple_modal") => servePage(new BootstrapModalPage())

            case Get("demo", "chartjs", "simple") => servePage(new SimpleChartjsPage())
          }
        }
        .orElse(
          Some(Redirect.temporaryRedirect("/demo/"))
        )
