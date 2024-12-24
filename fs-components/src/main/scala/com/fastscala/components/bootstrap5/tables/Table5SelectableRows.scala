package com.fastscala.components.bootstrap5.tables

import scala.util.chaining.scalaUtilChainingOps
import scala.xml.Elem

import com.fastscala.core.FSContext
import com.fastscala.js.Js
import com.fastscala.components.bootstrap5.utils.{ BSBtn, ImmediateInputFields }
import com.fastscala.xml.scala_xml.JS

trait Table5SelectableRows extends Table5Base with Table5ColsLabeled:
  import com.fastscala.components.bootstrap5.helpers.BSHelpers.{ given, * }

  lazy val allSelectedRowsEvenIfNotVisible = collection.mutable.Set[R]()

  def selectedVisibleRows: Set[R] =
    rows(rowsHints()).toSet intersect allSelectedRowsEvenIfNotVisible.toSet

  def transformSelectedRowTd(td: Elem): Elem = td.bg_primary_subtle

  override def transformTRTDElem(
    elem: Elem
  )(implicit
    tableBodyRerenderer: TableBodyRerenderer,
    trRerenderer: TRRerenderer,
    col: C,
    value: R,
    rowIdx: TableRowIdx,
    columns: Seq[(String, C)],
    rows: Seq[(String, R)],
    fsc: FSContext,
  ): Elem =
    super
      .transformTRTDElem(elem)
      .pipe(elem =>
        if allSelectedRowsEvenIfNotVisible.contains(value) then transformSelectedRowTd(elem) else elem
      )
      .pipe(elem =>
        col match
          case ColSelectRow => elem.align_middle.text_center
          case _ => elem
      )

  def onSelectedRowsChange()(implicit fsc: FSContext): Js = JS.void

  def selectAllVisibleRowsBtn: BSBtn =
    BSBtn().BtnOutlinePrimary
      .lbl(s"Select All")
      .ajax:
        implicit fsc =>
          allSelectedRowsEvenIfNotVisible.clear()
          allSelectedRowsEvenIfNotVisible ++= rows(rowsHints())
          onSelectedRowsChange() &
            rerenderTableAround()

  def clearRowSelectionBtn: BSBtn =
    BSBtn().BtnOutlinePrimary
      .lbl(s"Clear Selection")
      .ajax:
        implicit fsc =>
          allSelectedRowsEvenIfNotVisible.clear()
          onSelectedRowsChange() &
            rerenderTableAround()

  def onRowSelectionChanged(trRerenderer: TRRerenderer): Js = trRerenderer.rerenderer.rerender()

  def showAllSelectOnTH: Boolean = false

  var allSelected: Boolean = false

  val ColSelectRow = new Table5StandardColumn[R]:

    override def label: String = ""

    var selectAllChkBoxId: String = ""

    override def renderTH(
    )(implicit
      tableHeadRerenderer: TableHeadRerenderer,
      trRerenderer: TRRerenderer,
      thRerenderer: THRerenderer,
      colIdx: TableColIdx,
      pageRows: Seq[(String, R)],
      fsc: FSContext,
    ): Elem = if showAllSelectOnTH then
      val contents = ImmediateInputFields
        .checkbox(
          () => allSelected,
          selected =>
            allSelected = selected
            if selected then
              allSelectedRowsEvenIfNotVisible.clear()
              allSelectedRowsEvenIfNotVisible ++= rows(rowsHints())
            else allSelectedRowsEvenIfNotVisible.clear()
            onSelectedRowsChange() & rerenderTableAround()
          ,
          "",
        )
        .m_0
        .py_0
        .d_inline_block
      selectAllChkBoxId = contents.child.find(_.label == "input").flatMap(_.attribute("id")).get.head.text
      <th class="align-middle text-center">{contents}</th>
    else <th></th>

    override def renderTD(
    )(implicit
      tableBodyRerenderer: TableBodyRerenderer,
      trRerenderer: TRRerenderer,
      tdRerenderer: TDRerenderer,
      value: R,
      rowIdx: TableRowIdx,
      colIdx: TableColIdx,
      rows: Seq[(String, R)],
      fsc: FSContext,
    ): Elem =
      val contents = ImmediateInputFields
        .checkbox(
          () => allSelectedRowsEvenIfNotVisible.contains(value),
          selected =>
            if selected then allSelectedRowsEvenIfNotVisible += value
            else allSelectedRowsEvenIfNotVisible -= value
            onSelectedRowsChange() & onRowSelectionChanged(trRerenderer) `&`:
              if selected then Js.void
              else
                allSelected = false
                Js.setCheckboxTo(selectAllChkBoxId, Some(false))
          ,
          "",
        )
        .m_0
        .d_inline_block
      <td>{contents}</td>
