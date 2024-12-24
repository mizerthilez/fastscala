package com.fastscala.components.bootstrap5.form7

import scala.util.chaining.scalaUtilChainingOps
import scala.xml.{ Elem, NodeSeq }

import com.fastscala.components.bootstrap5.form7.renderermodifiers.*
import com.fastscala.components.form7.mixins.StandardF7Field
import com.fastscala.components.form7.renderers.CheckboxF7FieldRenderer

abstract class BSCheckboxF7FieldRendererImpl(using
  checkboxAlignment: CheckboxAlignment,
  checkboxStyle: CheckboxStyle,
  checkboxSide: CheckboxSide,
) extends CheckboxF7FieldRenderer
       with BSStandardF7FieldRendererImpl:
  import com.fastscala.components.bootstrap5.helpers.BSHelpers.{ given, * }

  override def render(
    field: StandardF7Field
  )(
    inputElem: Elem,
    label: Option[Elem],
    invalidFeedback: Option[Elem],
    validFeedback: Option[Elem],
    help: Option[Elem],
  ): Elem =
    val aroundId = field.aroundId
    val inputId = inputElem.getId.getOrElse(field.elemId)
    val labelId = label.flatMap(_.getId).getOrElse(field.labelId)
    val invalidFeedbackId = invalidFeedback.flatMap(_.getId).getOrElse(field.invalidFeedbackId)
    val validFeedbackId = validFeedback.flatMap(_.getId).getOrElse(field.validFeedbackId)
    val helpId = help.flatMap(_.getId).getOrElse(field.helpId)

    div.form_check
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
      .mb_3
      .withId(aroundId):
        inputElem
          .withIdIfNotSet(inputId)
          .form_check_input
          .pipe: elem =>
            if invalidFeedback.isDefined then elem.is_invalid
            else if validFeedback.isDefined then elem.is_valid
            else elem
          .withAttrs(
            (label.map(_ => "aria-labelledby" -> helpId).toSeq `++`:
              invalidFeedback
                .map(_ => "aria-describedby" -> invalidFeedbackId)
                .orElse(validFeedback.map(_ => "aria-describedby" -> validFeedbackId))
            )*
          ) ++
          label.map(_.withIdIfNotSet(labelId).form_check_label.withFor(inputId)).getOrElse(Empty) ++
          invalidFeedback.getOrElse(div.visually_hidden).invalid_feedback.withIdIfNotSet(invalidFeedbackId) ++
          validFeedback.getOrElse(div.visually_hidden).valid_feedback.withIdIfNotSet(validFeedbackId) ++
          help.getOrElse(div.visually_hidden).form_text.withIdIfNotSet(helpId): NodeSeq
