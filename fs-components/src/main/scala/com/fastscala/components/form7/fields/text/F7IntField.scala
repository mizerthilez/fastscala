package com.fastscala.components.form7.fields.text

import java.util.regex.Pattern

import com.fastscala.components.form7.mixins.*
import com.fastscala.components.form7.renderers.*

class F7IntField(using TextF7FieldRenderer)
    extends F7TextFieldBase[Int]
       with F7FieldWithPrefix
       with F7FieldWithSuffix
       with F7FieldWithMin
       with F7FieldWithStep
       with F7FieldWithMax:
  override def _inputTypeDefault: String = "number"

  def defaultValue: Int = 0

  def toString(value: Int): String = (prefix + " " + value + " " + suffix).trim

  def fromString(str: String): Either[String, Int] =
    str.toLowerCase.trim
      .replaceAll("^" + Pattern.quote(prefix.toLowerCase) + " *", "")
      .replaceAll(" *" + Pattern.quote(suffix.toLowerCase) + "$", "")
      .toIntOption match
      case Some(value) => Right(value)
      case None => Left(s"Not an int?: $str")
