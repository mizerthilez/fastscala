package com.fastscala.templates.form7.renderers

import scala.xml.{ Elem, NodeSeq }

import com.fastscala.templates.form7.RenderHint
import com.fastscala.templates.form7.fields.text.F7TextareaField

trait TextareaF7FieldRenderer:
  def defaultRequiredFieldLabel: String

  def render[T](
    field: F7TextareaField[T]
  )(
    label: Option[NodeSeq],
    inputElem: Elem,
    error: Option[NodeSeq],
  )(using Seq[RenderHint]
  ): Elem
