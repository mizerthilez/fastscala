package com.fastscala.components.form6.fields

import scala.xml.{ Elem, NodeSeq }

import com.fastscala.core.FSContext
import com.fastscala.js.Js
import com.fastscala.components.form6.Form6

class F6RawHtmlField(
  gen: => NodeSeq
) extends StandardF6Field
       with F6FieldWithDisabled
       with F6FieldWithReadOnly
       with F6FieldWithDependencies
       with F6FieldWithEnabled:
  override def render()(implicit form: Form6, fsc: FSContext, hints: Seq[RenderHint]): Elem =
    if !enabled then <div style="display:none;" id={aroundId}></div>
    else <div id={aroundId}>{gen}</div>

  override def fieldsMatching(predicate: PartialFunction[F6Field, Boolean]): List[F6Field] =
    if predicate.applyOrElse[F6Field, Boolean](this, _ => false) then List(this) else Nil

class F6SurroundWithHtmlField[T <: F6Field](
  wrap: Elem => Elem
)(
  field: T
) extends StandardF6Field
       with F6FieldWithReadOnly
       with F6FieldWithDependencies
       with F6FieldWithDisabled
       with F6FieldWithEnabled:
  override def render()(implicit form: Form6, fsc: FSContext, hints: Seq[RenderHint]): Elem =
    if !enabled then <div style="display:none;" id={aroundId}></div>
    else <div id={aroundId}>{wrap(field.render())}</div>

  override def fieldsMatching(predicate: PartialFunction[F6Field, Boolean]): List[F6Field] =
    List(this).filter(_ => predicate.applyOrElse[F6Field, Boolean](this, _ => false)) :::
      List(field).flatMap(_.fieldsMatching(predicate))

  override def onEvent(
    event: FormEvent
  )(implicit
    form: Form6,
    fsc: FSContext,
    hints: Seq[RenderHint],
  ): Js = field.onEvent(event)
