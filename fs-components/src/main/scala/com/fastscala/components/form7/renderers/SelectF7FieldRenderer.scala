package com.fastscala.components.form7.renderers

import scala.xml.{ Elem, NodeSeq }

import com.fastscala.components.form7.RenderHint

trait SelectF7FieldRenderer extends StandardOneInputElemF7FieldRenderer:
  def renderOption(selected: Boolean, value: String, label: NodeSeq)(using Seq[RenderHint]): Elem
