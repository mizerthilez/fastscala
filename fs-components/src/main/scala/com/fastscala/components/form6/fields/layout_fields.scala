package com.fastscala.components.form6.fields

import scala.xml.{ Elem, NodeSeq }

import com.fastscala.core.FSContext
import com.fastscala.js.Js
import com.fastscala.components.form6.Form6
import com.fastscala.xml.scala_xml.FSScalaXmlEnv.*
import com.fastscala.xml.scala_xml.JS
import com.fastscala.xml.scala_xml.ScalaXmlNodeSeqUtils.MkNSFromElems

class F6VerticalField()(children: F6Field*)
    extends StandardF6Field
       with F6FieldWithEnabled
       with F6FieldWithDependencies
       with F6FieldWithDisabled
       with F6FieldWithReadOnly:
  var currentlyEnabled = enabled

  override def render()(implicit form: Form6, fsc: FSContext, hints: Seq[RenderHint]): Elem =
    currentlyEnabled = enabled
    if !currentlyEnabled then <div style="display:none;" id={aroundId}></div>
    else buildElem("div", "id" -> aroundId)(children.map(_.render()).mkNS)

  override def reRender()(implicit form: Form6, fsc: FSContext, hints: Seq[RenderHint]): Js =
    if enabled != currentlyEnabled then JS.replace(aroundId, render())
    else children.map(_.reRender()).reduceOption[Js](_ & _).getOrElse(Js.void)

  override def fieldsMatching(predicate: PartialFunction[F6Field, Boolean]): List[F6Field] =
    List(this).filter(_ => predicate.applyOrElse[F6Field, Boolean](this, _ => false)) :::
      children.toList.flatMap(_.fieldsMatching(predicate))

  override def onEvent(
    event: FormEvent
  )(implicit
    form: Form6,
    fsc: FSContext,
    hints: Seq[RenderHint],
  ): Js =
    super.onEvent(event) & children.map(_.onEvent(event)).reduceOption(_ & _).getOrElse(Js.void)

object F6VerticalField:
  def apply()(children: F6Field*) = new F6VerticalField()(children*)

abstract class F6ContainerFieldBase
    extends StandardF6Field
       with F6FieldWithEnabled
       with F6FieldWithDependencies
       with F6FieldWithDisabled
       with F6FieldWithReadOnly:
  def aroundClass: String

  def children: Seq[(String, F6Field)]

  var currentlyEnabled = enabled

  override def render()(implicit form: Form6, fsc: FSContext, hints: Seq[RenderHint]): Elem =
    currentlyEnabled = enabled
    if !currentlyEnabled then <div style="display:none;" id={aroundId}></div>
    else
      withFieldRenderHints:
        implicit hints =>
          val contents = children
            .map:
              case (clas, field) =>
                <div class={clas}>{field.render()}</div>
            .reduceOption[NodeSeq](_ ++ _)
            .getOrElse(NodeSeq.Empty)
          <div class={aroundClass} id={aroundId}>{contents}</div>

  override def reRender()(implicit form: Form6, fsc: FSContext, hints: Seq[RenderHint]): Js =
    if enabled != currentlyEnabled then JS.replace(aroundId, render())
    else children.map(_._2.reRender()).reduceOption[Js](_ & _).getOrElse(Js.void)

  override def fieldsMatching(predicate: PartialFunction[F6Field, Boolean]): List[F6Field] =
    List(this).filter(_ => predicate.applyOrElse[F6Field, Boolean](this, _ => false)) :::
      children.toList.flatMap(_._2.fieldsMatching(predicate))

  override def onEvent(
    event: FormEvent
  )(implicit
    form: Form6,
    fsc: FSContext,
    hints: Seq[RenderHint],
  ): Js =
    super.onEvent(event) & children.map(_._2.onEvent(event)).reduceOption(_ & _).getOrElse(Js.void)

class F6ContainerField(val aroundClass: String)(val children: (String, F6Field)*) extends F6ContainerFieldBase

object F6ContainerField:
  def apply(aroundClass: String)(children: (String, F6Field)*) = new F6ContainerField(aroundClass)(
    children*
  )
