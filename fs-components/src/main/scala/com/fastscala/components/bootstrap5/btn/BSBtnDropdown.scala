package com.fastscala.components.bootstrap5.utils

import scala.xml.Elem

import com.fastscala.core.FSContext
import com.fastscala.xml.scala_xml.ScalaXmlNodeSeqUtils.MkNSFromElems

object BSBtnDropdown:
  import com.fastscala.components.bootstrap5.helpers.BSHelpers.{ given, * }

  def apply(btn: BSBtn, rightAlignedMenu: Boolean = false)(btns: BSBtn*)(implicit fsc: FSContext): Elem =
    custom(btn, rightAlignedMenu)(btns.map(btn => btn.btnLink.withClass("dropdown-item"))*)

  def custom(btn: BSBtn, rightAlignedMenu: Boolean = false)(elems: Elem*)(implicit fsc: FSContext): Elem =
    div
      .withClass("btn-group")
      .apply:
        btn.btn
          .withType("button")
          .withClass("dropdown-toggle")
          .withAttr("data-bs-toggle" -> "dropdown")
          .withAttr("aria-expanded" -> "false") ++
          ul.withClass("dropdown-menu")
            .withClassIf(rightAlignedMenu, "dropdown-menu-end")
            .apply:
              elems.map(elem => li.apply(elem.withClass("dropdown-item"))).mkNS
