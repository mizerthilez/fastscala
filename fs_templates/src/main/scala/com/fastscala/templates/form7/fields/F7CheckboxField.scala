package com.fastscala.templates.form7.fields

import scala.xml.{ Elem, NodeSeq }

import com.fastscala.core.FSContext
import com.fastscala.js.Js
import com.fastscala.templates.form7.*
import com.fastscala.templates.form7.mixins.*
import com.fastscala.templates.form7.renderers.*
import com.fastscala.xml.scala_xml.FSScalaXmlEnv

class F7CheckboxField(using val renderer: CheckboxF7FieldRenderer)
    extends StandardOneInputElemF7Field
       with StringSerializableF7Field
       with FocusableF7Field
       with F7FieldWithDisabled
       with F7FieldWithReadOnly
       with F7FieldWithEnabled
       with F7FieldWithTabIndex
       with F7FieldWithName
       with F7FieldWithValidFeedback
       with F7FieldWithHelp
       with F7FieldWithLabel
       with F7FieldWithAdditionalAttrs
       with F7FieldWithDependencies
       with F7FieldWithValue[Boolean]:
  def defaultValue: Boolean = false

  def loadFromString(str: String): Seq[(F7Field, NodeSeq)] = str.toBooleanOption match
    case Some(value) =>
      currentValue = value
      _setter(currentValue)
      Nil
    case None =>
      List((this, FSScalaXmlEnv.buildText(s"Could not parse value '$str' as boolean")))

  def saveToString(): Option[String] = Some(currentValue.toString).filter(_ != "")

  override def submit()(using Form7, FSContext): Js = super.submit() & _setter(currentValue)

  def focusJs: Js = Js.focus(elemId) & Js.select(elemId)

  def render()(using form: Form7, fsc: FSContext, hints: Seq[RenderHint]): Elem =
    if !enabled then renderer.renderDisabled(this)
    else
      withFieldRenderHints: hints ?=>
        val errorsToShow: Seq[(F7Field, NodeSeq)] = if shouldShowValidation then validate() else Nil
        showingValidation = errorsToShow.nonEmpty

        val onchangeJs = fsc
          .callback(
            Js.checkboxIsCheckedById(elemId),
            str =>
              str.toBooleanOption match
                case Some(value) if currentValue != value =>
                  setFilled()
                  currentValue = value
                  form.onEvent(ChangedField(this))
                case Some(_) => Js.void
                case None =>
                  // Log error
                  Js.void,
          )
          .cmd
        renderer.render(this)(
          inputElem = processInputElem(<input type="checkbox"
              id={elemId}
              onchange={onchangeJs}
              checked={if currentValue then "true" else null}
            ></input>),
          label = label,
          invalidFeedback = errorsToShow.headOption.map(error => <div>{error._2}</div>),
          validFeedback = if errorsToShow.isEmpty then validFeedback else None,
          help = help,
        )
