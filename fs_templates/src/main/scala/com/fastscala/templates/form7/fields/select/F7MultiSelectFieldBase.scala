package com.fastscala.templates.form7.fields.select

import scala.xml.{ Elem, NodeSeq }

import com.fastscala.core.FSContext
import com.fastscala.js.Js
import com.fastscala.templates.form7.*
import com.fastscala.templates.form7.fields.text.F7FieldWithAdditionalAttrs
import com.fastscala.templates.form7.mixins.*
import com.fastscala.templates.form7.renderers.*

abstract class F7MultiSelectFieldBase[T](using renderer: MultiSelectF7FieldRenderer)
    extends StandardF7Field
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

  def finalAdditionalAttrs: Seq[(String, String)] = additionalAttrs

  def render()(using form: Form7, fsc: FSContext, hints: Seq[RenderHint]): Elem =
    val renderedOptions = options()
    val ids2Option: Map[String, T] = renderedOptions.map(opt => fsc.session.nextID() -> opt).toMap
    val option2Id: Map[T, String] = ids2Option.map(_.swap)
    val optionsRendered = renderedOptions.map: opt =>
      renderer.renderOption(this)(currentValue.contains(opt), option2Id(opt), _option2NodeSeq(opt))

    val errorsAtRenderTime = validate()

    if !enabled then <div style="display:none;" id={aroundId}></div>
    else
      withFieldRenderHints: hints ?=>
        import RenderHint.*
        val onchangeJs = fsc
          .callback(
            Js.selectedValues(Js.elementById(elemId)),
            ids =>
              currentValue = ids.split(",").filter(_.trim != "").toSet[String].map(id => ids2Option(id))
              form.onEvent(ChangedField(this)) `&`:
                if hints.contains(ShowValidationsHint) || errorsAtRenderTime.nonEmpty || validate().nonEmpty
                then reRender() & Js.focus(elemId)
                else Js.void,
          )
          .cmd
        renderer.render(this)(
          label.map(label => <label for={elemId}>{label}</label>),
          processInputElem(<select
              multiple="multiple"
              name={name.getOrElse(null)}
              onblur={onchangeJs}
              onchange={onchangeJs}
              id={elemId}
            >{optionsRendered}</select>),
          errorsAtRenderTime.headOption.map(_._2),
        )

  def fieldAndChildrenMatchingPredicate(pf: PartialFunction[F7Field, Boolean]): List[F7Field] =
    if pf.applyOrElse(this, _ => false) then List(this) else Nil
