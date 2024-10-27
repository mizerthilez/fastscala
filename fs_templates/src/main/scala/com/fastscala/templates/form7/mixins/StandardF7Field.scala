package com.fastscala.templates.form7.mixins

import scala.xml.NodeSeq

import com.fastscala.core.FSContext
import com.fastscala.js.Js
import com.fastscala.templates.form7.*
import com.fastscala.templates.form7.renderers.StandardF7FieldRenderer

trait StandardF7Field extends F7FieldWithValidations:
  def renderer: StandardF7FieldRenderer

  var showingValidation = false

  def visible: () => Boolean = () => enabled

  override def updateFieldStatus()(using Form7, FSContext, Seq[RenderHint]): Js =
    super.updateFieldStatus() & updateValidation()

  override def postValidation(errors: Seq[(F7Field, NodeSeq)])(using Form7, FSContext): Js =
    updateValidation()

  def showOrUpdateValidation(ns: NodeSeq): Js

  def hideValidation(): Js

  def updateValidation()(using Form7): Js =
    if shouldShowValidation then
      val errors = this.validate()
      if errors.nonEmpty then
        val validation = errors.headOption.map(error => <div>{error._2}</div>).getOrElse(<div></div>)
        showingValidation = true
        showOrUpdateValidation(validation)
      else
        showingValidation = false
        hideValidation()
    else if showingValidation then
      showingValidation = false
      hideValidation()
    else Js.void

  override def postSubmit()(using Form7, FSContext): Js = super.postSubmit() `&`:
    setFilled()
    Js.void

  def fieldAndChildrenMatchingPredicate(pf: PartialFunction[F7Field, Boolean]): List[F7Field] =
    if pf.applyOrElse(this, _ => false) then List(this) else Nil
