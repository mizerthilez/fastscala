package dev.ironduck.working

import scala.io.Source
import scala.util.Try
import scala.xml.NodeSeq

import com.typesafe.config.ConfigFactory

import com.fastscala.core.FSContext
import com.fastscala.templates.bootstrap5.helpers.BSHelpers
import com.fastscala.xml.scala_xml.{ JS, ScalaXmlRenderableWithFSContext }

trait BasePage extends ScalaXmlRenderableWithFSContext:
  val config = ConfigFactory.load()

  def navBarTopRight()(implicit fsc: FSContext): NodeSeq

  def renderSideMenu()(implicit fsc: FSContext): NodeSeq

  def renderPageContents()(implicit fsc: FSContext): NodeSeq

  def append2Head(): NodeSeq = NodeSeq.Empty

  def append2Body(): NodeSeq = NodeSeq.Empty

  def pageTitle: String

  def openWSSessionAtStart: Boolean = false

  /*
dev.ironduck.working.pages.include_file_in_body
   */
  def render()(implicit fsc: FSContext): NodeSeq =
    import BSHelpers.{ given, * }

    <html data-bs-theme="auto">
      <head>
        <script src="/static/assets/js/color-modes.js" crossorigin="anonymous"></script>
        <meta charset="utf-8"/>
        <meta name="viewport" content="width=device-width, initial-scale=1"/>
        <meta name="author" content={"David Antunes <david@fastscala.com>"}/>
        <title>{pageTitle}</title>
        <meta name="description" content="FastScala is a Web Framework for the Scala language that enables to quickly develop complex web flows."/>
        <link href="/static/assets/dist/css/bootstrap.min.css" rel="stylesheet"/>
        <link href="https://fastly.jsdelivr.net/npm/bootstrap-icons@1.10.2/font/bootstrap-icons.css" rel="stylesheet"/>
        <link href="/static/custom_base_page.css" rel="stylesheet"/>
        {JS.inScriptTag(fsc.fsPageScript(openWSSessionAtStart))}
        {append2Head()}
        {
      Try(config.getString("dev.ironduck.working.pages.include_file_in_head"))
        .map(Source.fromFile(_).getLines().mkString("\n"))
        .map(scala.xml.Unparsed(_))
        .getOrElse(NodeSeq.Empty)
    }
      </head>
      <body>
        <svg xmlns="http://www.w3.org/2000/svg" class="d-none">
          <symbol id="check2" viewBox="0 0 16 16">
            <path d="M13.854 3.646a.5.5 0 0 1 0 .708l-7 7a.5.5 0 0 1-.708 0l-3.5-3.5a.5.5 0 1 1 .708-.708L6.5 10.293l6.646-6.647a.5.5 0 0 1 .708 0z"/>
          </symbol>
          <symbol id="circle-half" viewBox="0 0 16 16">
            <path d="M8 15A7 7 0 1 0 8 1v14zm0 1A8 8 0 1 1 8 0a8 8 0 0 1 0 16z"/>
          </symbol>
          <symbol id="moon-stars-fill" viewBox="0 0 16 16">
            <path d="M6 .278a.768.768 0 0 1 .08.858 7.208 7.208 0 0 0-.878 3.46c0 4.021 3.278 7.277 7.318 7.277.527 0 1.04-.055 1.533-.16a.787.787 0 0 1 .81.316.733.733 0 0 1-.031.893A8.349 8.349 0 0 1 8.344 16C3.734 16 0 12.286 0 7.71 0 4.266 2.114 1.312 5.124.06A.752.752 0 0 1 6 .278z"/>
            <path d="M10.794 3.148a.217.217 0 0 1 .412 0l.387 1.162c.173.518.579.924 1.097 1.097l1.162.387a.217.217 0 0 1 0 .412l-1.162.387a1.734 1.734 0 0 0-1.097 1.097l-.387 1.162a.217.217 0 0 1-.412 0l-.387-1.162A1.734 1.734 0 0 0 9.31 6.593l-1.162-.387a.217.217 0 0 1 0-.412l1.162-.387a1.734 1.734 0 0 0 1.097-1.097l.387-1.162zM13.863.099a.145.145 0 0 1 .274 0l.258.774c.115.346.386.617.732.732l.774.258a.145.145 0 0 1 0 .274l-.774.258a1.156 1.156 0 0 0-.732.732l-.258.774a.145.145 0 0 1-.274 0l-.258-.774a1.156 1.156 0 0 0-.732-.732l-.774-.258a.145.145 0 0 1 0-.274l.774-.258c.346-.115.617-.386.732-.732L13.863.1z"/>
          </symbol>
          <symbol id="sun-fill" viewBox="0 0 16 16">
            <path d="M8 12a4 4 0 1 0 0-8 4 4 0 0 0 0 8zM8 0a.5.5 0 0 1 .5.5v2a.5.5 0 0 1-1 0v-2A.5.5 0 0 1 8 0zm0 13a.5.5 0 0 1 .5.5v2a.5.5 0 0 1-1 0v-2A.5.5 0 0 1 8 13zm8-5a.5.5 0 0 1-.5.5h-2a.5.5 0 0 1 0-1h2a.5.5 0 0 1 .5.5zM3 8a.5.5 0 0 1-.5.5h-2a.5.5 0 0 1 0-1h2A.5.5 0 0 1 3 8zm10.657-5.657a.5.5 0 0 1 0 .707l-1.414 1.415a.5.5 0 1 1-.707-.708l1.414-1.414a.5.5 0 0 1 .707 0zm-9.193 9.193a.5.5 0 0 1 0 .707L3.05 13.657a.5.5 0 0 1-.707-.707l1.414-1.414a.5.5 0 0 1 .707 0zm9.193 2.121a.5.5 0 0 1-.707 0l-1.414-1.414a.5.5 0 0 1 .707-.707l1.414 1.414a.5.5 0 0 1 0 .707zM4.464 4.465a.5.5 0 0 1-.707 0L2.343 3.05a.5.5 0 1 1 .707-.707l1.414 1.414a.5.5 0 0 1 0 .708z"/>
          </symbol>
        </svg>

        <main>
          <aside class="main-sidebar min-vh-100 h-100 offcanvas-lg offcanvas-start" id="main-sidebar">
            <div class="d-flex flex-nowrap min-vh-100 h-100">
              <div class="d-flex flex-column flex-shrink-0 p-3 text-bg-dark" style="width: 280px;">
                <div class="position-relative">
                  <a href="/" class="d-flex align-items-center mb-3 mb-md-0 me-md-auto text-white text-decoration-none">
                    <img style="width: 220px; height: 3.5em;" class="rounded" src="/static/images/logo.png"></img>
                  </a>
                  <button type="button" class="btn-close btn-close-white d-lg-none text-white position-absolute end-0 top-0" data-bs-dismiss="offcanvas" aria-label="Close" data-bs-target="#main-sidebar"></button>
                </div>
                {renderSideMenu()}
              </div>
            </div>
          </aside>

          <div class="main-content min-vh-100">
            <header class="p-3 text-bg-dark">
              <div class="d-flex flex-row align-items-center justify-content-between">
                <div>
                  <button class="d-lg-none navbar-toggler p-2" type="button" data-bs-toggle="offcanvas"
                          data-bs-target="#main-sidebar" aria-controls="main-sidebar"
                          aria-label="Toggle docs navigation">
                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" class="bi" fill="currentColor"
                         viewBox="0 0 16 16">
                      <path fill-rule="evenodd"
                            d="M2.5 11.5A.5.5 0 0 1 3 11h10a.5.5 0 0 1 0 1H3a.5.5 0 0 1-.5-.5zm0-4A.5.5 0 0 1 3 7h10a.5.5 0 0 1 0 1H3a.5.5 0 0 1-.5-.5zm0-4A.5.5 0 0 1 3 3h10a.5.5 0 0 1 0 1H3a.5.5 0 0 1-.5-.5z"></path>
                    </svg>

                    <span class="d-none fs-6 pe-1">Browse</span>
                  </button>
                </div>

                {div.d_flex.withStyle("height: 2.4em").apply(navBarTopRight())}
              </div>
            </header>

            <div class="p-3">
              {renderPageContents()}
            </div>
          </div>
        </main>

        <script src="https://code.jquery.com/jquery-2.2.4.min.js" integrity="sha256-BbhdlvQf/xTY9gja0Dq3HiwQF8LaCRTXxZKRutelT44=" crossorigin="anonymous"></script>
        <script src="/static/assets/dist/js/bootstrap.bundle.min.js"></script>
        <script src="https://fastly.jsdelivr.net/npm/feather-icons@4.28.0/dist/feather.min.js" integrity="sha384-uO3SXW5IuS1ZpFPKugNNWqTZRRglnUJK6UAZ/gxOX80nxEkN9NcGZTftn6RzhGWE" crossorigin="anonymous"></script>
        {append2Body()}
        {
      Try(config.getString("dev.ironduck.working.pages.include_file_in_body"))
        .map(Source.fromFile(_).getLines().mkString("\n"))
        .map(scala.xml.Unparsed(_))
        .getOrElse(NodeSeq.Empty)
    }
      </body>
    </html>
