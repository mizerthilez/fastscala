package com.fastscala.templates.form7.fields.select

import com.fastscala.templates.form7.renderers.*

class F7MultiSelectField[T](using MultiSelectF7FieldRenderer) extends F7MultiSelectFieldBase[T]:
  def defaultValue: Set[T] = Set()
