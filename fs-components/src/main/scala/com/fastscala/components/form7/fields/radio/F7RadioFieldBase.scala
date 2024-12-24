package com.fastscala.components.form7.fields.radio

import scala.util.chaining.*
import scala.util.{ Failure, Success }
import scala.xml.{ Elem, NodeSeq }

import com.fastscala.core.FSContext
import com.fastscala.js.Js
import com.fastscala.components.form7.*
import com.fastscala.components.form7.mixins.*
import com.fastscala.components.form7.renderers.*
import com.fastscala.utils.IdGen
import com.fastscala.xml.scala_xml.FSScalaXmlEnv

trait F7RadioFieldBase[T](using val renderer: RadioF7FieldRenderer)
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
    val all = options
    all.find(_option2Id(_, all) == str) match
      case Some(v) =>
        currentValue = v
        _setter(v)
        Nil
      case None =>
        List((this, FSScalaXmlEnv.buildText(s"Not found id: '$str'")))

  def saveToString(): Option[String] = Some(_option2Id(currentValue, options)).filter(_ != "")

  override def submit()(using Form7, FSContext): Js = super.submit() & _setter(currentValue)

  def focusJs: Js = Js.focus(elemId) & Js.select(elemId)

  override def updateFieldReadOnlyStatus()(using Form7, FSContext, Seq[RenderHint]): Js =
    readOnly.pipe: shouldBeReadOnly =>
      if shouldBeReadOnly != currentlyReadOnly then
        currentlyReadOnly = shouldBeReadOnly
        if currentlyReadOnly then
          Js:
            s"""Array.from(document.querySelectorAll('#${aroundId} [name=$radioNameId]')).forEach((elem,idx) => { elem.setAttribute('readonly', 'true') });"""
        else
          Js:
            s"""Array.from(document.querySelectorAll('#${aroundId} [name=$radioNameId]')).forEach((elem,idx) => { elem.removeAttribute('readonly') });"""
      else Js.void

  override def updateFieldDisabledStatus()(using Form7, FSContext, Seq[RenderHint]): Js =
    disabled.pipe: shouldBeDisabled =>
      if shouldBeDisabled != currentlyDisabled then
        currentlyDisabled = shouldBeDisabled
        if currentlyDisabled then
          Js:
            s"""Array.from(document.querySelectorAll('#${aroundId} [name=$radioNameId]')).forEach((elem,idx) => { elem.setAttribute('disabled', 'disabled') });"""
        else
          Js:
            s"""Array.from(document.querySelectorAll('#${aroundId} [name=$radioNameId]')).forEach((elem,idx) => { elem.removeAttribute('disabled') });"""
      else Js.void

  override def updateFieldWithoutReRendering()(using Form7, FSContext, Seq[RenderHint]) =
    super
      .updateFieldWithoutReRendering()
      .flatMap: superJs =>
        currentRenderedOptions
          .map:
            case (renderedOptions, ids2Option, option2Id)
                 if !currentRenderedValue.exists(_ == currentValue) =>
              option2Id
                .get(currentValue)
                .map: optionId =>
                  currentRenderedValue = Some(currentValue)
                  Success(superJs & Js.setChecked(optionId, true))
                .getOrElse(Failure(Exception("CurrentValue is not one of the rendered values")))
            case _ => Success(superJs)
          .getOrElse(Success(superJs))

  def render()(using form: Form7, fsc: FSContext, hints: Seq[RenderHint]): Elem =
    if !enabled then renderer.renderDisabled(this)
    else
      withFieldRenderHints: hints ?=>
        val errorsToShow: Seq[(F7Field, NodeSeq)] = if shouldShowValidation then validate() else Nil
        showingValidation = errorsToShow.nonEmpty

        val renderedOptions: Seq[T] = options
        val ids2Option: Map[String, T] = renderedOptions.map(opt => fsc.session.nextID() -> opt).toMap
        val option2Id: Map[T, String] = ids2Option.map(_.swap)
        currentRenderedOptions = Some(renderedOptions, ids2Option, option2Id)

        if !renderedOptions.contains(currentValue) then currentValue = defaultValue

        currentRenderedValue = Some(currentValue)
        val radioToggles: Seq[(Elem, Some[Elem])] = renderedOptions.map: opt =>
          val onchangeJs = fsc
            .callback: () =>
              if currentValue != opt then
                setFilled()
                currentRenderedValue = Some(opt)
                currentValue = opt
                form.onEvent(ChangedField(this))
              else Js.void
            .cmd
          (
            processInputElem(<input
                type="radio"
                id={option2Id(opt)}
                checked={if currentValue == opt then "checked" else null}
                onchange={onchangeJs}
                name={radioNameId}></input>),
            Some(<label>{_option2NodeSeq(opt)}</label>),
          )

        renderer.render(this)(
          inputElemsAndLabels = radioToggles,
          label = label,
          invalidFeedback = errorsToShow.headOption.map(error => <div>{error._2}</div>),
          validFeedback = if errorsToShow.isEmpty then validFeedback else None,
          help = help,
        )

  override def showOrUpdateValidation(ns: NodeSeq): Js = renderer.showOrUpdateValidation(this)(ns)

  override def hideValidation(): Js = renderer.hideValidation(this)
