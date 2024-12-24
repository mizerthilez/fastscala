package com.fastscala.components.bootstrap5.tables

import com.fastscala.core.FSContext
import com.fastscala.js.Js
import com.fastscala.components.bootstrap5.utils.{ BSBtn, BSBtnDropdown }
import com.fastscala.xml.scala_xml.JS
import com.fastscala.xml.scala_xml.JS.ScalaXmlRerenderer

trait Table5SelectableRowsWithActions extends Table5SelectableRows with Table5StdColsHelper:
  def actionsForRows(rows: Set[R]): Seq[BSBtn] = Nil

  def actionsBtnToIncludeInTopDropdown: BSBtn = BSBtn().BtnPrimary.lbl("Actions")

  def actionsBtnToIncludeInRowDropdown: BSBtn = BSBtn().BtnPrimary.lbl("Actions").sm

  override def onSelectedRowsChange()(implicit fsc: FSContext): Js = super.onSelectedRowsChange() &
    actionsDropdownBtnRenderer.rerender()

  lazy val actionsDropdownBtnRenderer: ScalaXmlRerenderer = JS.rerenderable(
    rerenderer =>
      implicit fsc =>
        BSBtnDropdown(actionsBtnToIncludeInTopDropdown)(
          actionsForRows(selectedVisibleRows)*
        ),
    debugLabel = Some("actions_dropdown_btn"),
  )

  lazy val ColActionsDefault = ColNsFullTd(
    actionsBtnToIncludeInRowDropdown.titleOpt
      .map(_.toString)
      .orElse:
        actionsBtnToIncludeInRowDropdown.content.lastOption.map(_.toString)
      .getOrElse(""),
    implicit fsc => {
      case (tableBodyRerenderer, trRerenderer, tdRerenderer, elem, rowIdx, colIdx, rows) =>
        val contents = BSBtnDropdown(actionsBtnToIncludeInRowDropdown)(
          actionsForRows(Set(elem))*
        )
        <td class="py-2">{contents}</td>
    },
  )
