package com.fastscala.templates.form6.fields

object EnumField:
  def NonNullable[T <: Enumeration](e: T)(implicit renderer: SelectF6FieldRenderer)
    : F6SelectField[e.Value] =
    new F6SelectField[e.Value](e.values.toList)

  def Nullable[T <: Enumeration](e: T)(implicit renderer: SelectF6FieldRenderer)
    : F6SelectOptField[e.Value] =
    new F6SelectOptField[e.Value]().optionsNonEmpty(e.values.toList)

  def Multi[T <: Enumeration](e: T)(implicit renderer: MultiSelectF6FieldRenderer)
    : F6MultiSelectField[e.Value] =
    new F6MultiSelectField[e.Value]().options(e.values.toList)
