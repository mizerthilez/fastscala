package com.fastscala.templates.form7.renderers

import scala.xml.Elem

import com.fastscala.templates.form7.RenderHint
import com.fastscala.templates.form7.fields.F7SaveButtonField

trait ButtonF7FieldRenderer:
  def render(field: F7SaveButtonField[?])(btn: Elem)(implicit hints: Seq[RenderHint]): Elem
