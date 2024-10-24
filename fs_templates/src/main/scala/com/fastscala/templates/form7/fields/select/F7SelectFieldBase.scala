package com.fastscala.templates.form7.fields.select

import scala.xml.{ Elem, NodeSeq }

import com.fastscala.core.FSContext
import com.fastscala.js.Js
import com.fastscala.templates.form7.*
import com.fastscala.templates.form7.mixins.*
import com.fastscala.templates.form7.renderers.*
import com.fastscala.xml.scala_xml.FSScalaXmlEnv

abstract class F7SelectFieldBase[T](using val renderer: SelectF7FieldRenderer)
    extends StandardOneInputElemF7Field
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
       with F7FieldWithDependencies
       with F7FieldWithValue[T]:
  def loadFromString(str: String): Seq[(F7Field, NodeSeq)] =
    val all = options()
    all.find:
      case opt => _option2Id(opt, all) == str
    match
      case Some(v) =>
        currentValue = v
        _setter(v)
        Nil
      case None =>
        List((this, FSScalaXmlEnv.buildText(s"Not found id: '$str'")))

  def saveToString(): Option[String] = Some(_option2Id(currentValue, options())).filter(_ != "")

  override def submit()(implicit form: Form7, fsc: FSContext): Js = super.submit() & _setter(currentValue)

  def focusJs: Js = Js.focus(elemId) & Js.select(elemId)

  def render()(using form: Form7, fsc: FSContext, hints: Seq[RenderHint]): Elem =
    if !enabled then renderer.renderDisabled(this)
    else
      withFieldRenderHints: hints ?=>
        val errorsToShow: Seq[(F7Field, NodeSeq)] = if shouldShowValidation then validate() else Nil
        showingValidation = errorsToShow.nonEmpty

        val renderedOptions = options()
        val ids2Option: Map[String, T] = renderedOptions.map(opt => fsc.session.nextID() -> opt).toMap
        val option2Id: Map[T, String] = ids2Option.map(_.swap)
        val optionsRendered = renderedOptions.map: opt =>
          renderer.renderOption(currentValue == opt, option2Id(opt), _option2NodeSeq(opt))

        val onchangeJs = fsc
          .callback(
            Js.elementValueById(elemId),
            ids2Option.get(_) match
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
          inputElem = processInputElem(<select
              onblur={onchangeJs}
              onchange={onchangeJs}
            >{optionsRendered}</select>),
          label = label,
          invalidFeedback = errorsToShow.headOption.map(error => <div>{error._2}</div>),
          validFeedback = if errorsToShow.isEmpty then validFeedback else None,
          help = help,
        )
