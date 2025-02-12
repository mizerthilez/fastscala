package com.fastscala.components.bootstrap5.tables

import scala.xml.{ Elem, NodeSeq }

import com.fastscala.core.FSContext
import com.fastscala.components.bootstrap5.utils.{ BSBtn, ImmediateInputFields }
import com.fastscala.utils.{ Lazy, given }
import com.fastscala.xml.scala_xml.ScalaXmlNodeSeqUtils.MkNSFromElems

trait Table5Paginated extends Table5SeqDataSource:
  import com.fastscala.components.bootstrap5.helpers.BSHelpers.{ given, * }

  def defaultNumberOfAdditionalPagesEachSide = 3

  def defaultCurrentPage = 0

  def defaultPageSize = 100

  def visiblePageSizes: List[Int] = List(10, 20, 50, 100, 500)

  lazy val currentPageSize = Lazy(defaultPageSize)

  lazy val currentPage = Lazy(defaultCurrentPage)

  def maxPages = (seqRowsSource.size - 1) / currentPageSize()

  override def rowsHints(): Seq[RowsHint] = super.rowsHints() :+ PagingRowsHint(
    offset = currentPage() * currentPageSize(),
    limit = currentPageSize(),
  )

  def visiblePages(): List[Int] =
    (math.max(0, currentPage() - defaultNumberOfAdditionalPagesEachSide) to math.min(
      maxPages,
      currentPage() + (defaultNumberOfAdditionalPagesEachSide * 2),
    )).take(defaultNumberOfAdditionalPagesEachSide * 2 + 1)
      .toList
      .filter(_ >= 0)

  def renderPagesButtons()(implicit fsc: FSContext): Elem =
    ul.mb_3.withClass("pagination pagination-sm"):
      li.withClass("page-item"):
        BSBtn()
          .withClass("page-link")
          .lbl("«")
          .ajax: fsc ?=>
            currentPage() = math.max(0, currentPage() - 1)
            rerenderTableAround()
          .btn ++
          visiblePages()
            .map: page =>
              li.withClass(s"page-item ${if currentPage() == page then "active" else ""}"):
                BSBtn()
                  .withClass("page-link")
                  .lbl((page + 1).toString)
                  .ajax: fsc ?=>
                    currentPage() = page
                    rerenderTableAround()
                  .btn
            .mkNS ++
          li.withClass("page-item"):
            BSBtn()
              .withClass("page-link")
              .lbl("»")
              .ajax: fsc ?=>
                currentPage() = math.min(maxPages, currentPage() + 1)
                rerenderTableAround()
              .btn

  def renderPageSizeDropdown()(implicit fsc: FSContext): Elem =
    ImmediateInputFields
      .select[Int](
        () => visiblePageSizes,
        () => currentPageSize(),
        pageSize =>
          currentPageSize() = pageSize
          currentPage() = 0
          rerenderTableAround()
        ,
        classes = "form-select form-select-sm",
        style = "max-width: 200px; float: right;",
      )
      .mb_3

  def renderPaginationBottomControls()(implicit fsc: FSContext): Elem =
    row.apply:
      col.apply(renderPagesButtons()) ++
        col.apply(renderPageSizeDropdown())

  override def renderAroundContents()(implicit fsc: FSContext): NodeSeq =
    super.renderAroundContents() ++
      renderPaginationBottomControls()
