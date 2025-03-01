package com.fastscala.components.form7.fields

import scala.xml.Elem

import com.fastscala.core.FSContext
import com.fastscala.js.Js
import com.fastscala.components.form7.*
import com.fastscala.components.form7.mixins.*

class F7HtmlSurroundField[T <: F7Field](
  surround: Elem => Elem
)(
  field: T
) extends F7FieldWithValidations
       with F7FieldWithReadOnly
       with F7FieldWithDependencies
       with F7FieldWithDisabled
       with F7FieldWithEnabled:
  def render()(implicit form: Form7, fsc: FSContext, hints: Seq[RenderHint]): Elem =
    if !enabled then <div style="display:none;" id={aroundId}></div>
    else <div id={aroundId}>{surround(field.render())}</div>

  def fieldAndChildrenMatchingPredicate(pf: PartialFunction[F7Field, Boolean]): List[F7Field] =
    List(this).filter(_ => pf.applyOrElse(this, _ => false)) :::
      List(field).flatMap(_.fieldAndChildrenMatchingPredicate(pf))

  override def onEvent(event: F7Event)(implicit form: Form7, fsc: FSContext, hints: Seq[RenderHint]): Js =
    field.onEvent(event)
