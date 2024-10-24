package com.fastscala.templates.form7.fields.text

import scala.xml.{ Elem, NodeSeq }

import com.fastscala.core.FSContext
import com.fastscala.js.Js
import com.fastscala.templates.form7.*
import com.fastscala.templates.form7.mixins.*
import com.fastscala.templates.form7.renderers.*
import com.fastscala.xml.scala_xml.FSScalaXmlEnv

abstract class F7TextareaFieldBase[T](using val renderer: TextareaF7FieldRenderer)
    extends StandardOneInputElemF7Field
       with F7Field
       with StringSerializableF7Field
       with FocusableF7Field
       with F7FieldWithNumRows
       with F7FieldWithDisabled
       with F7FieldWithRequired
       with F7FieldWithReadOnly
       with F7FieldWithEnabled
       with F7FieldWithTabIndex
       with F7FieldWithName
       with F7FieldWithPlaceholder
       with F7FieldWithLabel
       with F7FieldWithValidFeedback
       with F7FieldWithHelp
       with F7FieldWithMaxlength
       with F7FieldWithInputType
       with F7FieldWithAdditionalAttrs
       with F7FieldWithDependencies
       with F7FieldWithValue[T]:
  def toString(value: T): String

  def fromString(str: String): Either[String, T]

  def loadFromString(str: String): Seq[(F7Field, NodeSeq)] =
    fromString(str) match
      case Right(value) =>
        currentValue = value
        _setter(currentValue)
        Nil
      case Left(error) =>
        List((this, FSScalaXmlEnv.buildText(s"Could not parse value '$str': $error")))

  def saveToString(): Option[String] = Some(toString(currentValue)).filter(_ != "")

  override def submit()(using Form7, FSContext): Js = super.submit() & _setter(currentValue)

  def focusJs: Js = Js.focus(elemId) & Js.select(elemId)

  def render()(using form: Form7, fsc: FSContext, hints: Seq[RenderHint]): Elem =
    if !enabled then renderer.renderDisabled(this)
    else
      withFieldRenderHints: hints ?=>
        val errorsToShow: Seq[(F7Field, NodeSeq)] = if shouldShowValidation then validate() else Nil
        showingValidation = errorsToShow.nonEmpty

        import RenderHint.*
        val onblusJs = fsc
          .callback(
            Js.elementValueById(elemId),
            str =>
              if currentValue != str then
                setFilled()
                fromString(str).foreach(currentValue = _)
                form.onEvent(ChangedField(this))
              else Js.void,
          )
          .cmd
        val onkeypressJs =
          s"event = event || window.event; if ((event.keyCode ? event.keyCode : event.which) == 13 && event.ctrlKey) {${Js.evalIf(hints.contains(SaveOnEnterHint))(Js.blur(elemId) & form.submitFormClientSide())}}"

        renderer.render(this)(
          inputElem = processInputElem(<textarea
              type="text"
              onblur={onblusJs}
              onkeypress={onkeypressJs}
            >{this.toString(currentValue)}</textarea>),
          label = label,
          invalidFeedback = errorsToShow.headOption.map(error => <div>{error._2}</div>),
          validFeedback = if errorsToShow.isEmpty then validFeedback else None,
          help = help,
        )
