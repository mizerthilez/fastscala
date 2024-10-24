package com.fastscala.templates.form7.renderers

import scala.xml.{ Elem, NodeSeq }

import com.fastscala.js.Js
import com.fastscala.templates.form7.fields.radio.F7RadioFieldBase

trait RadioF7FieldRenderer extends StandardF7FieldRenderer:
  def render(
    field: F7RadioFieldBase[?]
  )(
    inputElemsAndLabels: Seq[(Elem, Option[Elem])],
    invalidFeedback: Option[Elem],
    validFeedback: Option[Elem],
    help: Option[Elem],
  ): Elem

  def showOrUpdateValidation(field: F7RadioFieldBase[?])(ns: NodeSeq): Js

  def hideValidation(field: F7RadioFieldBase[?]): Js
