package com.fastscala.components.bootstrap5.tables

import scala.xml.Elem

import com.fastscala.core.FSContext
import com.fastscala.js.Js
import com.fastscala.utils.Lazy
import com.fastscala.xml.scala_xml.ScalaXmlElemUtils.RichElem

trait Table5Sortable extends Table5Base with Table5StandardColumns:
  def defaultSortCol: Option[C] = None

  def defaultSortAsc(col: C): Boolean = true

  def isSortable(col: C): Boolean

  lazy val currentSortCol: Lazy[Option[C]] = Lazy(defaultSortCol)
  lazy val currentSortAsc: Lazy[Boolean] = Lazy(
    currentSortCol().map(defaultSortAsc).getOrElse(true)
  )

  override def rowsHints(): Seq[RowsHint] = super.rowsHints() ++ currentSortCol()
    .map: col =>
      SortingRowsHint[C](col, currentSortAsc())
    .toSeq

  def clickedClientSide(
  )(implicit
    tableHeadRerenderer: TableHeadRerenderer,
    trRerenderer: TRRerenderer,
    thRerenderer: THRerenderer,
    columns: Seq[(String, Table5StandardColumn[R])],
    rows: Seq[(String, R)],
    colThId: String,
    col: Table5StandardColumn[R],
    tableColIdx: TableColIdx,
    fsc: FSContext,
  ): Js =
    fsc.callback: () =>
      if currentSortCol() == Some(col) then currentSortAsc() = !currentSortAsc()
      else
        currentSortCol() = Some(col)
        currentSortAsc() = defaultSortAsc(col)
      tableRenderer.rerenderer.rerender()

  override def renderTableHeadTRTH(
  )(implicit
    tableHeadRerenderer: TableHeadRerenderer,
    trRerenderer: TRRerenderer,
    thRerenderer: THRerenderer,
    columns: Seq[(String, Table5StandardColumn[R])],
    rows: Seq[(String, R)],
    colThId: String,
    col: Table5StandardColumn[R],
    tableColIdx: TableColIdx,
    fsc: FSContext,
  ): Elem =
    val elem = super.renderTableHeadTRTH()

    if elem.child.exists: e =>
          e.label == "div" `&&`:
            e.child.exists: el =>
              el.label == "input" `&&`:
                el.attribute("type").flatMap(_.headOption.map(_.text)) == Some("checkbox")
    then elem
    else
      (if isSortable(col) then
         val chevron =
           if currentSortCol() == Some(col) then
             if currentSortAsc() then "bi-chevron-double-down" else "bi-chevron-double-up"
           else "bi-chevron-expand"
         elem
           .withAppendedToContents(<i class={s"bi $chevron"} style="float: right;padding: 0;"></i>)
       else elem)
      .withAttr("onclick")(_ => clickedClientSide().cmd)
