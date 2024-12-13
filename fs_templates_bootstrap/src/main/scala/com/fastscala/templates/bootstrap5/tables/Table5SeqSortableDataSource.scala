package com.fastscala.templates.bootstrap5.tables

import scala.util.chaining.scalaUtilChainingOps

trait Table5SeqSortableDataSource extends Table5SeqDataSource with Table5Sortable:
  def seqRowsSource: Seq[R]

  def rowsSorter: PartialFunction[C, Seq[R] => Seq[R]] =
    columns().foldLeft(PartialFunction.empty): (pf, col) =>
      col match
        case ColStr(title, render) =>
          pf.orElse:
            case `col` => _.sortBy(render)
        case _ => pf

  def isSortable(col: C): Boolean = rowsSorter.isDefinedAt(col)

  override def rows(hints: Seq[RowsHint]): Seq[R] =
    seqRowsSource
      .pipe: rows =>
        hints
          .collectFirst:
            case hint: SortingRowsHint[?] => hint
          .collect:
            case SortingRowsHint(sortCol: C @unchecked, ascending) if rowsSorter.isDefinedAt(sortCol) =>
              val sorted = rowsSorter.apply(sortCol)(rows)
              if ascending then sorted else sorted.reverse
          .getOrElse(rows)
      .pipe: rows =>
        hints
          .collectFirst:
            case hint: PagingRowsHint => hint
          .map:
            case PagingRowsHint(offset, limit) => rows.drop(offset.toInt).take(limit.toInt)
          .getOrElse(rows)
