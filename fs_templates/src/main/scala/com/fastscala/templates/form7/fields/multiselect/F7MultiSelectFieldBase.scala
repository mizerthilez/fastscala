package com.fastscala.templates.form7.fields.multiselect

import scala.xml.{ Elem, NodeSeq }

import com.fastscala.core.FSContext
import com.fastscala.js.Js
import com.fastscala.templates.form7.*
import com.fastscala.templates.form7.mixins.*
import com.fastscala.templates.form7.renderers.*

abstract class F7MultiSelectFieldBase[T](using val renderer: MultiSelectF7FieldRenderer)
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
       with F7FieldWithTabIndex
       with F7FieldWithName
       with F7FieldWithSize
       with F7FieldWithValidFeedback
       with F7FieldWithHelp
       with F7FieldWithLabel
       with F7FieldWithAdditionalAttrs
       with F7FieldWithDependencies
       with F7FieldWithValue[Set[T]]:
  def loadFromString(str: String): Seq[(F7Field, NodeSeq)] =
    val all = options()
    val id2Option: Map[String, T] = all.map(opt => _option2Id(opt, all) -> opt).toMap
    val selected: Seq[T] = str
      .split(";")
      .toList
      .flatMap: id =>
        id2Option.get(id)
    currentValue = selected.toSet
    _setter(currentValue)
    Nil

  def saveToString(): Option[String] = Some(
    currentValue.map(opt => _option2Id(opt, options())).mkString(";")
  )

  override def submit()(using Form7, FSContext): Js = super.submit() & _setter(currentValue)

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
          renderer.renderOption(currentValue.contains(opt), option2Id(opt), _option2NodeSeq(opt))

        val onchangeJs = fsc
          .callback(
            Js.selectedValues(Js.elementById(elemId)),
            ids =>
              val value = ids.split(",").filter(_.trim != "").toSet.map(id => ids2Option(id))
              if currentValue != value then
                setFilled()
                currentValue = value
                form.onEvent(ChangedField(this))
              else Js.void,
          )
          .cmd
        renderer.render(this)(
          inputElem = processInputElem(<select
              multiple="multiple"
              onblur={onchangeJs}
              onchange={onchangeJs}
            >{optionsRendered}</select>),
          label = label,
          invalidFeedback = errorsToShow.headOption.map(error => <div>{error._2}</div>),
          validFeedback = if errorsToShow.isEmpty then validFeedback else None,
          help = help,
        )
