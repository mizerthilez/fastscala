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
  )(implicit renderer: TextF7FieldRenderer
  ): F7LocalDateOptField = new F7LocalDateOptField().rw(
    get.map(date => java.time.LocalDate.parse(date, DateTimeFormatter.ofPattern(pattern))),
    dateOpt => set(dateOpt.map(_.format(DateTimeFormatter.ofPattern(pattern)))),
  )

class F7LocalDateOptField()(implicit renderer: TextF7FieldRenderer)
    extends F7TextField[Option[java.time.LocalDate]]:
  override def _inputTypeDefault: String = "date"

  override def defaultValue: Option[LocalDate] = None

  def toString(value: Option[java.time.LocalDate]): String =
    value.map(_.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).getOrElse("")

  def fromString(str: String): Either[String, Option[java.time.LocalDate]] = Right(
    Some(str)
      .filter(_.trim != "")
      .map(str => java.time.LocalDate.parse(str, DateTimeFormatter.ofPattern("yyyy-MM-dd")))
  )

  override def validate(): Seq[(F7Field, NodeSeq)] = super.validate() ++
    (if required && currentValue.isEmpty then
       Seq((this, FSScalaXmlEnv.buildText(renderer.defaultRequiredFieldLabel)))
     else Seq())
