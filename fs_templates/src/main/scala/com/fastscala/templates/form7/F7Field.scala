package com.fastscala.templates.form7

import scala.xml.{ Elem, NodeSeq }

import com.fastscala.core.FSContext
import com.fastscala.js.Js
import com.fastscala.templates.form7.mixins.F7FieldWithState
import com.fastscala.templates.utils.ElemWithRandomId
import com.fastscala.xml.scala_xml.JS

/** A field can contain other fields.
  */
trait F7Field extends F7FieldWithState with ElemWithRandomId:
  val aroundId: String = randomElemId

  def render()(using Form7, FSContext, Seq[RenderHint]): Elem

  def postRenderSetupJs()(using FSContext): Js = Js.void

  def preValidation()(using Form7, FSContext): Js = Js.void

  def validate(): Seq[(F7Field, NodeSeq)] = Nil

  def postValidation(errors: Seq[(F7Field, NodeSeq)])(using Form7, FSContext): Js = Js.void

  def preSubmit()(using Form7, FSContext): Js = Js.void

  def submit()(using Form7, FSContext): Js = Js.void

  def postSubmit()(using Form7, FSContext): Js = Js.void

  def fieldAndChildrenMatchingPredicate(predicate: PartialFunction[F7Field, Boolean]): List[F7Field]

  def onEvent(event: F7Event)(using Form7, FSContext, Seq[RenderHint]): Js = Js.void

  def deps: Set[F7Field]

  def enabled: Boolean

  def disabled: Boolean

  def readOnly: Boolean

  def reRender()(using Form7, FSContext, Seq[RenderHint]): Js = JS.replace(aroundId, render())

  def withFieldRenderHints[T](func: Seq[RenderHint] ?=> T)(using renderHints: Seq[RenderHint]): T =
    import RenderHint.*
    func(
      using List(DisableFieldsHint).filter(_ => disabled) ++
        List(ReadOnlyFieldsHint).filter(_ => readOnly) ++
        renderHints
    )
