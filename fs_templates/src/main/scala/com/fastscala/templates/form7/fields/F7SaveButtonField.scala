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
  using Conversion[B, Elem]
)(
  btn: FSContext => B,
  val toInitialState: B => B = identity[B],
  val toChangedState: B => B = identity[B],
  val toErrorState: B => B = identity[B],
)(implicit renderer: ButtonF7FieldRenderer
) extends StandardF7Field
       with F7FieldWithReadOnly
       with F7FieldWithDependencies
       with F7FieldWithDisabled
       with F7FieldWithEnabled:
  override def fieldAndChildreenMatchingPredicate(predicate: PartialFunction[F7Field, Boolean])
    : List[F7Field] = if predicate.applyOrElse[F7Field, Boolean](this, _ => false) then List(this) else Nil

  val btnRenderer = JS.rerenderableP[(B => B, Form7)](_ =>
    implicit fsc => {
      case (transformer, form) =>
        (transformer(btn(fsc)): Elem)
          .withId(elemId)
          .addOnClick((Js.focus(elemId) & form.submitFormClientSide()).cmd)
    }
  )

  override def render()(implicit form: Form7, fsc: FSContext, hints: Seq[RenderHint]): Elem =
    if !enabled then <div style="display:none;" id={aroundId}></div>
    else
      withFieldRenderHints:
        implicit hints =>
          renderer.render(this) {
            if hints.contains(FailedSaveStateHint) then btnRenderer.render((toErrorState, form))
            else btnRenderer.render((toInitialState, form))
          }(hints)
