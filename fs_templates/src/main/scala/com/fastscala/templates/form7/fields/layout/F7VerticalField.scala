package com.fastscala.templates.form7.fields.layout

import scala.xml.Elem

import com.fastscala.core.FSContext
import com.fastscala.js.Js
import com.fastscala.templates.form7.*
import com.fastscala.templates.form7.mixins.*
import com.fastscala.xml.scala_xml.ScalaXmlNodeSeqUtils.MkNSFromElems
import com.fastscala.xml.scala_xml.{ FSScalaXmlEnv, JS }

class F7VerticalField(children: F7Field*)
    extends F7FieldWithValidations
       with F7FieldWithEnabled
       with F7FieldWithDependencies
       with F7FieldWithDisabled
       with F7FieldWithReadOnly:
  var currentlyEnabled = enabled

  def render()(using Form7, FSContext, Seq[RenderHint]): Elem =
    currentlyEnabled = enabled
    if !currentlyEnabled then <div style="display:none;" id={aroundId}></div>
    else FSScalaXmlEnv.buildElem("div", "id" -> aroundId)(children.map(_.render()).mkNS)

  override def reRender()(using Form7, FSContext, Seq[RenderHint]): Js =
    if enabled != currentlyEnabled then JS.replace(aroundId, render())
    else children.map(_.reRender()).reduceOption(_ & _).getOrElse(Js.void)

  def fieldAndChildrenMatchingPredicate(pf: PartialFunction[F7Field, Boolean]): List[F7Field] =
    List(this).filter(_ => pf.applyOrElse(this, _ => false)) :::
      children.toList.flatMap(_.fieldAndChildrenMatchingPredicate(pf))

  override def onEvent(event: F7Event)(using Form7, FSContext, Seq[RenderHint]): Js =
    super.onEvent(event) & children.map(_.onEvent(event)).reduceOption(_ & _).getOrElse(Js.void)

object F7VerticalField:
  def apply(children: F7Field*) = new F7VerticalField(children*)
