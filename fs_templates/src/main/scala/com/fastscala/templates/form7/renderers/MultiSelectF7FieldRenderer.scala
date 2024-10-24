package com.fastscala.templates.form7.renderers

import scala.xml.{ Elem, NodeSeq }

import com.fastscala.templates.form7.RenderHint

trait MultiSelectF7FieldRenderer extends StandardOneInputElemF7FieldRenderer:
  def renderOption(selected: Boolean, value: String, label: NodeSeq)(using Seq[RenderHint]): Elem
