package com.fastscala.templates.form7.fields

import scala.xml.Elem

import com.fastscala.core.FSContext
import com.fastscala.js.Js
import com.fastscala.templates.form7.*
import com.fastscala.templates.form7.mixins.*
import com.fastscala.templates.form7.renderers.*
import com.fastscala.xml.scala_xml.JS
import com.fastscala.xml.scala_xml.ScalaXmlElemUtils.RichElem

class F7SaveButtonField[B](
  btn: FSContext => B,
  val toInitialState: B => B = identity[B],
  val toChangedState: B => B = identity[B],
  val toErrorState: B => B = identity[B],
)(using
  renderer: ButtonF7FieldRenderer,
  conv: Conversion[B, Elem],
) extends F7FieldWithValidations
       with F7FieldWithReadOnly
       with F7FieldWithDependencies
       with F7FieldWithDisabled
       with F7FieldWithEnabled:
  val btnRenderer = JS.rerenderableP[(B => B, Form7)]: _ =>
    implicit fsc =>
      (transformer, form) =>
        (transformer(btn(fsc)): Elem)
          .withId(elemId)
          .addOnClick((Js.focus(elemId) & form.submitFormClientSide()).cmd)

  def render()(using form: Form7, fsc: FSContext, hints: Seq[RenderHint]): Elem =
    if !enabled then <div style="display:none;" id={aroundId}></div>
    else
      withFieldRenderHints: hints ?=>
        import RenderHint.*
        renderer.render(this):
          if hints.contains(FailedSaveStateHint) then btnRenderer.render(toErrorState, form)
          else btnRenderer.render(toInitialState, form)

  def fieldAndChildrenMatchingPredicate(pf: PartialFunction[F7Field, Boolean]): List[F7Field] =
    if pf.applyOrElse(this, _ => false) then List(this) else Nil
