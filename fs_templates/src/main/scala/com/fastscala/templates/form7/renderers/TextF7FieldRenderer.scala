package com.fastscala.templates.form7.renderers

import scala.xml.{ Elem, NodeSeq }

import com.fastscala.js.Js
import com.fastscala.templates.form7.fields.text.F7TextField

trait TextF7FieldRenderer:
  def defaultRequiredFieldLabel: String

  def render(
    field: F7TextField[?]
  )(
    inputElem: Elem,
    label: Option[Elem],
    invalidFeedback: Option[Elem],
    validFeedback: Option[Elem],
    help: Option[Elem],
  ): Elem

  def showValidation(field: F7TextField[?])(ns: NodeSeq): Js

  def hideValidation(field: F7TextField[?])(): Js
