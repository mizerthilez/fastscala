package com.fastscala.templates.form7

import scala.xml.{ Elem, NodeSeq }

import com.fastscala.core.FSContext
import com.fastscala.js.Js
import com.fastscala.templates.form7.mixins.F7FieldWithState

/** A field can contain other fields.
  */
trait F7Field extends F7FieldWithState:
  def render()(implicit form: Form7, fsc: FSContext, hints: Seq[RenderHint]): Elem

  def postRenderSetupJs()(implicit fsc: FSContext): Js = Js.void

  def reRender()(implicit form: Form7, fsc: FSContext, hints: Seq[RenderHint]): Js

  def preValidation()(implicit form: Form7, fsc: FSContext): Js = Js.void

  def validate(): Seq[(F7Field, NodeSeq)] = Nil

  def postValidation(errors: Seq[(F7Field, NodeSeq)])(implicit form: Form7, fsc: FSContext): Js = Js.void

  def preSubmit()(implicit form: Form7, fsc: FSContext): Js = Js.void

  def submit()(implicit form: Form7, fsc: FSContext): Js = Js.void

  def postSubmit()(implicit form: Form7, fsc: FSContext): Js = Js.void

  def fieldAndChildreenMatchingPredicate(predicate: PartialFunction[F7Field, Boolean]): List[F7Field]

  def onEvent(event: F7Event)(implicit form: Form7, fsc: FSContext, hints: Seq[RenderHint]): Js = Js.void

  def deps: Set[F7Field]

  def enabled: Boolean
