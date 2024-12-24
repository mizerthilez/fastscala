package com.fastscala.components.form6.fields

import scala.deriving.Mirror

import com.fastscala.utils.ReflectUtils.valuesFor

object F6EnumField:
  def NonNullable[T <: Enumeration](e: T)(using SelectF6FieldRenderer): F6SelectField[e.Value] =
    F6SelectField(e.values.toList)

  def Nullable[T <: Enumeration](e: T)(using SelectF6FieldRenderer): F6SelectOptField[e.Value] =
    F6SelectOptField().optionsNonEmpty(e.values.toList)

  def Multi[T <: Enumeration](e: T)(using MultiSelectF6FieldRenderer): F6MultiSelectField[e.Value] =
    F6MultiSelectField().options(e.values.toList)

  inline def NonNullable[T: Mirror.SumOf](using SelectF6FieldRenderer): F6SelectField[T] =
    F6SelectField(valuesFor)

  inline def Nullable[T: Mirror.SumOf](using SelectF6FieldRenderer): F6SelectOptField[T] =
    F6SelectOptField().optionsNonEmpty(valuesFor)

  inline def Multi[T: Mirror.SumOf](using MultiSelectF6FieldRenderer): F6MultiSelectField[T] =
    F6MultiSelectField().options(valuesFor)
