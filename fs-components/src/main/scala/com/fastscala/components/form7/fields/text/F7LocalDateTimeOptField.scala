package com.fastscala.components.form7.fields.text

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import scala.xml.NodeSeq

import com.fastscala.components.form7.F7Field
import com.fastscala.components.form7.renderers.*
import com.fastscala.xml.scala_xml.FSScalaXmlEnv

object F7LocalDateTimeOptField:
  def apply(
    get: => Option[String],
    set: Option[String] => Unit,
    pattern: String = "yyyy-MM-dd'T'HH:mm",
  )(using TextF7FieldRenderer
  ): F7LocalDateTimeOptField = new F7LocalDateTimeOptField().rw(
    get.map(date => LocalDateTime.parse(date, DateTimeFormatter.ofPattern(pattern))),
    dateOpt => set(dateOpt.map(_.format(DateTimeFormatter.ofPattern(pattern)))),
  )

  def apply()(using TextF7FieldRenderer): F7LocalDateTimeOptField = new F7LocalDateTimeOptField()

class F7LocalDateTimeOptField(using renderer: TextF7FieldRenderer)
    extends F7TextFieldBase[Option[LocalDateTime]]:
  override def _inputTypeDefault: String = "datetime-local"

  def defaultValue: Option[LocalDateTime] = None

  def toString(value: Option[LocalDateTime]): String =
    value.map(_.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"))).getOrElse("")

  def fromString(str: String): Either[String, Option[LocalDateTime]] = Right(
    Some(str)
      .filter(_.trim != "")
      .map(str => LocalDateTime.parse(str, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")))
  )

  override def validate(): Seq[(F7Field, NodeSeq)] = super.validate() `++`:
    if required && currentValue.isEmpty then
      Seq((this, FSScalaXmlEnv.buildText(renderer.defaultRequiredFieldLabel)))
    else Seq()
