package com.fastscala.templates.form7.fields.radio

import com.fastscala.templates.form7.mixins.*
import com.fastscala.templates.form7.renderers.*

class F7RadioField[T](opts: () => Seq[T])(using RadioF7FieldRenderer)
    extends F7RadioFieldBase[T]
       with F7FieldWithValidations:
  options(opts)

  def this(opts: Seq[T])(using RadioF7FieldRenderer) = this(() => opts)

  def defaultValue: T = options().head
