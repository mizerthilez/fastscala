package com.fastscala.templates.form7.fields.layout

import scala.xml.{ Elem, NodeSeq }

import com.fastscala.core.FSContext
import com.fastscala.js.Js
import com.fastscala.templates.form7.*
import com.fastscala.templates.form7.mixins.*
import com.fastscala.xml.scala_xml.JS

abstract class F7ContainerFieldBase
    extends StandardF7Field
       with F7FieldWithEnabled
       with F7FieldWithDependencies
       with F7FieldWithDisabled
       with F7FieldWithReadOnly:
  def aroundClass: String

  def children: Seq[(String, F7Field)]

  var currentlyEnabled = enabled

  def render()(using Form7, FSContext, Seq[RenderHint]): Elem =
    currentlyEnabled = enabled
    if !currentlyEnabled then <div style="display:none;" id={aroundId}></div>
    else
      withFieldRenderHints: hints ?=>
        val contents = children
          .map:
            case (clas, field) =>
              <div class={clas}>{field.render()}</div>
          .reduceOption[NodeSeq](_ ++ _)
          .getOrElse(NodeSeq.Empty)
        <div class={aroundClass} id={aroundId}>{contents}</div>

  override def reRender()(using Form7, FSContext, Seq[RenderHint]): Js =
    if enabled != currentlyEnabled then JS.replace(aroundId, render())
    else children.map(_._2.reRender()).reduceOption(_ & _).getOrElse(Js.void)

  def fieldAndChildrenMatchingPredicate(pf: PartialFunction[F7Field, Boolean]): List[F7Field] =
    List(this).filter(_ => pf.applyOrElse(this, _ => false)) :::
      children.toList.flatMap(_._2.fieldAndChildrenMatchingPredicate(pf))

  override def onEvent(event: F7Event)(using Form7, FSContext, Seq[RenderHint]): Js =
    super.onEvent(event) & children.map(_._2.onEvent(event)).reduceOption(_ & _).getOrElse(Js.void)
