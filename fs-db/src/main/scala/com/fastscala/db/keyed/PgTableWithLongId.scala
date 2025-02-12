package com.fastscala.db.keyed

import java.lang.reflect.Field

import scalikejdbc.*

import com.fastscala.db.{ PgTable, TableWithId }

trait PgTableWithLongId[R <: RowWithLongId[R]] extends PgTable[R] with TableWithId[R, java.lang.Long]:
  override def createSampleRowInternal(): R =
    val ins = super.createSampleRowInternal()
    if ins == null then ins.id = 0
    ins

  override def insertFields: List[Field] = fieldsList.filter(_.getName != "id")

  override def updateFields: List[Field] = fieldsList.filter(_.getName != "id")

  override def fieldTypeToSQLType(
    field: java.lang.reflect.Field,
    clas: Class[?],
    value: => Any,
    columnConstrains: Set[String] = Set("not null"),
  ): String =
    if field.getName == "id" then "bigserial primary key not null"
    else super.fieldTypeToSQLType(field, clas, value, columnConstrains)

  def getForIds(id: java.lang.Long*): List[R] = select(sqls""" where id = $id""")

  def getForIdOpt(key: java.lang.Long): Option[R] = select(sqls""" where id = $key""").headOption
