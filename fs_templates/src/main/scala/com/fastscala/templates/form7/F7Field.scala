package com.fastscala.templates.form7

import scala.annotation.tailrec
import scala.util.{ Success, Try }
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

  def onEvent(event: F7Event)(using form: Form7, fsc: FSContext, hints: Seq[RenderHint]): Js =
    event match
      case ChangedField(field) if deps.contains(field) =>
        updateFieldWithoutReRendering().getOrElse(reRender()) & form.onEvent(ChangedField(this))
      case ChangedField(field) if field == this =>
        updateFieldWithoutReRendering().getOrElse(reRender())
      case _ => Js.void

  /** Tries to update the field without needing to rerender. If
    */
  def updateFieldWithoutReRendering()(using Form7, FSContext, Seq[RenderHint]): Try[Js] = Success(Js.void)

  def deps: Set[F7Field]

  def enabled: Boolean

  def disabled: Boolean

  def reRender()(using Form7, FSContext, Seq[RenderHint]): Js =
    JS.replace(aroundId, render()) & postRenderSetupJs()

  def withFieldRenderHints[T](func: Seq[RenderHint] ?=> T)(using renderHints: Seq[RenderHint]): T =
    func(
      using List(RenderHint.DisableFieldsHint).filter(_ => disabled) ++ renderHints
    )

  def shouldShowValidation(using form: Form7): Boolean =
    @tailrec
    def aux(validationStrategy: F7FormValidationStrategy): Boolean =
      import F7FormValidationStrategy.*
      validationStrategy match
        case ValidateBeforeUserInput => true
        case ValidateEachFieldAfterUserInput =>
          state match
            case F7FieldState.Filled => true
            case F7FieldState.AwaitingInput => aux(ValidateOnAttemptSubmitOnly)
        case ValidateOnAttemptSubmitOnly =>
          form.state match
            case Form7State.ValidationFailed => true
            case _ => false

    aux(form.validationStrategy)
