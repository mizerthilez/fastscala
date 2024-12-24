package com.fastscala.components.form7.fields.select

import scala.util.{ Failure, Success }
import scala.xml.{ Elem, NodeSeq }

import com.fastscala.core.FSContext
import com.fastscala.js.Js
import com.fastscala.components.form7.*
import com.fastscala.components.form7.mixins.*
import com.fastscala.components.form7.renderers.*
import com.fastscala.xml.scala_xml.FSScalaXmlEnv

trait F7SelectFieldBase[T](using val renderer: SelectF7FieldRenderer)
    extends StandardOneInputElemF7Field[T]
       with F7FieldWithOptions[T]
       with F7FieldWithOptionIds[T]
       with F7FieldWithOptionsNsLabel[T]
       with StringSerializableF7Field
       with FocusableF7Field
       with F7FieldWithDisabled
       with F7FieldWithRequired
       with F7FieldWithReadOnly
       with F7FieldWithEnabled
       with F7FieldWithOnChangedField
       with F7FieldWithTabIndex
       with F7FieldWithValidFeedback
       with F7FieldWithHelp
       with F7FieldWithName
       with F7FieldWithLabel
       with F7FieldWithAdditionalAttrs
       with F7FieldWithDependencies:
  def loadFromString(str: String): Seq[(F7Field, NodeSeq)] =
    val all = options
    all.find:
      case opt => _option2Id(opt, all) == str
    match
      case Some(v) =>
        currentValue = v
        _setter(v)
        Nil
      case None =>
        List((this, FSScalaXmlEnv.buildText(s"Not found id: '$str'")))

  def saveToString(): Option[String] = Some(_option2Id(currentValue, options)).filter(_ != "")

  override def submit()(implicit form: Form7, fsc: FSContext): Js = super.submit() & _setter(currentValue)

  def focusJs: Js = Js.focus(elemId) & Js.select(elemId)

  override def updateFieldWithoutReRendering()(using form: Form7, fsc: FSContext, hints: Seq[RenderHint]) =
    super
      .updateFieldWithoutReRendering()
      .flatMap: superJs =>
        currentRenderedOptions
          .map:
            case (renderedOptions, ids2Option, option2Id)
                 if !currentRenderedValue.exists(_ == currentValue) =>
              option2Id
                .get(currentValue)
                .map: valueId =>
                  currentRenderedValue = Some(currentValue)
                  Success(superJs & Js.setElementValue(elemId, valueId))
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
        val optionsRendered = renderedOptions.map: opt =>
          renderer.renderOption(currentValue == opt, option2Id(opt), _option2NodeSeq(opt))

        val onchangeJs = fsc
          .callback(
            Js.elementValueById(elemId),
            ids2Option.get(_) match
              case Some(value) if currentValue == value => Js.void
              case Some(value) =>
                currentRenderedValue = Some(value)
                setFilled()
                currentValue = value
                form.onEvent(ChangedField(this))
              case None =>
                // Log error
                Js.void,
          )
          .cmd
        renderer.render(this)(
          inputElem = processInputElem(<select
              onblur={onchangeJs}
              onchange={onchangeJs}
            >{optionsRendered}</select>),
          label = label,
          invalidFeedback = errorsToShow.headOption.map(error => <div>{error._2}</div>),
          validFeedback = if errorsToShow.isEmpty then validFeedback else None,
          help = help,
        )
