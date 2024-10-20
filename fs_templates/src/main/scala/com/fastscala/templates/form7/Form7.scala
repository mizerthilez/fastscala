package com.fastscala.templates.form7

import scala.util.chaining.scalaUtilChainingOps
import scala.xml.{ Elem, NodeSeq }

import com.fastscala.core.FSContext
import com.fastscala.js.Js
import com.fastscala.templates.form7.formmixins.F7FormWithValidationStrategy
import com.fastscala.templates.form7.mixins.FocusableF7Field
import com.fastscala.templates.utils.ElemWithRandomId
import com.fastscala.utils.RenderableWithFSContext
import com.fastscala.xml.scala_xml.ScalaXmlElemUtils.RichElem
import com.fastscala.xml.scala_xml.{ FSScalaXmlEnv, JS, given }

trait F7FormRenderer:
  def render(form: Elem): NodeSeq

abstract class DefaultForm7(using val formRenderer: F7FormRenderer) extends Form7

enum Form7State:
  case Filling, ValidationFailed, Saved

trait Form7
    extends RenderableWithFSContext[FSScalaXmlEnv.type]
       with ElemWithRandomId
       with F7FormWithValidationStrategy:
  given form: this.type = this

  var state = Form7State.Filling

  lazy val rootField: F7Field

  def initForm()(using FSContext): Unit = ()

  def formRenderHints(): Seq[RenderHint] = Nil

  def onEvent(event: F7Event)(using Form7, FSContext): Js =
    given Seq[RenderHint] = formRenderHints()
    event match
      case RequestedSubmit(_) => submitFormServerSide()
      case ChangedField(_) =>
        state = Form7State.Filling
      case _ =>
    rootField.onEvent(event)

  def formRenderer: F7FormRenderer

  def focusFirstFocusableFieldJs(): Js =
    rootField
      .fieldAndChildrenMatchingPredicate:
        case _: FocusableF7Field => true
      .collectFirst:
        case fff: FocusableF7Field => fff
      .map(_.focusJs)
      .getOrElse(Js.void)

  def render()(using FSContext): Elem =
    given Seq[RenderHint] = formRenderHints()
    val rendered = rootField.render()
    if postRenderSetupJs() != Js.void then
      rendered.withAppendedToContents(JS.inScriptTag(postRenderSetupJs().onDOMContentLoaded))
    else rendered

  /** Used to run JS to initialize the form after it is rendered or re-rendered.
    */
  def postRenderSetupJs()(using FSContext): Js =
    rootField
      .fieldAndChildrenMatchingPredicate(_.enabled)
      .map(_.postRenderSetupJs())
      .reduceOption(_ & _)
      .getOrElse(Js.void)

  def reRender()(using FSContext): Js =
    given Seq[RenderHint] = formRenderHints()
    rootField.reRender() & postRenderSetupJs()

  def preValidateForm()(using FSContext): Js = Js.void

  def postValidateForm(errors: List[(F7Field, NodeSeq)])(using FSContext): Js = Js.void

  def preSubmitForm()(using FSContext): Js = Js.void

  def postSubmitForm()(using FSContext): Js = Js.void

  def submitFormClientSide()(using fsc: FSContext): Js =
    fsc.page.rootFSContext.callback(() => submitFormServerSide())

  def submitFormServerSide()(using fsc: FSContext): Js =
    if fsc != fsc.page.rootFSContext then submitFormServerSide()(using fsc.page.rootFSContext)
    else
      val enabledFields = rootField.fieldAndChildrenMatchingPredicate(_.enabled)
      preValidateForm() &
        enabledFields.map(_.preValidation()).reduceOption(_ & _).getOrElse(Js.void) &
        enabledFields
          .collect(_.validate())
          .flatten
          .pipe: errors =>
            if errors.nonEmpty then state = Form7State.ValidationFailed
            enabledFields.map(_.postValidation(errors)).reduceOption(_ & _).getOrElse(Js.void) &
              postValidateForm(errors) &
              (if errors.isEmpty then
                 try
                   savePipeline(enabledFields)
                 finally
                   state = Form7State.Saved
               else Js.void)

  private def savePipeline(enabledFields: List[F7Field])(using FSContext): Js =
    preSubmitForm() &
      enabledFields.map(_.preSubmit()).reduceOption(_ & _).getOrElse(Js.void) &
      enabledFields.map(_.submit()).reduceOption(_ & _).getOrElse(Js.void) &
      enabledFields.map(_.postSubmit()).reduceOption(_ & _).getOrElse(Js.void) &
      postSubmitForm() // &      rootField.reRender()
