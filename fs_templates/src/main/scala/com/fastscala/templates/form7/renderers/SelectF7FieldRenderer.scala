package com.fastscala.templates.form7.renderers

import scala.xml.{ Elem, NodeSeq }

import com.fastscala.templates.form7.RenderHint
import com.fastscala.templates.form7.fields.select.F7SelectFieldBase

trait SelectF7FieldRenderer:
  def defaultRequiredFieldLabel: String

  def render[T](
    field: F7SelectFieldBase[T]
  )(
    label: Option[Elem],
    elem: Elem,
    error: Option[NodeSeq],
  )(implicit hints: Seq[RenderHint]
  ): Elem

  def renderOption[T](
    field: F7SelectFieldBase[T]
  )(
    selected: Boolean,
    value: String,
    label: NodeSeq,
  )(implicit hints: Seq[RenderHint]
  ): Elem
