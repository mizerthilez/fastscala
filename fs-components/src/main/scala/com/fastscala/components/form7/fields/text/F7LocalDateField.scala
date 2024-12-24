package com.fastscala.components.form7.fields.text

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import scala.util.Try

import com.fastscala.components.form7.renderers.*

object F7LocalDateField:
  def apply(
    get: => String,
    set: String => Unit,
    pattern: String = "yyyy-MM-dd",
  )(using TextF7FieldRenderer
  ): F7LocalDateField = new F7LocalDateField().rw(
    LocalDate.parse(get, DateTimeFormatter.ofPattern(pattern)),
    date => set(date.format(DateTimeFormatter.ofPattern(pattern))),
  )

  def apply()(using TextF7FieldRenderer): F7LocalDateField = new F7LocalDateField()

class F7LocalDateField(val defaultValue: LocalDate = LocalDate.now())(using TextF7FieldRenderer)
    extends F7TextFieldBase[LocalDate]:
  override def _inputTypeDefault: String = "date"

  def toString(value: LocalDate): String = value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

  def fromString(str: String): Either[String, LocalDate] =
    Try(LocalDate.parse(str, DateTimeFormatter.ofPattern("yyyy-MM-dd"))).toEither.left
      .map(_ => "Invalid input")
