package com.fastscala.templates.form7.fields.text

import scala.xml.{ Elem, NodeSeq }

import com.fastscala.core.FSContext
import com.fastscala.js.Js
import com.fastscala.templates.form7.*
import com.fastscala.templates.form7.mixins.*
import com.fastscala.templates.form7.renderers.*
import com.fastscala.xml.scala_xml.FSScalaXmlEnv
import com.fastscala.xml.scala_xml.ScalaXmlElemUtils.RichElem

abstract class F7TextareaField[T](using renderer: TextareaF7FieldRenderer)
    extends StandardF7Field
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

  def finalAdditionalAttrs: Seq[(String, String)] = additionalAttrs

  def render()(using form: Form7, fsc: FSContext, hints: Seq[RenderHint]): Elem =
    if !enabled then <div style="display:none;" id={aroundId}></div>
    else
      withFieldRenderHints: hints ?=>
        import RenderHint.*
        val onblusJs = fsc
          .callback(
            Js.elementValueById(elemId),
            str =>
              fromString(str).foreach(currentValue = _)
              form.onEvent(ChangedField(this)) &
                Js.evalIf(hints.contains(ShowValidationsHint))(reRender()), // TODO: is this wrong? (running on the client side, but should be server?)
          )
          .cmd
        val onkeypressJs =
          s"event = event || window.event; if ((event.keyCode ? event.keyCode : event.which) == 13 && event.ctrlKey) {${Js.evalIf(hints.contains(SaveOnEnterHint))(Js.blur(elemId) & form.submitFormClientSide())}}"

        renderer.render(this)(
          _label().map(lbl => <label for={elemId}>{lbl}</label>),
          processInputElem(<textarea
                      type="text"
                      id={elemId}
                      onblur={onblusJs}
                      onkeypress={onkeypressJs}
            >{this.toString(currentValue)}</textarea>).withAttrs(finalAdditionalAttrs*),
          validate().headOption.map(_._2),
        )

  def fieldAndChildrenMatchingPredicate(pf: PartialFunction[F7Field, Boolean]): List[F7Field] =
    if pf.applyOrElse(this, _ => false) then List(this) else Nil
