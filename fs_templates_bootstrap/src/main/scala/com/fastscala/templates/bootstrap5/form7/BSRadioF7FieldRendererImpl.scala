package com.fastscala.templates.bootstrap5.form7

import scala.util.chaining.scalaUtilChainingOps
import scala.xml.{ Elem, NodeSeq }

import com.fastscala.js.Js
import com.fastscala.templates.bootstrap5.form7.renderermodifiers.*
import com.fastscala.templates.form7.fields.radio.F7RadioFieldBase
import com.fastscala.templates.form7.renderers.RadioF7FieldRenderer
import com.fastscala.utils.IdGen
import com.fastscala.xml.scala_xml.JS
import com.fastscala.xml.scala_xml.ScalaXmlNodeSeqUtils.MkNSFromNodeSeq

abstract class BSRadioF7FieldRendererImpl(using
  checkboxAlignment: CheckboxAlignment,
  checkboxStyle: CheckboxStyle,
  checkboxSide: CheckboxSide,
) extends RadioF7FieldRenderer:
  import com.fastscala.templates.bootstrap5.helpers.BSHelpers.{ given, * }

  override def render(
    field: F7RadioFieldBase[?]
  )(
    inputElemsAndLabels: Seq[(Elem, Option[Elem])],
    invalidFeedback: Option[Elem],
    validFeedback: Option[Elem],
    help: Option[Elem],
  ): Elem =
    div.mb_3.withId(field.aroundId):
      val invalidFeedbackId = invalidFeedback.flatMap(_.getId).getOrElse(field.invalidFeedbackId)
      val validFeedbackId = validFeedback.flatMap(_.getId).getOrElse(field.validFeedbackId)
      val helpId = help.flatMap(_.getId).getOrElse(field.helpId)

      inputElemsAndLabels
        .map: (inputElem, label) =>
          form_check
            .pipe: elem =>
              checkboxAlignment match
                case CheckboxAlignment.Vertical => elem
                case CheckboxAlignment.Horizontal => elem.form_check_inline
            .pipe: elem =>
              checkboxStyle match
                case CheckboxStyle.Checkbox => elem
                case CheckboxStyle.Switch => elem.form_switch
            .pipe: elem =>
              checkboxSide match
                case CheckboxSide.Normal => elem
                case CheckboxSide.Opposite => elem.form_check_reverse
            .pipe: elem =>
              if invalidFeedback.isDefined then elem.is_invalid
              else if validFeedback.isDefined then elem.is_valid
              else elem
            .apply:
              val inputId = inputElem.getId.getOrElse(IdGen.id("input"))
              val labelId = label.flatMap(_.getId).getOrElse(IdGen.id("label"))
              inputElem
                .withName(field.radioNameId)
                .withIdIfNotSet(inputId)
                .form_check_input
                .pipe: elem =>
                  if invalidFeedback.isDefined then elem.is_invalid
                  else if validFeedback.isDefined then elem.is_valid
                  else elem
                .withAttrs(
                  (label.map(_ => "aria-labelledby" -> labelId).toSeq :+ "aria-describedby" `->`:
                    if invalidFeedback.isDefined then invalidFeedbackId
                    else if validFeedback.isDefined then validFeedbackId
                    else helpId
                  )*
                ) ++
                label
                  .map(_.form_check_label.withIdIfNotSet(labelId).withFor(inputId))
                  .getOrElse(Empty)
        .mkNS ++
        invalidFeedback.getOrElse(div.visually_hidden).invalid_feedback.withIdIfNotSet(invalidFeedbackId) ++
        validFeedback.getOrElse(div.visually_hidden).valid_feedback.withIdIfNotSet(validFeedbackId) ++
        help.getOrElse(div.visually_hidden).form_text.withIdIfNotSet(helpId)

  def showOrUpdateValidation(field: F7RadioFieldBase[?])(ns: NodeSeq): Js =
    JS.setContents(field.invalidFeedbackId, ns) &
      JS.removeClass(field.invalidFeedbackId, "visually-hidden") &
      JS.addClass(field.validFeedbackId, "visually-hidden") &
      JS.addClassToElemsMatchingSelector(s"#${field.aroundId} > .form-check", "is-invalid") &
      JS.addClassToElemsMatchingSelector(s"#${field.aroundId} > .form-check > input", "is-invalid") &
      JS.removeClassFromElemsMatchingSelector(s"#${field.aroundId} > .form-check", "is-valid") &
      JS.removeClassFromElemsMatchingSelector(s"#${field.aroundId} > .form-check > input", "is-valid")

  def hideValidation(field: F7RadioFieldBase[?]): Js =
    JS.addClass(field.invalidFeedbackId, "visually-hidden") &
      JS.removeClassFromElemsMatchingSelector(s"#${field.aroundId} > .form-check", "is-invalid") &
      JS.removeClassFromElemsMatchingSelector(s"#${field.aroundId} > .form-check > input", "is-invalid")
