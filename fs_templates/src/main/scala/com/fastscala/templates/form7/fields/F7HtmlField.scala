package com.fastscala.templates.form7.fields

import scala.xml.{ Elem, NodeSeq }

import com.fastscala.core.FSContext
import com.fastscala.templates.form7.mixins.*
import com.fastscala.templates.form7.{ F7Field, Form7, RenderHint }

object F7HtmlField:
  def label(str: String) = new F7HtmlField(<label>{str}</label>)

class F7HtmlField(
  gen: => NodeSeq
) extends F7FieldWithValidations
       with F7FieldWithDisabled
       with F7FieldWithDependencies
       with F7FieldWithEnabled:
  def render()(using Form7, FSContext, Seq[RenderHint]): Elem =
    if !enabled then <div style="display:none;" id={aroundId}></div>
    else <div id={aroundId}>{gen}</div>

  def fieldAndChildrenMatchingPredicate(pf: PartialFunction[F7Field, Boolean]): List[F7Field] =
    if pf.applyOrElse(this, _ => false) then List(this) else Nil
