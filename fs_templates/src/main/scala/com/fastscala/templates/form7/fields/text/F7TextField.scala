package com.fastscala.templates.form7.fields.text

import scala.annotation.tailrec
import scala.xml.{ Elem, NodeSeq }

import com.fastscala.core.FSContext
import com.fastscala.js.Js
import com.fastscala.templates.form7.*
import com.fastscala.templates.form7.mixins.*
import com.fastscala.templates.form7.renderers.*
import com.fastscala.xml.scala_xml.FSScalaXmlEnv
import com.fastscala.xml.scala_xml.ScalaXmlElemUtils.RichElem

abstract class F7TextField[T](using renderer: TextF7FieldRenderer)
    extends StandardF7Field
       with StringSerializableF7Field
       with FocusableF7Field
       with F7FieldWithDisabled
       with F7FieldWithRequired
       with F7FieldWithReadOnly
       with F7FieldWithEnabled
       with F7FieldWithTabIndex
       with F7FieldWithName
       with F7FieldWithPlaceholder
       with F7FieldWithLabel
       with F7FieldWithValidFeedback
       with F7FieldWithHelp
       with F7FieldWithMaxlength
       with F7FieldWithInputType
       with F7FieldWithAdditionalAttrs
       with F7FieldWithDependencies
       with F7FieldWithValue[T]:
  var showingValidation = false

  def toString(value: T): String

  def fromString(str: String): Either[String, T]

  def loadFromString(str: String): Seq[(F7Field, NodeSeq)] =
    fromString(str) match
      case Right(value) =>
        currentValue = value
        _setter(currentValue)
        Nil
      case Left(error) =>
        List((this, FSScalaXmlEnv.buildText(s"Could not parse value '$str': $error")))

  def saveToString(): Option[String] = Some(toString(currentValue)).filter(_ != "")

  override def submit()(using Form7, FSContext): Js = super.submit() & _setter(currentValue)

  def focusJs: Js = Js.focus(elemId) & Js.select(elemId)

  def finalAdditionalAttrs: Seq[(String, String)] = additionalAttrs

  override def postValidation(errors: Seq[(F7Field, NodeSeq)])(using Form7, FSContext): Js =
    updateValidation()

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

  def updateValidation()(using Form7): Js =
    val errors = this.validate()
    val shouldShowErrors = shouldShowValidation && errors.nonEmpty
    if shouldShowErrors != showingValidation then
      if shouldShowErrors then
        val validation = errors.headOption.map(error => <div>{error._2}</div>).getOrElse(<div></div>)
        showingValidation = true
        renderer.showValidation(this)(validation)
      else
        showingValidation = false
        renderer.hideValidation(this)()
    else Js.void

  override def postSubmit()(using Form7, FSContext): Js = super.postSubmit() `&`:
    setFilled()
    Js.void

  override def onEvent(event: F7Event)(using Form7, FSContext, Seq[RenderHint]): Js =
    event match
      case ChangedField(f) if f == this => updateValidation()
      case _ => Js.void

  def render()(using form: Form7, fsc: FSContext, hints: Seq[RenderHint]): Elem =
    if !enabled then <div style="display:none;" id={aroundId}></div>
    else
      withFieldRenderHints: hints ?=>
        import RenderHint.*
        val errorsToShow: Seq[(F7Field, NodeSeq)] = if shouldShowValidation then validate() else Nil
        showingValidation = errorsToShow.nonEmpty

        val onblurJs = fsc
          .callback(
            Js.elementValueById(elemId),
            str =>
              if currentValue != str then
                setFilled()
                fromString(str).foreach(currentValue = _)
                form.onEvent(ChangedField(this))
              else Js.void,
          )
          .cmd
        val onkeypressJs =
          s"event = event || window.event; if ((event.keyCode ? event.keyCode : event.which) == 13) {${Js.evalIf(hints.contains(SaveOnEnterHint))(Js.blur(elemId) & form.submitFormClientSide())}}"

        renderer.render(this)(
          inputElem = processInputElem(<input
              type={inputType}
              id={elemId}
              onblur={onblurJs}
              onkeypress={onkeypressJs}
              value={this.toString(currentValue)}
            />).withAttrs(finalAdditionalAttrs*),
          label = _label(),
          invalidFeedback = errorsToShow.headOption.map(error => <div>{error._2}</div>),
          validFeedback = if errorsToShow.isEmpty then validFeedback else None,
          help = help,
        )

  def fieldAndChildrenMatchingPredicate(pf: PartialFunction[F7Field, Boolean]): List[F7Field] =
    if pf.applyOrElse(this, _ => false) then List(this) else Nil
