package com.fastscala.templates.form7.fields

import scala.xml.{ Elem, NodeSeq }

import com.fastscala.core.FSContext
import com.fastscala.templates.form7.mixins.*
import com.fastscala.templates.form7.{ F7Field, Form7, RenderHint }

class F7HtmlField(
  gen: => NodeSeq
) extends StandardF7Field
       with F7FieldWithDisabled
       with F7FieldWithReadOnly
       with F7FieldWithDependencies
       with F7FieldWithEnabled:
  override def render()(implicit form: Form7, fsc: FSContext, hints: Seq[RenderHint]): Elem =
    if !enabled then <div style="display:none;" id={aroundId}></div>
    else <div id={aroundId}>{gen}</div>

  override def fieldAndChildreenMatchingPredicate(predicate: PartialFunction[F7Field, Boolean])
    : List[F7Field] = if predicate.applyOrElse[F7Field, Boolean](this, _ => false) then List(this) else Nil
