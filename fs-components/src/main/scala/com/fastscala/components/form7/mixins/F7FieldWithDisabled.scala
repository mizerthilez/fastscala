package com.fastscala.components.form7.mixins

import scala.util.chaining.scalaUtilChainingOps
import scala.xml.Elem

import com.fastscala.core.FSContext
import com.fastscala.js.Js
import com.fastscala.components.form7.{ Form7, F7Field, RenderHint }
import com.fastscala.xml.scala_xml.ScalaXmlElemUtils.RichElem

trait F7FieldWithDisabled extends F7Field with F7FieldInputFieldMixin:
  var _disabled: () => Boolean = () => false

  def disabled: Boolean = _disabled()

  def isDisabled: this.type = disabled(true)

  def isNotDisabled: this.type = disabled(false)

  def disabled(v: Boolean): this.type = mutate:
    _disabled = () => v

  def disabled(f: () => Boolean): this.type = mutate:
    _disabled = f

  var currentlyDisabled: Boolean = false

  override def processInputElem(input: Elem): Elem = super
    .processInputElem(input)
    .pipe: input =>
      currentlyDisabled = _disabled()
      if currentlyDisabled then input.withAttr("disabled", "disabled") else input

  def updateFieldDisabledStatus()(using Form7, FSContext, Seq[RenderHint]): Js =
    _disabled().pipe: shouldBeDisabled =>
      if shouldBeDisabled != currentlyDisabled then
        currentlyDisabled = shouldBeDisabled
        if currentlyDisabled then Js.setAttr(elemId)("disabled", "disabled")
        else Js.removeAttr(elemId, "disabled")
      else Js.void

  override def updateFieldWithoutReRendering()(using Form7, FSContext, Seq[RenderHint]) =
    super.updateFieldWithoutReRendering().map(_ & updateFieldDisabledStatus())
