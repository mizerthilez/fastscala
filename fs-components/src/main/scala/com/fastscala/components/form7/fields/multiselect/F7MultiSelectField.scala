package com.fastscala.components.form7.fields.multiselect

import com.fastscala.components.form7.renderers.*

class F7MultiSelectField[T](using MultiSelectF7FieldRenderer) extends F7MultiSelectFieldBase[T]:
  def defaultValue: Set[T] = Set()
