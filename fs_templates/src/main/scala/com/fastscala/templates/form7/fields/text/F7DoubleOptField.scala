package com.fastscala.templates.form7.fields.text

import java.text.DecimalFormat
import java.util.regex.Pattern

import scala.xml.NodeSeq

import com.fastscala.templates.form7.F7Field
import com.fastscala.templates.form7.mixins.*
import com.fastscala.templates.form7.renderers.*
import com.fastscala.xml.scala_xml.FSScalaXmlEnv

class F7DoubleOptField()(implicit renderer: TextF7FieldRenderer)
    extends F7TextField[Option[Double]]
       with F7FieldWithPrefix
       with F7FieldWithSuffix
       with F7FieldWithMin
       with F7FieldWithStep
       with F7FieldWithMax:
  override def _inputTypeDefault: String = "number"

  override def defaultValue: Option[Double] = None

  def toString(value: Option[Double]): String = value
    .map(value => prefix + " " + new DecimalFormat("0.#").format(value) + " " + suffix)
    .map(_.trim)
    .getOrElse("")

  def fromString(str: String): Either[String, Option[Double]] =
    if str.trim == "" then Right(None)
    else
      str.toLowerCase.trim
        .replaceAll("^" + Pattern.quote(prefix.toLowerCase) + " *", "")
        .replaceAll(" *" + Pattern.quote(suffix.toLowerCase) + "$", "")
        .toDoubleOption match
        case Some(value) => Right(Some(value))
        case None => Left(s"Not a double?: $str")

  override def validate(): Seq[(F7Field, NodeSeq)] = super.validate() ++
    (if required && currentValue.isEmpty then
       Seq((this, FSScalaXmlEnv.buildText(renderer.defaultRequiredFieldLabel)))
     else Seq())
