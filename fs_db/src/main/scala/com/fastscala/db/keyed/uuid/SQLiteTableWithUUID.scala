package com.fastscala.db

import java.util.UUID

import scalikejdbc.*

import com.fastscala.db.keyed.uuid.{ SQLiteRowWithUUID, TableWithUUIDBase }

trait SQLiteTableWithUUID[R <: SQLiteRowWithUUID[R]] extends SQLiteTable[R] with TableWithUUIDBase[R]:
  protected lazy val PlaceholderUUID = UUID.randomUUID()

  override def createSampleRowInternal(): R =
    val ins = super.createSampleRowInternal()
    if ins.uuid.isEmpty then ins.uuid = Some(PgTableWithUUID.PlaceholderUUID)
    ins

  override def fieldTypeToSQLType(
    field: java.lang.reflect.Field,
    clas: Class[?],
    value: => Any,
    columnConstrains: Set[String] = Set("not null"),
  ): String =
    if field.getName == "uuid" then
      super.fieldTypeToSQLType(field, clas, value, columnConstrains + "primary key")
    else super.fieldTypeToSQLType(field, clas, value, columnConstrains)

  def getForIdOpt(key: UUID): Option[R] = getForIds(key).headOption

  def getForIds(uuid: UUID*): List[R] =
    if uuid.isEmpty then Nil
    else
      select(
        SQLSyntax.createUnsafely(
          s""" WHERE uuid IN (${(0 until uuid.size).map(_ => "?").mkString(",")})""",
          uuid.map(_.toString),
        )
      )

object SQLiteTableWithUUID:
  val sampleUUID = UUID.fromString("11111111-1111-1111-1111-111111111111")
