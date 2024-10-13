package com.fastscala.templates.form7.renderers

import scala.xml.{ Elem, NodeSeq }

import com.fastscala.templates.form7.RenderHint
import com.fastscala.templates.form7.fields.F7CheckboxField

trait CheckboxF7FieldRenderer:
  def render(
    field: F7CheckboxField
  )(
    label: Option[Elem],
    elem: Elem,
    error: Option[NodeSeq],
  )(implicit hints: Seq[RenderHint]
  ): Elem
