package com.fastscala.db.caching

import scalikejdbc.interpolation.SQLSyntax

import com.fastscala.db.{ RowWithId, RowWithIdBase }

trait TableCacheLike[K, R <: RowWithIdBase & RowWithId[K, R]]:
  def values: Seq[R]

  def select(rest: SQLSyntax): List[R]

  def getForIdX(id: K): R

  def getForIdOptX(id: K): Option[R]

  def getForIdsX(ids: K*): List[R]
