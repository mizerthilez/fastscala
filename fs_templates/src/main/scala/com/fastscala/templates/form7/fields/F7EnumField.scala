package com.fastscala.templates.form7.fields

import scala.deriving.Mirror

import com.fastscala.templates.form7.fields.multiselect.F7MultiSelectField
import com.fastscala.templates.form7.fields.select.{ F7SelectField, F7SelectOptField }
import com.fastscala.templates.form7.renderers.{ MultiSelectF7FieldRenderer, SelectF7FieldRenderer }
import com.fastscala.utils.ReflectUtils.valuesFor

object F7EnumField:
  def NonNullable[T <: Enumeration](e: T)(using SelectF7FieldRenderer): F7SelectField[e.Value] =
    F7SelectField(e.values.toList)

  def Nullable[T <: Enumeration](e: T)(using SelectF7FieldRenderer): F7SelectOptField[e.Value] =
    F7SelectOptField().optionsNonEmpty(e.values.toList)

  def Multi[T <: Enumeration](e: T)(using MultiSelectF7FieldRenderer): F7MultiSelectField[e.Value] =
    F7MultiSelectField().options(e.values.toList)

  inline def NonNullable[T: Mirror.SumOf](using SelectF7FieldRenderer): F7SelectField[T] =
    F7SelectField(valuesFor)

  inline def Nullable[T: Mirror.SumOf](using SelectF7FieldRenderer): F7SelectOptField[T] =
    F7SelectOptField().optionsNonEmpty(valuesFor)

  inline def Multi[T: Mirror.SumOf](using MultiSelectF7FieldRenderer): F7MultiSelectField[T] =
    F7MultiSelectField().options(valuesFor)
