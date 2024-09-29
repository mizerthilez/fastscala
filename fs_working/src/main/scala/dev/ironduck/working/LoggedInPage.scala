package dev.ironduck.working

import java.time.LocalDate

import scala.xml.NodeSeq

import com.fastscala.core.FSContext
import com.fastscala.js.Js
import com.fastscala.xml.scala_xml.JS
import com.fastscala.xml.scala_xml.given
import com.fastscala.templates.bootstrap5.utils.BSBtn

abstract class LoggedInPage() extends BasePage:
  implicit val atTime: LocalDate = LocalDate.now()

  override def navBarTopRight()(implicit fsc: FSContext): NodeSeq =
    import com.fastscala.templates.bootstrap5.classes.BSHelpers.{ given, * }
    <div class="dropdown mb-3 me-3 bd-mode-toggle">
      <button class="btn btn-link nav-link py-2 px-0 px-lg-2 dropdown-toggle d-flex align-items-center"
              id="bd-theme"
              type="button"
              aria-expanded="false"
              data-bs-toggle="dropdown"
              data-bs-display="static"
              aria-label="Toggle theme (auto)">
        <svg class="bi my-0 theme-icon-active" width="1.5em" height="1.5em"><use href="#circle-half"></use></svg>
        <span class="d-lg-none ms-2" id="bd-theme-text">Toggle theme</span>
      </button>
      <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="bd-theme-text">
        <li>
          <button type="button" class="dropdown-item d-flex align-items-center" data-bs-theme-value="light" aria-pressed="false">
            <svg class="bi me-2 opacity-50" width="1.2em" height="1.2em"><use href="#sun-fill"></use></svg>
            Light
            <svg class="bi ms-auto d-none" width="1.2em" height="1.2em"><use href="#check2"></use></svg>
          </button>
        </li>
        <li>
          <button type="button" class="dropdown-item d-flex align-items-center" data-bs-theme-value="dark" aria-pressed="false">
            <svg class="bi me-2 opacity-50" width="1.2em" height="1.2em"><use href="#moon-stars-fill"></use></svg>
            Dark
            <svg class="bi ms-auto d-none" width="1.2em" height="1.2em"><use href="#check2"></use></svg>
          </button>
        </li>
        <li>
          <button type="button" class="dropdown-item d-flex align-items-center active" data-bs-theme-value="auto" aria-pressed="true">
            <svg class="bi me-2 opacity-50" width="1.2em" height="1.2em"><use href="#circle-half"></use></svg>
            Auto
            <svg class="bi ms-auto d-none" width="1.2em" height="1.2em"><use href="#check2"></use></svg>
          </button>
        </li>
      </ul>
    </div> ++
      BSBtn().BtnOutlineWarning
        .lbl("Logout")
        .ajax:
          implicit fsc => Js.void // user.logOut()
        .btn
        .ms_2

  def renderSideMenu()(implicit fsc: FSContext): NodeSeq =
    FSDemoMainMenu.render() // ++
  //      hr ++
  //      p_3.d_flex.align_items_center.apply {
  //        a.apply(user.miniHeadshotOrPlaceholderRendered.withStyle("width: 55px;border-radius: 55px;")) ++
  //          a.text_decoration_none.fw_bold.text_warning.ms_2.apply(user.fullName)
  //      }

  lazy val pageRenderer =
    JS.rerenderableContents(rerenderer => implicit fsc => renderPageContents())

  def rerenderPageContents(): Js = pageRenderer.rerender()

  def renderPageContents()(implicit fsc: FSContext): NodeSeq
