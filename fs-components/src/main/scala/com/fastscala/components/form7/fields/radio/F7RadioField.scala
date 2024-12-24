package com.fastscala.components.form7.fields.radio

import com.fastscala.components.form7.renderers.*

class F7RadioField[T](opts: () => Seq[T])(using RadioF7FieldRenderer) extends F7RadioFieldBase[T]:
  options(opts)

  def this(opts: Seq[T])(using RadioF7FieldRenderer) = this(() => opts)

  def defaultValue: T = options.head
