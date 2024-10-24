package com.fastscala.templates.form7.fields.text

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import scala.xml.NodeSeq

import com.fastscala.templates.form7.F7Field
import com.fastscala.templates.form7.renderers.*
import com.fastscala.xml.scala_xml.FSScalaXmlEnv

object F7LocalDateOptField:
  def apply(
    get: => Option[String],
    set: Option[String] => Unit,
    pattern: String = "yyyy-MM-dd",
  )(using TextF7FieldRenderer
  ): F7LocalDateOptField = new F7LocalDateOptField().rw(
    get.map(date => LocalDate.parse(date, DateTimeFormatter.ofPattern(pattern))),
    dateOpt => set(dateOpt.map(_.format(DateTimeFormatter.ofPattern(pattern)))),
  )

  def apply()(using TextF7FieldRenderer): F7LocalDateOptField = new F7LocalDateOptField()

class F7LocalDateOptField(using renderer: TextF7FieldRenderer) extends F7TextFieldBase[Option[LocalDate]]:
  override def _inputTypeDefault: String = "date"

  def defaultValue: Option[LocalDate] = None

  def toString(value: Option[LocalDate]): String =
    value.map(_.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).getOrElse("")

  def fromString(str: String): Either[String, Option[LocalDate]] = Right(
    Some(str)
      .filter(_.trim != "")
      .map(str => LocalDate.parse(str, DateTimeFormatter.ofPattern("yyyy-MM-dd")))
  )

  override def validate(): Seq[(F7Field, NodeSeq)] = super.validate() `++`:
    if required && currentValue.isEmpty then
      Seq((this, FSScalaXmlEnv.buildText(renderer.defaultRequiredFieldLabel)))
    else Seq()
