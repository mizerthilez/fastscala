package com.fastscala.templates.form7.fields.text

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import scala.util.Try

import com.fastscala.templates.form7.renderers.*

class F7LocalDateField(val defaultValue: LocalDate = LocalDate.now())(using TextF7FieldRenderer)
    extends F7TextField[LocalDate]:
  override def _inputTypeDefault: String = "date"

  def toString(value: LocalDate): String = value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

  def fromString(str: String): Either[String, LocalDate] =
    Try(LocalDate.parse(str, DateTimeFormatter.ofPattern("yyyy-MM-dd"))).toEither.left
      .map(_ => "Invalid input")
