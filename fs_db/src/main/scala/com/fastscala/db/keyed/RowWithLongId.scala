package com.fastscala.db.keyed

import scala.reflect.Typeable

import scalikejdbc.*

import com.fastscala.db.{ Row, RowWithId, RowWithIdBase }

trait RowWithLongId[R <: RowWithLongId[R]: Typeable]
    extends Row[R]
       with RowWithIdBase
       with RowWithId[java.lang.Long, R]:
  self: R =>
  def table: PgTableWithLongId[R]

  var id: java.lang.Long = scala.compiletime.uninitialized

  override def key: java.lang.Long = id

  def isPersisted_? = id != null

  def reloadOpt(): Option[R] = table.getForIdOpt(id)

  def reload(): R = reloadOpt().get

  def save(): R =
    if isPersisted_? then update()
    else
      DB.localTx:
        implicit session => id = table.insertSQL(this).updateAndReturnGeneratedKey("id").apply()
    this

  def duplicate(): R =
    DB.localTx:
      implicit session =>
        id = null
        id = table.insertSQL(this).updateAndReturnGeneratedKey("id").apply()
    this

  def update(): Unit =
    DB.localTx:
      implicit session => table.updateSQL(this, sqls" where id = $id").execute()

  def update(upd: R => Unit): R =
    table
      .getForIdOpt(id)
      .map: row =>
        upd(row)
        row.save()
        upd(this)
    this

  def delete(): Unit =
    DB.localTx:
      implicit session => table.deleteSQL(this, sqls"where id = $id").execute()

  override def insert(): Unit =
    if isPersisted_? then throw new Exception(s"Row already inserted with id $id")
    super.insert()

  override def hashCode(): Int = id.hashCode() * 41

  override def equals(obj: Any): Boolean =
    obj match
      case obj2: R =>
        (obj2.id != null && id != null && obj2.id == id) ||
        (obj2.id == null && id == null && super.equals(obj2))
      case _ => false
