package com.fastscala.components.form7.fields.select

import com.fastscala.components.form7.renderers.*

class F7SelectField[T](opts: () => Seq[T])(using SelectF7FieldRenderer) extends F7SelectFieldBase[T]:
  options(opts)

  def this(opts: Seq[T])(using SelectF7FieldRenderer) = this(() => opts)

  def defaultValue: T = options.head
