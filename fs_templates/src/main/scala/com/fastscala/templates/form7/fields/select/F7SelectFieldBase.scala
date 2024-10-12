package com.fastscala.templates.form7.fields.select

import com.fastscala.core.FSContext
import com.fastscala.js.Js
import com.fastscala.templates.form7._
import com.fastscala.templates.form7.fields.text.F7FieldWithAdditionalAttrs
import com.fastscala.templates.form7.mixins._
import com.fastscala.templates.form7.renderers._
import com.fastscala.xml.scala_xml.FSScalaXmlSupport

import scala.xml.{Elem, NodeSeq}

abstract class F7SelectFieldBase[T]()(implicit renderer: SelectF7FieldRenderer) extends StandardF7Field
  with F7FieldWithOptions[T]
  with F7FieldWithOptionIds[T]
  with F7Field
  with StringSerializableF7Field
  with FocusableF7Field
  with F7FieldWithDisabled
  with F7FieldWithRequired
  with F7FieldWithReadOnly
  with F7FieldWithEnabled
  with F7FieldWithTabIndex
  with F7FieldWithName
  with F7FieldWithLabel
  with F7FieldWithAdditionalAttrs
  with F7FieldWithDependencies
  with F7FieldWithValue[T]
  with F7FieldWithOptionsNsLabel[T] {

  override def loadFromString(str: String): Seq[(F7Field, NodeSeq)] = {
    val all = options()
    all.find({
      case opt => _option2Id(opt, all) == str
    }) match {
      case Some(v) =>
        currentValue = v
        _setter(v)
        Nil
      case None =>
        List((this, FSScalaXmlSupport.fsXmlSupport.buildText(s"Not found id: '$str'")))
    }
  }

  override def saveToString(): Option[String] = Some(_option2Id(currentValue, options())).filter(_ != "")

  override def submit()(implicit form: Form7, fsc: FSContext): Js = super.submit() & _setter(currentValue)

  def focusJs: Js = Js.focus(elemId) & Js.select(elemId)

  def finalAdditionalAttrs: Seq[(String, String)] = additionalAttrs

  def render()(implicit form: Form7, fsc: FSContext, hints: Seq[RenderHint]): Elem = {
    val renderedOptions = options()
    val ids2Option: Map[String, T] = renderedOptions.map(opt => fsc.session.nextID() -> opt).toMap
    val option2Id: Map[T, String] = ids2Option.map(_.swap)
    val optionsRendered = renderedOptions.map(opt => {
      renderer.renderOption(this)(currentValue == opt, option2Id(opt), _option2NodeSeq(opt))
    })

    val errorsAtRenderTime = validate()

    if (!enabled()) <div style="display:none;" id={aroundId}></div>
    else {
      withFieldRenderHints { hints =>
        val onchangeJs = fsc.callback(Js.elementValueById(elemId), {
          case id =>
            ids2Option.get(id).map(value => {
              currentValue = value
              form.onEvent(ChangedField(this)(hints))(form, fsc)
            }).getOrElse(Js.void) &
              (if (hints.contains(ShowValidationsHint) || errorsAtRenderTime.nonEmpty || validate().nonEmpty) reRender()(form, fsc, hints) else Js.void)
        }).cmd
        renderer.render(this)(
          label.map(label => <label for={elemId}>{label}</label>),
          processInputElem(<select
              name={name.getOrElse(null)}
              onblur={onchangeJs}
              onchange={onchangeJs}
              id={elemId}
            >{optionsRendered}</select>),
          errorsAtRenderTime.headOption.map(_._2)
        )(hints)
      }
    }
  }

  override def fieldAndChildreenMatchingPredicate(predicate: PartialFunction[F7Field, Boolean]): List[F7Field] = if (predicate.applyOrElse[F7Field, Boolean](this, _ => false)) List(this) else Nil
}