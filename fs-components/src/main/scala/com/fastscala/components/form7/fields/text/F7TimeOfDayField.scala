package com.fastscala.components.form7.fields.text

import java.util.regex.Pattern

import scala.util.chaining.scalaUtilChainingOps
import scala.util.{ Failure, Success, Try }
import scala.xml.NodeSeq

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

import com.fastscala.components.form7.F7Field
import com.fastscala.components.form7.mixins.*
import com.fastscala.components.form7.renderers.*
import com.fastscala.xml.scala_xml.FSScalaXmlEnv

class F7TimeOfDayField(using renderer: TextF7FieldRenderer)
    extends F7TextFieldBase[Option[Int]]
       with F7FieldWithPrefix
       with F7FieldWithSuffix
       with F7FieldWithMin
       with F7FieldWithStep
       with F7FieldWithMax:
  def defaultValue: Option[Int] = None

  override def validate(): Seq[(F7Field, NodeSeq)] = super.validate() `++`:
    if required && currentValue.isEmpty then
      Seq((this, FSScalaXmlEnv.buildText(renderer.defaultRequiredFieldLabel)))
    else Seq()

  def toString(value: Option[Int]): String = value
    .map(value => DateTimeFormat.forPattern("HH:mm").print(DateTime().withTime(value / 60, value % 60, 0, 0)))
    .map(_.trim)
    .getOrElse("")

  def fromString(str: String): Either[String, Option[Int]] =
    if str.trim == "" then Right(None)
    else
      str.toLowerCase.trim
        .replaceAll("^" + Pattern.quote(prefix.toLowerCase) + " *", "")
        .replaceAll(" *" + Pattern.quote(suffix.toLowerCase) + "$", "")
        .pipe: txt =>
          Try(DateTimeFormat.forPattern("HH:mm").parseLocalTime(txt)) match
            case Failure(exception) => Left(s"Not a time?: $txt")
            case Success(parsed) => Right(Some(parsed.getHourOfDay * 60 + parsed.getMinuteOfHour))
