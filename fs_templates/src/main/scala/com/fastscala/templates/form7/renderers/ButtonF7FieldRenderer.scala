package com.fastscala.templates.form7.renderers

import scala.xml.Elem

import com.fastscala.templates.form7.RenderHint
import com.fastscala.templates.form7.fields.F7SubmitButtonField

trait ButtonF7FieldRenderer:
  def render(field: F7SubmitButtonField[?])(btn: Elem)(using Seq[RenderHint]): Elem
