package com.fastscala.db.caching

import scala.reflect.Typeable

import org.slf4j.LoggerFactory
import scalikejdbc.interpolation.SQLSyntax

import com.fastscala.db.*
import com.fastscala.db.observable.{ DBObserver, ObservableRowBase }
import com.fastscala.db.util.Utils

class TableCache[K, R <: Row[R] & ObservableRowBase & RowWithId[K, R]: Typeable](
  val table: TableWithId[R, K],
  val loadAll: [R] => Table[R] => Seq[R] = [R] => (r: Table[R]) => r.selectAll().toVector,
  var status: CacheStatus.Value = CacheStatus.NONE_LOADED,
  val entries: collection.mutable.Map[K, R] = collection.mutable.Map[K, R](),
) extends DBObserver
       with TableCacheLike[K, R]:
  val logger = LoggerFactory.getLogger(getClass.getName)

  override def observingTables: Seq[TableBase] = Seq(table)

  def subCache(loadSubsetSQL: SQLSyntax, filterSubset: R => Boolean) =
    new TableSubCache[K, R](this, loadSubsetSQL, filterSubset)

  def valuesLoadedInCache: Seq[R] = entries.values.toSeq

  def values: Seq[R] =
    if status != CacheStatus.ALL_LOADED then
      // format: off
      Utils.time {
        loadAll(table).foreach: e =>
          if !entries.contains(e.key) then entries += (e.key -> e)
        status = CacheStatus.ALL_LOADED
      } { ms =>
        logger.trace(
          s"${table.tableName}.values: LOADED ${entries.size} entries FROM DB in ${ms}ms"
        )
      }
      // format: on
    entries.values.toList

  def select(rest: SQLSyntax): List[R] =
    table
      .select(rest)
      .map: loaded =>
        entries.get(loaded.key) match
          case Some(existing) =>
            existing.copyFrom(loaded)
            existing
          case None => loaded

  def apply(key: K): R = getForIdX(key)

  def getForIdX(key: K): R = try getForIdOptX(key).get
  catch
    case ex: java.util.NoSuchElementException =>
      println(s"Not found: key ${key} in table ${table.tableName}")
      throw ex

  def getForIdOptX(key: K): Option[R] =

    if status != CacheStatus.ALL_LOADED then this.values

    entries.get(key) match
      case Some(value) =>
        // logger.trace(s"${table.tableName}.getForIdOptX($uuid): CACHE HIT")
        Some(value)
      case None if status == CacheStatus.ALL_LOADED =>
        logger.debug(s"${table.tableName}.getForIdOptX($key): CACHE MISS (all loaded)")
        None
      case None =>
        logger.trace(s"${table.tableName}.getForIdOptX($key): CACHE MISS (getting from db...)")
        // format: off
        Utils.time(table.getForIdOpt(key)){ ms =>
          logger.trace(s"${table.tableName}.getForIdOptX($key): LOADED FROM DB in ${ms}ms")
        } match
          case Some(value) =>
            entries += value.key -> value
            if status == CacheStatus.NONE_LOADED then
              status = CacheStatus.SOME_LOADED
              logger.trace(s"${table.tableName} cache status: $status")
            Some(value)
          case None =>
            logger.trace(s"${table.tableName}.getForIdOptX($key): NOT FOUND (in db)")
            None
        // format: on

  def getForIdsX(ids: K*): List[R] =
    ids.toList.flatMap(id => table.getForIdOpt(id))

  override def preSave(table: TableBase, row: RowBase): Unit = ()

  def postSave(t: TableBase, row: RowBase): Unit = (table, row) match
    case (`table`, row: R) =>
      entries += row.key -> row
      if status == CacheStatus.NONE_LOADED then
        status = CacheStatus.SOME_LOADED
        logger.trace(s"${table.tableName} cache status: $status")
    case _ =>

  def preDelete(t: TableBase, row: RowBase): Unit = (table, row) match
    case (`table`, row: R) =>
      if status != CacheStatus.NONE_LOADED then entries -= row.key
    case _ =>

  override def postDelete(table: TableBase, row: RowBase): Unit = ()
