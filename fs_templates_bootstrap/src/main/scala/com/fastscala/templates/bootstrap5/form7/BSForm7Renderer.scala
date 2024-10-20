package com.fastscala.templates.bootstrap5.form7

import scala.util.chaining.scalaUtilChainingOps
import scala.xml.{ Elem, NodeSeq }

import com.fastscala.js.Js
import com.fastscala.templates.bootstrap5.helpers.BSHelpers
import com.fastscala.templates.form7.*
import com.fastscala.templates.form7.fields.*
import com.fastscala.templates.form7.fields.select.{ F7MultiSelectFieldBase, F7SelectFieldBase }
import com.fastscala.templates.form7.fields.text.{ F7TextField, F7TextareaField }
import com.fastscala.templates.form7.renderers.*
import com.fastscala.templates.form7.RenderHint.*
import com.fastscala.xml.scala_xml.JS
import com.fastscala.xml.scala_xml.JS.given

abstract class BSForm7Renderer:
  import com.fastscala.templates.bootstrap5.helpers.BSHelpers.{ given, * }

  def defaultRequiredFieldLabel: String

  given BSForm7Renderer = this

  given TextF7FieldRenderer with
    def defaultRequiredFieldLabel: String = BSForm7Renderer.this.defaultRequiredFieldLabel

    def render(
      field: F7TextField[?]
    )(
      inputElem: Elem,
      label: Option[Elem],
      invalidFeedback: Option[Elem],
      validFeedback: Option[Elem],
      help: Option[Elem],
    ): Elem =
      if !field.enabled then div.withId(field.aroundId).withStyle("display:none;")
      else
        div.mb_3.withId(field.aroundId):
          label.map(_.form_label.withFor(field.elemId)).getOrElse(Empty) ++
            inputElem.form_control
              .pipe: elem =>
                if invalidFeedback.isDefined then elem.is_invalid
                else if validFeedback.isDefined then elem.is_valid
                else elem
              .withAttrs(
                (if invalidFeedback.isEmpty && validFeedback.isEmpty then
                   help.map(help => "aria-describedby" -> help.getId.getOrElse(field.elemId + "-help")).toSeq
                 else
                   invalidFeedback
                     .map(invalidFeedback =>
                       "aria-describedby" -> invalidFeedback.getId
                         .getOrElse(field.elemId + "-invalid-feedback")
                     )
                     .toSeq
                )*
              ) ++
            invalidFeedback
              .getOrElse(div.visually_hidden)
              .invalid_feedback
              .withFor(field.elemId)
              .withIdIfNotSet(field.elemId + "-invalid-feedback") ++
            validFeedback
              .getOrElse(div.visually_hidden)
              .valid_feedback
              .withIdIfNotSet(field.elemId + "-valid-feedback") ++
            help
              .filter(_ => invalidFeedback.isEmpty && validFeedback.isEmpty)
              .getOrElse(div.visually_hidden)
              .form_text
              .withIdIfNotSet(field.elemId + "-help"): NodeSeq

    def showValidation(field: F7TextField[?])(ns: NodeSeq): Js = (
      JS.setContents(field.elemId + "-invalid-feedback", ns) &
        JS.removeClass(field.elemId + "-invalid-feedback", "visually-hidden") &
        JS.addClass(field.elemId + "-valid-feedback", "visually-hidden") &
        JS.addClass(field.elemId, "is-invalid") &
        JS.removeClass(field.elemId, "is-valid") &
        JS.setAttr(field.elemId)("aria-describedby", field.elemId + "-invalid-feedback")
    ).printBeforeExec

    def hideValidation(field: F7TextField[?])(): Js =
      JS.addClass(field.elemId + "-invalid-feedback", "visually-hidden") &
        JS.removeClass(field.elemId, "is-invalid") &
        JS.removeAttr(field.elemId, "aria-describedby")

  def textareaFieldRendererTextareaElemClasses: String = form_control.getClassAttr

  def textareaFieldRendererTextareaElemStyle: String = form_control.getStyleAttr

  given TextareaF7FieldRenderer with
    def defaultRequiredFieldLabel: String = BSForm7Renderer.this.defaultRequiredFieldLabel

    def render[T](
      field: F7TextareaField[T]
    )(
      label: Option[NodeSeq],
      inputElem: Elem,
      error: Option[NodeSeq],
    )(using hints: Seq[RenderHint]
    ): Elem =
      if !field.enabled then div.withId(field.aroundId).withStyle(";display:none;")
      else
        div.mb_3.withId(field.aroundId):
          val showErrors = hints.contains(ShowValidationsHint)
          label
            .map(lbl => BSHelpers.label.form_label.withAttr("for" -> field.elemId)(lbl))
            .getOrElse(Empty) ++
            inputElem
              .withClass(textareaFieldRendererTextareaElemClasses)
              .withStyle(textareaFieldRendererTextareaElemStyle)
              .withClassIf(showErrors && error.isDefined, is_invalid.getClassAttr)
              .withAttrIf(hints.contains(DisableFieldsHint), "disabled" -> "true")
              .withAttrIf(hints.contains(ReadOnlyFieldsHint), "readonly" -> "true") ++
            error.filter(_ => showErrors).map(error => invalid_feedback.apply(error)).getOrElse(Empty)

  def selectFieldRendererSelectElemClasses: String = form_select.form_control.getClassAttr

  given SelectF7FieldRenderer with
    def defaultRequiredFieldLabel: String = BSForm7Renderer.this.defaultRequiredFieldLabel

    def render[T](
      field: F7SelectFieldBase[T]
    )(
      label: Option[Elem],
      elem: Elem,
      error: Option[NodeSeq],
    )(using hints: Seq[RenderHint]
    ): Elem =
      if !field.enabled then div.withId(field.aroundId).withStyle(";display:none;")
      else
        div.mb_3.withId(field.aroundId):
          val showErrors = true // hints.contains(ShowValidationsHint)
          label
            .map(lbl => BSHelpers.label.form_label.withAttr("for" -> field.elemId)(lbl))
            .getOrElse(Empty) ++
            elem
              .addClass(selectFieldRendererSelectElemClasses)
              .withClassIf(showErrors && error.isDefined, is_invalid.getClassAttr)
              .withAttrIf(hints.contains(DisableFieldsHint), "disabled" -> "true")
              .withAttrIf(hints.contains(ReadOnlyFieldsHint), "readonly" -> "true") ++
            error.filter(_ => showErrors).map(error => invalid_feedback.apply(error)).getOrElse(Empty)

    def renderOption[T](
      field: F7SelectFieldBase[T]
    )(
      selected: Boolean,
      value: String,
      label: NodeSeq,
    )(using Seq[RenderHint]
    ): Elem =
      <option selected={if selected then "true" else null} value={value}>{label}</option>

  def multiSelectFieldRendererSelectElemClasses: String = form_select.form_control.getClassAttr

  given MultiSelectF7FieldRenderer with
    def defaultRequiredFieldLabel: String = BSForm7Renderer.this.defaultRequiredFieldLabel

    def render[T](
      field: F7MultiSelectFieldBase[T]
    )(
      label: Option[Elem],
      elem: Elem,
      error: Option[NodeSeq],
    )(using hints: Seq[RenderHint]
    ): Elem =
      if !field.enabled then div.withId(field.aroundId).withStyle(";display:none;")
      else
        div.mb_3.withId(field.aroundId):
          val showErrors = true // hints.contains(ShowValidationsHint)
          label
            .map(lbl => BSHelpers.label.form_label.withAttr("for" -> field.elemId)(lbl))
            .getOrElse(Empty) ++
            elem
              .addClass(selectFieldRendererSelectElemClasses)
              .withClassIf(showErrors && error.isDefined, is_invalid.getClassAttr)
              .withAttrIf(hints.contains(DisableFieldsHint), "disabled" -> "true")
              .withAttrIf(hints.contains(ReadOnlyFieldsHint), "readonly" -> "true") ++
            error.filter(_ => showErrors).map(error => invalid_feedback.apply(error)).getOrElse(Empty)

    def renderOption[T](
      field: F7MultiSelectFieldBase[T]
    )(
      selected: Boolean,
      value: String,
      label: NodeSeq,
    )(using Seq[RenderHint]
    ): Elem =
      <option selected={if selected then "true" else null} value={value}>{label}</option>

  def checkboxFieldRendererCheckboxElemClasses: String = form_check_input.getClassAttr

  given CheckboxF7FieldRenderer with
    def render(
      field: F7CheckboxField
    )(
      label: Option[Elem],
      elem: Elem,
      error: Option[NodeSeq],
    )(using hints: Seq[RenderHint]
    ): Elem =
      if !field.enabled then div.withId(field.aroundId).withStyle(";display:none;")
      else
        div.mb_3.form_check.withId(field.aroundId):
          val showErrors = hints.contains(ShowValidationsHint)
          elem
            .addClass(checkboxFieldRendererCheckboxElemClasses)
            .withClassIf(showErrors && error.isDefined, is_invalid.getClassAttr)
            .withAttrIf(hints.contains(DisableFieldsHint), "disabled" -> "true")
            .withAttrIf(hints.contains(ReadOnlyFieldsHint), "readonly" -> "true") ++
            label
              .map(lbl => BSHelpers.label.form_check_label.withFor(field.elemId)(lbl))
              .getOrElse(Empty) ++
            error.filter(_ => showErrors).map(error => invalid_feedback.apply(error)).getOrElse(Empty)

  given ButtonF7FieldRenderer with
    def render(field: F7SaveButtonField[?])(btn: Elem)(using hints: Seq[RenderHint]): Elem =
      if !field.enabled then div.withId(field.aroundId).withStyle(";display:none;")
      else
        div.mb_3
          .addClass("d-grid gap-2 d-md-flex justify-content-md-end")
          .withId(field.aroundId):
            btn
              .withAttrIf(hints.contains(DisableFieldsHint), "disabled" -> "true")
              .withAttrIf(hints.contains(ReadOnlyFieldsHint), "readonly" -> "true")

  given F7FormRenderer with
    def render(form: Elem): Elem = form.mb_5.w_100.addClass("form")
