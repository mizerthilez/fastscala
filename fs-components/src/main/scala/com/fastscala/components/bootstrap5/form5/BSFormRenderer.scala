package com.fastscala.components.bootstrap5.form5

import scala.xml.{ Elem, NodeSeq }

import com.fastscala.components.bootstrap5.helpers.BSHelpers
import com.fastscala.components.form5.FormRenderer
import com.fastscala.components.form5.fields.*

abstract class BSFormRenderer:
  import com.fastscala.components.bootstrap5.helpers.BSHelpers.{ given, * }

  def defaultRequiredFieldLabel: String

  implicit val bsFormRenderer: BSFormRenderer = this

  implicit val dateFieldOptRenderer: DateFieldOptRenderer = new DateFieldOptRenderer:
    override def defaultRequiredFieldLabel: String = BSFormRenderer.this.defaultRequiredFieldLabel

  def textFieldRendererInputElemClasses: String = form_control.getClassAttr

  def textFieldRendererInputElemStyle: String = form_control.getStyleAttr

  implicit val textFieldRenderer: TextFieldRenderer = new TextFieldRenderer:

    def defaultRequiredFieldLabel: String = BSFormRenderer.this.defaultRequiredFieldLabel

    override def render[T](
      field: F5TextField[T]
    )(
      label: Option[NodeSeq],
      inputElem: Elem,
      error: Option[NodeSeq],
    )(implicit hints: Seq[RenderHint]
    ): Elem =
      if !field.enabled() then div.withId(field.aroundId).withStyle(";display:none;")
      else
        div.mb_3
          .withId(field.aroundId)
          .apply:
            val showErrors = hints.contains(ShowValidationsHint)
            label
              .map(lbl => BSHelpers.label.form_label.withAttr("for" -> field.elemId).apply(lbl))
              .getOrElse(Empty) ++
              inputElem
                .withStyle(textFieldRendererInputElemStyle)
                .withClass(textFieldRendererInputElemClasses)
                .withClassIf(showErrors && error.isDefined, is_invalid.getClassAttr)
                .withAttrIf(hints.contains(DisableFieldsHint), "disabled" -> "true")
                .withAttrIf(hints.contains(ReadOnlyFieldsHint), "readonly" -> "true") ++
              error
                .filter(_ => showErrors)
                .map(error => invalid_feedback.apply(error))
                .getOrElse(Empty)

  def textareaFieldRendererTextareaElemClasses: String = form_control.getClassAttr

  def textareaFieldRendererTextareaElemStyle: String = form_control.getStyleAttr

  implicit val textareaFieldRenderer: TextareaFieldRenderer = new TextareaFieldRenderer:

    def defaultRequiredFieldLabel: String = BSFormRenderer.this.defaultRequiredFieldLabel

    override def render(
      field: F5TextAreaField
    )(
      label: Option[NodeSeq],
      inputElem: Elem,
      error: Option[NodeSeq],
    )(implicit hints: Seq[RenderHint]
    ): Elem =
      if !field.enabled() then div.withId(field.aroundId).withStyle(";display:none;")
      else
        div.mb_3
          .withId(field.aroundId)
          .apply:
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
              error
                .filter(_ => showErrors)
                .map(error => invalid_feedback.apply(error))
                .getOrElse(Empty)

  def selectFieldRendererSelectElemClasses: String = form_select.form_control.getClassAttr

  implicit val selectFieldRenderer: SelectFieldRenderer = new SelectFieldRenderer:

    def defaultRequiredFieldLabel: String = BSFormRenderer.this.defaultRequiredFieldLabel

    override def render[T](
      field: F5SelectField[T]
    )(
      label: Option[Elem],
      elem: Elem,
      error: Option[NodeSeq],
    )(implicit hints: Seq[RenderHint]
    ): Elem =
      if !field.enabled() then div.withId(field.aroundId).withStyle(";display:none;")
      else
        div.mb_3
          .withId(field.aroundId)
          .apply:
            val showErrors = true // hints.contains(ShowValidationsHint)
            label
              .map(lbl => BSHelpers.label.form_label.withAttr("for" -> field.elemId)(lbl))
              .getOrElse(Empty) ++
              elem
                .addClass(selectFieldRendererSelectElemClasses)
                .withClassIf(showErrors && error.isDefined, is_invalid.getClassAttr)
                .withAttrIf(hints.contains(DisableFieldsHint), "disabled" -> "true")
                .withAttrIf(hints.contains(ReadOnlyFieldsHint), "readonly" -> "true") ++
              error
                .filter(_ => showErrors)
                .map(error => invalid_feedback.apply(error))
                .getOrElse(Empty)

  def multiSelectFieldRendererSelectElemClasses: String = form_select.form_control.getClassAttr

  implicit val multiSelectFieldRenderer: MultiSelectFieldRenderer = new MultiSelectFieldRenderer:

    def defaultRequiredFieldLabel: String = BSFormRenderer.this.defaultRequiredFieldLabel

    override def render[T](
      field: F5MultiSelectField[T]
    )(
      label: Option[Elem],
      elem: Elem,
      error: Option[NodeSeq],
    )(implicit hints: Seq[RenderHint]
    ): Elem =
      if !field.enabled() then div.withId(field.aroundId).withStyle(";display:none;")
      else
        div.mb_3
          .withId(field.aroundId)
          .apply:
            val showErrors = true // hints.contains(ShowValidationsHint)
            label
              .map(lbl => BSHelpers.label.form_label.withAttr("for" -> field.elemId)(lbl))
              .getOrElse(Empty) ++
              elem
                .addClass(multiSelectFieldRendererSelectElemClasses)
                .withClassIf(showErrors && error.isDefined, is_invalid.getClassAttr)
                .withAttrIf(hints.contains(DisableFieldsHint), "disabled" -> "true")
                .withAttrIf(hints.contains(ReadOnlyFieldsHint), "readonly" -> "true") ++
              error
                .filter(_ => showErrors)
                .map(error => invalid_feedback.apply(error))
                .getOrElse(Empty)

  def checkboxFieldRendererCheckboxElemClasses: String = form_check_input.getClassAttr

  implicit val checkboxFieldRenderer: CheckboxFieldRenderer = new CheckboxFieldRenderer:

    override def render(
      field: F5CheckboxField
    )(
      label: Option[Elem],
      elem: Elem,
      error: Option[NodeSeq],
    )(implicit hints: Seq[RenderHint]
    ): Elem =
      if !field.enabled() then div.withId(field.aroundId).withStyle(";display:none;")
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
            error
              .filter(_ => showErrors)
              .map(error => invalid_feedback.apply(error))
              .getOrElse(Empty)

  implicit val fileUploadFieldRenderer: FileUploadFieldRenderer = new FileUploadFieldRenderer:
    override def transformFormElem(
      field: F5FileUploadField
    )(
      elem: Elem
    )(implicit hints: Seq[RenderHint]
    ): Elem = super.transformFormElem(field)(elem).mb_3

  implicit val buttonFieldRenderer: ButtonFieldRenderer = new ButtonFieldRenderer:
    override def render(field: F5SaveButtonField[?])(btn: Elem)(implicit hints: Seq[RenderHint]): Elem =
      if !field.enabled() then div.withId(field.aroundId).withStyle(";display:none;")
      else
        div.mb_3
          .addClass("d-grid gap-2 d-md-flex justify-content-md-end")
          .withId(field.aroundId)
          .apply(
            btn
              .withAttrIf(hints.contains(DisableFieldsHint), "disabled" -> "true")
              .withAttrIf(hints.contains(ReadOnlyFieldsHint), "readonly" -> "true")
          )

  implicit def formRenderer: FormRenderer = (form: Elem) => form.mb_5.w_100.addClass("form")
