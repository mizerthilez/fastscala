package com.fastscala.components.form7.fields

import scala.xml.{ Elem, NodeSeq }

import com.fastscala.core.FSContext
import com.fastscala.js.Js
import com.fastscala.components.form7.*
import com.fastscala.components.form7.mixins.*
import com.fastscala.components.form7.renderers.*
import com.fastscala.xml.scala_xml.JS

class F7CheckboxOptField(using val renderer: CheckboxF7FieldRenderer)
    extends StandardOneInputElemF7Field[Option[Boolean]]
       with StringSerializableF7Field
       with FocusableF7Field
       with F7FieldWithDisabled
       with F7FieldWithEnabled
       with F7FieldWithTabIndex
       with F7FieldWithName
       with F7FieldWithValidFeedback
       with F7FieldWithHelp
       with F7FieldWithLabel
       with F7FieldWithAdditionalAttrs
       with F7FieldWithDependencies
       with F7FieldWithReadOnly:
  var _switchingToUndefinedAllowed: () => Boolean = () => true

  def switchingToUndefinedAllowed: Boolean = _switchingToUndefinedAllowed()

  def allowSwitchingToUndefined: this.type = switchingToUndefinedAllowed(true)

  def disableSwitchingToUndefined: this.type = switchingToUndefinedAllowed(false)

  def switchingToUndefinedAllowed(v: Boolean): this.type = mutate:
    _switchingToUndefinedAllowed = () => v

  def switchingToUndefinedAllowed(f: () => Boolean): this.type = mutate:
    _switchingToUndefinedAllowed = f

  def defaultValue: Option[Boolean] = None

  def loadFromString(str: String): Seq[(F7Field, NodeSeq)] =
    currentValue = str.toBooleanOption
    _setter(currentValue)
    Nil

  def saveToString(): Option[String] = currentValue.map(_.toString)

  override def submit()(using Form7, FSContext): Js = super.submit() & _setter(currentValue)

  def focusJs: Js = Js.focus(elemId) & Js.select(elemId)

  override def postRenderSetupJs()(using FSContext): Js =
    if currentValue == None then JS.setIndeterminate(elemId)
    else Js.void

  override def updateFieldWithoutReRendering()(using Form7, FSContext, Seq[RenderHint]) =
    super.updateFieldWithoutReRendering().map(_ & Js.setCheckboxTo(elemId, currentValue))

  def render()(using form: Form7, fsc: FSContext, hints: Seq[RenderHint]): Elem =
    if !enabled then renderer.renderDisabled(this)
    else
      withFieldRenderHints: hints ?=>
        val errorsToShow: Seq[(F7Field, NodeSeq)] = if shouldShowValidation then validate() else Nil
        showingValidation = errorsToShow.nonEmpty

        val onchangeJs = fsc
          .callback: () =>
            setFilled()
            currentValue match
              case Some(false) if !switchingToUndefinedAllowed => currentValue = Some(true)
              case Some(false) => currentValue = None
              case None => currentValue = Some(true)
              case Some(true) => currentValue = Some(false)
            currentRenderedValue = Some(currentValue)
            form.onEvent(ChangedField(this)) & reRender()
          .cmd

        currentRenderedValue = Some(currentValue)

        renderer.render(this)(
          inputElem = processInputElem(<input type="checkbox"
               id={elemId}
               onchange={onchangeJs}
               checked={if currentRenderedValue.get == Some(true) then "true" else null}
            ></input>),
          label = label,
          invalidFeedback = errorsToShow.headOption.map(error => <div>{error._2}</div>),
          validFeedback = if errorsToShow.isEmpty then validFeedback else None,
          help = help,
        )
