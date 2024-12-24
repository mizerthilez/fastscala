package com.fastscala.demo.docs

import java.time.LocalDate
import scala.io.Source
import scala.util.Try
import scala.xml.NodeSeq

import com.typesafe.config.ConfigFactory

import com.fastscala.core.FSContext
import com.fastscala.js.Js
import com.fastscala.components.bootstrap5.helpers.BSHelpers
import com.fastscala.components.bootstrap5.utils.BSBtn
import com.fastscala.xml.scala_xml.{ JS, ScalaXmlRenderableWithFSContext }, JS.given
import com.fastscala.utils.given

import com.fastscala.demo.db.CurrentUser

trait BasePage extends ScalaXmlRenderableWithFSContext:
  import BSHelpers.{ given, * }

  val config = ConfigFactory.load()

  def navBarTopRight()(using FSContext): NodeSeq =
    <ul class="navbar-nav">
      <li class="nav-item"> <a class="nav-link" data-lte-toggle="sidebar" href="#" role="button"> <i class="bi bi-list"></i> </a> </li>
    </ul>
    <ul class="navbar-nav ms-auto">
      <li class="nav-item"> <a class="nav-link" href="#" data-lte-toggle="fullscreen"> <i data-lte-icon="maximize" class="bi bi-arrows-fullscreen"></i> <i data-lte-icon="minimize" class="bi bi-fullscreen-exit" style="display: none;"></i> </a> </li>
      <li class="nav-item dropdown">
        <button class="btn btn-link nav-link py-2 px-0 px-lg-2 dropdown-toggle d-flex align-items-center" id="bd-theme" type="button" aria-expanded="false" data-bs-toggle="dropdown" data-bs-display="static" aria-label="Toggle theme (auto)">
          <svg class="bi my-0 opacity-50 theme-icon-active" width="1.2em" height="1.2em"> <use href="#circle-half"></use> </svg>
          <span class="d-lg-none ms-2" id="bd-theme-text">Toggle theme</span>
        </button>
        <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="bd-theme-text">
          <li>
            <button type="button" class="dropdown-item d-flex align-items-center" data-bs-theme-value="light" aria-pressed="false">
              <svg class="bi me-2 opacity-50" width="1.2em" height="1.2em"> <use href="#sun-fill"></use> </svg>
              Light
              <svg class="bi ms-auto d-none" width="1.2em" height="1.2em"> <use href="#check2"></use> </svg>
            </button>
          </li>
          <li>
            <button type="button" class="dropdown-item d-flex align-items-center" data-bs-theme-value="dark" aria-pressed="false">
              <svg class="bi me-2 opacity-50" width="1.2em" height="1.2em"> <use href="#moon-stars-fill"></use> </svg>
              Dark
              <svg class="bi ms-auto d-none" width="1.2em" height="1.2em"> <use href="#check2"></use> </svg>
            </button>
          </li>
          <li>
            <button type="button" class="dropdown-item d-flex align-items-center active" data-bs-theme-value="auto" aria-pressed="true">
              <svg class="bi me-2 opacity-50" width="1.2em" height="1.2em"> <use href="#circle-half"></use> </svg>
              Auto
              <svg class="bi ms-auto d-none" width="1.2em" height="1.2em"> <use href="#check2"></use> </svg>
            </button>
          </li>
        </ul>
      </li>
      {
      CurrentUser()
        .map: user =>
          li.withClass("nav-item"):
            BSBtn().BtnOutlineWarning
              .lbl("退出")
              .ajax:
                implicit fsc => user.logOut()
              .btn
              .ms_2
        .getOrElse(Empty)
    }
      <li class="nav-item"> <a href="https://training.fastscala.com/" class="btn btn-warning">Register for Free Training!</a> </li>
    </ul>

  def renderSideMenu()(using FSContext): NodeSeq =
    FSDemoMainMenu.render() ++ CurrentUser()
      .map: user =>
        hr ++
          p_3.d_flex.align_items_center.withClass("menu-user"):
            a(user.miniHeadshotOrPlaceholderRendered).mx_2 ++
              a(user.fullName).text_decoration_none.fw_bold.text_warning.ms_2
      .getOrElse(Empty)

  def append2Head(): NodeSeq = NodeSeq.Empty

  def append2Body(): NodeSeq = NodeSeq.Empty

  def pageTitle: String

  def openWSSessionAtStart: Boolean = false

  given LocalDate = LocalDate.now()

  lazy val pageRenderer =
    JS.rerenderableContents(rerenderer => fsc ?=> renderPageContents(), debugLabel = Some("page"))

  def rerenderPageContents(): Js = pageRenderer.rerender()

  def renderPageContents()(using FSContext): NodeSeq

  def render()(using fsc: FSContext): NodeSeq =
    <html lang="en" data-bs-theme="auto">
      <head>
        <meta charset="utf-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <meta name="author" content="Kevin Tsu &lt;xuxiang999@gmail.com&gt;" />
        <title>{pageTitle}</title>
        <link href="//cdn.jsdelivr.net/npm/@fontsource/source-sans-3@5.1.0/index.css" rel="stylesheet" crossorigin="anonymous" />
        <link href="//cdn.jsdelivr.net/npm/overlayscrollbars@2.10.0/styles/overlayscrollbars.min.css" rel="stylesheet" crossorigin="anonymous" />
        <link href="//cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet" crossorigin="anonymous" />
        <link href="//cdn.jsdelivr.net/npm/admin-lte@4.0.0-beta2/dist/css/adminlte.min.css" rel="stylesheet" crossorigin="anonymous" />
        <link href="/static/custom_base_page.css" rel="stylesheet" />
        <script src="/static/assets/js/color-modes.js" crossorigin="anonymous"></script>
        {JS.inScriptTag(fsc.fsPageScript(openWSSessionAtStart))}
        {append2Head()}
        {
      Try(config.getString("com.solantec.web.pages.include_file_in_head"))
        .map(Source.fromFile(_).getLines().mkString("\n"))
        .map(scala.xml.Unparsed(_))
        .getOrElse(NodeSeq.Empty)
    }
      </head>
      <body class="layout-fixed sidebar-expand-lg bg-body-tertiary">
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

        <div class="app-wrapper">
          <nav class="app-header navbar navbar-expand bg-body">
            <div class="container-fluid">
              {navBarTopRight()}
            </div>
          </nav>
          <aside class="app-sidebar bg-body-secondary shadow" data-bs-theme="dark">
            <div class="sidebar-brand">
              <a href="/" class="p-3 d-flex align-items-center mb-3 mb-md-0 me-md-auto text-white text-decoration-none ">
                <img src="/static/images/logo-wide.png"></img>
              </a>
            </div>
            <div class="sidebar-wrapper">
                <nav class="mt-2">
                  {renderSideMenu()}
                </nav>
            </div>
          </aside>
          <main class="app-main">
            <header class="py-1 px-2 text-bg-secondary">#Callbacks: <span id="fs_num_page_callbacks">--</span></header>
            {renderPageContents()}
          </main>
        </div>

        <script src="//code.jquery.com/jquery-2.2.4.min.js" crossorigin="anonymous"></script>
        <script src="//cdn.jsdelivr.net/npm/overlayscrollbars@2.10.0/browser/overlayscrollbars.browser.es6.min.js" crossorigin="anonymous"></script>
        <script src="//cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" crossorigin="anonymous"></script>
        <script src="//cdn.jsdelivr.net/npm/admin-lte@4.0.0-beta2/dist/js/adminlte.min.js" crossorigin="anonymous"></script>
        <script src="//cdn.jsdelivr.net/npm/feather-icons@4.29.2/dist/feather.min.js" crossorigin="anonymous"></script>
        {append2Body()}
        {
      Try(config.getString("com.solantec.web.pages.include_file_in_body"))
        .map(Source.fromFile(_).getLines().mkString("\n"))
        .map(scala.xml.Unparsed(_))
        .getOrElse(NodeSeq.Empty)
    }
        {
      JS.setContents("fs_num_page_callbacks", scala.xml.Text(fsc.page.callbacks.size.toString)).inScriptTag
    }
        <script>
        {
      scala.xml.Unparsed:
        """// <![CDATA[
            |const SELECTOR_SIDEBAR_WRAPPER = ".sidebar-wrapper";
            |const Default = {
            |    scrollbarTheme: "os-theme-light",
            |    scrollbarAutoHide: "leave",
            |    scrollbarClickScroll: true,
            |};
            |document.addEventListener("DOMContentLoaded", function () {
            |    const sidebarWrapper = document.querySelector(SELECTOR_SIDEBAR_WRAPPER);
            |    if (
            |        sidebarWrapper &&
            |        typeof OverlayScrollbarsGlobal?.OverlayScrollbars !== "undefined"
            |    ) {
            |        OverlayScrollbarsGlobal.OverlayScrollbars(sidebarWrapper, {
            |            scrollbars: {
            |                theme: Default.scrollbarTheme,
            |                autoHide: Default.scrollbarAutoHide,
            |                clickScroll: Default.scrollbarClickScroll,
            |            },
            |        });
            |    }
            |});
            |// ]]>
            |""".stripMargin
    }
        </script>
      </body>
    </html>
