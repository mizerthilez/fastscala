package com.fastscala.templates.bootstrap5.form7

import scala.util.chaining.scalaUtilChainingOps
import scala.xml.{ Elem, NodeSeq }

import com.fastscala.templates.form7.mixins.StandardF7Field
import com.fastscala.templates.form7.renderers.StandardOneInputElemF7FieldRenderer

trait BSStandardF7FieldRendererImpl extends StandardOneInputElemF7FieldRenderer:
  import com.fastscala.templates.bootstrap5.helpers.BSHelpers.{ given, * }
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

    div.mb_3.withId(aroundId):
      label.map(_.withIdIfNotSet(labelId).form_label.withFor(inputId)).getOrElse(Empty) ++
        inputElem
          .withIdIfNotSet(field.elemId)
          .form_control
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
        invalidFeedback.getOrElse(div.visually_hidden).invalid_feedback.withIdIfNotSet(invalidFeedbackId) ++
        validFeedback.getOrElse(div.visually_hidden).valid_feedback.withIdIfNotSet(validFeedbackId) ++
        help.getOrElse(div.visually_hidden).form_text.withIdIfNotSet(helpId): NodeSeq
