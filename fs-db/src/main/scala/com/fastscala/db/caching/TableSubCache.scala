package com.fastscala.db.caching

import scalikejdbc.interpolation.SQLSyntax

import com.fastscala.db.observable.ObservableRowBase
import com.fastscala.db.{ Row, RowWithId }

class TableSubCache[K, R <: Row[R] & ObservableRowBase & RowWithId[K, R]](
  val cache: TableCache[K, R],
  val loadSubsetSQL: SQLSyntax,
  val filterSubset: R => Boolean,
  var status: CacheStatus.Value = CacheStatus.NONE_LOADED,
) extends TableCacheLike[K, R]:
  override def values: Seq[R] =
    if status != CacheStatus.ALL_LOADED then
      status = CacheStatus.ALL_LOADED
      cache.select(loadSubsetSQL)
    else cache.valuesLoadedInCache.filter(filterSubset)

  override def select(rest: SQLSyntax): List[R] = cache.select(rest)

  override def getForIdX(id: K): R = cache.getForIdX(id)

  override def getForIdOptX(id: K): Option[R] = cache.getForIdOptX(id)

  override def getForIdsX(ids: K*): List[R] = cache.getForIdsX(ids*)
