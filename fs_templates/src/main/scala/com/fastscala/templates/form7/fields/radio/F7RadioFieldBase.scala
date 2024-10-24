package com.fastscala.templates.form7.fields.radio

import scala.xml.{ Elem, NodeSeq }

import com.fastscala.core.FSContext
import com.fastscala.js.Js
import com.fastscala.templates.form7.*
import com.fastscala.templates.form7.mixins.*
import com.fastscala.templates.form7.renderers.*
import com.fastscala.utils.IdGen
import com.fastscala.xml.scala_xml.FSScalaXmlEnv

abstract class F7RadioFieldBase[T](using val renderer: RadioF7FieldRenderer)
    extends StandardF7Field
       with StringSerializableF7Field
       with FocusableF7Field
       with F7FieldWithOptions[T]
       with F7FieldWithOptionIds[T]
       with F7FieldWithOptionsNsLabel[T]
       with F7FieldWithDisabled
       with F7FieldWithRequired
       with F7FieldWithReadOnly
       with F7FieldWithEnabled
       with F7FieldWithTabIndex
       with F7FieldWithValidFeedback
       with F7FieldWithHelp
       with F7FieldWithName
       with F7FieldWithLabel
       with F7FieldWithAdditionalAttrs
       with F7FieldWithDependencies
       with F7FieldWithValue[T]:
  val radioNameId = IdGen.id

  def loadFromString(str: String): Seq[(F7Field, NodeSeq)] =
    val all = options()
    all.find(_option2Id(_, all) == str) match
      case Some(v) =>
        currentValue = v
        _setter(v)
        Nil
      case None =>
        List((this, FSScalaXmlEnv.buildText(s"Not found id: '$str'")))

  def saveToString(): Option[String] = Some(_option2Id(currentValue, options())).filter(_ != "")

  override def submit()(using Form7, FSContext): Js = super.submit() & _setter(currentValue)

  def focusJs: Js = Js.focus(elemId) & Js.select(elemId)

  def render()(using form: Form7, fsc: FSContext, hints: Seq[RenderHint]): Elem =
    if !enabled then renderer.renderDisabled(this)
    else
      withFieldRenderHints: hints ?=>
        val errorsToShow: Seq[(F7Field, NodeSeq)] = if shouldShowValidation then validate() else Nil
        showingValidation = errorsToShow.nonEmpty

        val renderedOptions = options()
        val radioToggles: Seq[(Elem, Some[Elem])] = renderedOptions.map: opt =>
          val onchangeJs = fsc
            .callback: () =>
              if currentValue != opt then
                setFilled()
                currentValue = opt
                form.onEvent(ChangedField(this))
              else Js.void
            .cmd
          (
            processInputElem(<input
                type="radio"
                checked={if currentValue == opt then "checked" else null}
                onchange={onchangeJs}
                name={radioNameId}></input>),
            Some(<label>{_option2NodeSeq(opt)}</label>),
          )

        renderer.render(this)(
          inputElemsAndLabels = radioToggles,
          invalidFeedback = errorsToShow.headOption.map(error => <div>{error._2}</div>),
          validFeedback = if errorsToShow.isEmpty then validFeedback else None,
          help = help,
        )

  override def showOrUpdateValidation(ns: NodeSeq): Js = renderer.showOrUpdateValidation(this)(ns)

  override def hideValidation(): Js = renderer.hideValidation(this)
