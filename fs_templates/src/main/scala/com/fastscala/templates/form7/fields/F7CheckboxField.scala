package com.fastscala.templates.form7.fields

import scala.xml.{ Elem, NodeSeq }

import com.fastscala.core.FSContext
import com.fastscala.js.Js
import com.fastscala.templates.form7.*
import com.fastscala.templates.form7.fields.text.F7FieldWithAdditionalAttrs
import com.fastscala.templates.form7.mixins.*
import com.fastscala.templates.form7.renderers.*
import com.fastscala.xml.scala_xml.FSScalaXmlEnv
import com.fastscala.xml.scala_xml.ScalaXmlElemUtils.RichElem

class F7CheckboxField(using renderer: CheckboxF7FieldRenderer)
    extends StandardF7Field
       with StringSerializableF7Field
       with FocusableF7Field
       with F7FieldWithDisabled
       with F7FieldWithReadOnly
       with F7FieldWithEnabled
       with F7FieldWithTabIndex
       with F7FieldWithName
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

  def finalAdditionalAttrs: Seq[(String, String)] = additionalAttrs

  def render()(using form: Form7, fsc: FSContext, hints: Seq[RenderHint]): Elem =
    if !enabled then <div style="display:none;" id={aroundId}></div>
    else
      withFieldRenderHints: hints ?=>
        val onchangeJs = fsc
          .callback(
            Js.checkboxIsCheckedById(elemId),
            str =>
              str.toBooleanOption.foreach(currentValue = _)
              form.onEvent(ChangedField(this)) &
                Js.evalIf(hints.contains(RenderHint.ShowValidationsHint))(reRender()), // TODO: is this wrong? (running on the client side, but should be server?)
          )
          .cmd
        renderer.render(this)(
          _label().map(lbl => <label for={elemId}>{lbl}</label>),
          processInputElem(<input type="checkbox"
              id={elemId}
              onchange={onchangeJs}
              checked={if currentValue then "true" else null}
            ></input>).withAttrs(finalAdditionalAttrs*),
          validate().headOption.map(_._2),
        )

  def fieldAndChildrenMatchingPredicate(pf: PartialFunction[F7Field, Boolean]): List[F7Field] =
    if pf.applyOrElse(this, _ => false) then List(this) else Nil
