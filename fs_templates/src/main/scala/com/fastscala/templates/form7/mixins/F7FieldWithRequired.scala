package com.fastscala.templates.form7.mixins

import scala.util.chaining.scalaUtilChainingOps
import scala.xml.Elem

import com.fastscala.xml.scala_xml.ScalaXmlElemUtils.RichElem

trait F7FieldWithRequired extends F7FieldInputFieldMixin:
  var _required: () => Boolean = () => false

  def required: Boolean = _required()

  def isRequired: this.type = required(true)

  def isNotRequired: this.type = required(false)

  def required(v: Boolean): this.type = mutate:
    _required = () => v

  def required(f: () => Boolean): this.type = mutate:
    _required = f

  override def processInputElem(input: Elem): Elem = super
    .processInputElem(input)
    .pipe: input =>
      if _required() then input.withAttr("required", "true") else input
