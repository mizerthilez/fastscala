package com.fastscala.db.keyed.numeric

import java.lang.reflect.Field

import scalikejdbc.interpolation.SQLSyntax
import scalikejdbc.{ NoExtractor, SQL }

import com.fastscala.db.keyed.{ PgTableWithLongId, RowWithLongId }

trait TableWithLongIdSeqBacked[R <: RowWithLongId[R]] extends PgTableWithLongId[R]:
  def sequenceIdName = s"s_${tableName}_id"

  override def insertFields: List[Field] =
    (super.insertFields ::: fieldsList.filter(_.getName == "id")).distinct

  override def valueToFragment(field: Field, value: Any): SQLSyntax = if field.getName == "id" then
    SQLSyntax.createUnsafely(s"nextval('$sequenceIdName')")
  else super.valueToFragment(field, value)

  override def __createTableSQL: List[SQL[Nothing, NoExtractor]] =
    SQL(s"CREATE SEQUENCE IF NOT EXISTS \"$sequenceIdName\";") ::
      super.__createTableSQL
