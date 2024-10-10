package com.fastscala.templates.bootstrap5.form7

import com.fastscala.js.Js
import com.fastscala.templates.form7.fields.radio.F7RadioFieldBase
import com.fastscala.templates.form7.renderers.RadioF7FieldRenderer
import com.fastscala.utils.IdGen
import com.fastscala.xml.scala_xml.JS
import com.fastscala.xml.scala_xml.JS.RichJs
import com.fastscala.xml.scala_xml.ScalaXmlNodeSeqUtils.MkNSFromNodeSeq

import scala.util.chaining.scalaUtilChainingOps
import scala.xml.{Elem, NodeSeq}

trait BSRadioF7FieldRendererImpl extends RadioF7FieldRenderer {

  import com.fastscala.templates.bootstrap5.helpers.BSHelpers._

  override def render(field: F7RadioFieldBase[_])(inputElemsAndLabels: Seq[(Elem, Option[Elem])], invalidFeedback: Option[Elem], validFeedback: Option[Elem], help: Option[Elem]): Elem = {
    val aroundId = field.aroundId
    div.mb_3.withId(field.aroundId).apply {
      val invalidFeedbackId = invalidFeedback.flatMap(_.getId).getOrElse(field.invalidFeedbackId)
      val validFeedbackId = validFeedback.flatMap(_.getId).getOrElse(field.validFeedbackId)
      val helpId = help.flatMap(_.getId).getOrElse(field.helpId)

      inputElemsAndLabels.map({
        case (inputElem, label) =>
          form_check.pipe(elem => {
            if (invalidFeedback.isDefined) elem.is_invalid
            else if (validFeedback.isDefined) elem.is_valid
            else elem
          }).apply {
            val inputId = inputElem.getId.getOrElse(IdGen.id("input"))
            val labelId = label.flatMap(_.getId).getOrElse(IdGen.id("label"))
            inputElem.withName(field.radioNameId).withIdIfNotSet(inputId).form_check_input.pipe(elem => {
              if (invalidFeedback.isDefined) elem.is_invalid
              else if (validFeedback.isDefined) elem.is_valid
              else elem
            }.withAttrs(
              (if (invalidFeedback.isEmpty && validFeedback.isEmpty) {
                help.map(help => "aria-describedby" -> help.getId.getOrElse(field.helpId)).toSeq
              } else {
                invalidFeedback.map(invalidFeedback => "aria-describedby" -> invalidFeedback.getId.getOrElse(field.invalidFeedbackId)).toSeq
              }) ++
                label.map(help => "aria-labelledby" -> labelId): _*
            ) ++
              label.map(_.form_check_label.withIdIfNotSet(labelId).withFor(inputId)).getOrElse(Empty): NodeSeq)
          }
      }).mkNS ++
        invalidFeedback.getOrElse(div.visually_hidden).invalid_feedback.withIdIfNotSet(invalidFeedbackId) ++
        validFeedback.getOrElse(div.visually_hidden).valid_feedback.withIdIfNotSet(validFeedbackId) ++
        help.getOrElse(div.visually_hidden).form_text.withIdIfNotSet(helpId)
    }
  }

  def showOrUpdateValidation(
                              field: F7RadioFieldBase[_]
                            )(ns: NodeSeq): Js =
    JS.setContents(field.invalidFeedbackId, ns) &
      JS.removeClass(field.invalidFeedbackId, "visually-hidden") &
      JS.addClass(field.validFeedbackId, "visually-hidden") &
      JS.addClassToElemsMatchingSelector(s"#${field.aroundId} > .form-check", "is-invalid") &
      JS.addClassToElemsMatchingSelector(s"#${field.aroundId} > .form-check > input", "is-invalid") &
      JS.removeClassFromElemsMatchingSelector(s"#${field.aroundId} > .form-check", "is-valid") &
      JS.removeClassFromElemsMatchingSelector(s"#${field.aroundId} > .form-check > input", "is-valid")

  def hideValidation(
                      field: F7RadioFieldBase[_]
                    )(): Js =
    JS.addClass(field.invalidFeedbackId, "visually-hidden") &
      JS.removeClassFromElemsMatchingSelector(s"#${field.aroundId} > .form-check", "is-invalid") &
      JS.removeClassFromElemsMatchingSelector(s"#${field.aroundId} > .form-check > input", "is-invalid")
}