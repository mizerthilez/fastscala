package com.fastscala.templates.form7.mixins

import scala.util.chaining.scalaUtilChainingOps
import scala.xml.Elem

import com.fastscala.xml.scala_xml.ScalaXmlElemUtils.RichElem

trait F7FieldWithDisabled extends F7FieldInputFieldMixin:
  var _disabled: () => Boolean = () => false

  def disabled = _disabled()

  def isDisabled: this.type = disabled(true)

  def isNotDisabled: this.type = disabled(false)

  def disabled(v: Boolean): this.type = mutate:
    _disabled = () => v

  def disabled(f: () => Boolean): this.type = mutate:
    _disabled = f

  override def processInputElem(input: Elem): Elem = super
    .processInputElem(input)
    .pipe: input =>
      if _disabled() then input.withAttr("disabled", "disabled") else input
