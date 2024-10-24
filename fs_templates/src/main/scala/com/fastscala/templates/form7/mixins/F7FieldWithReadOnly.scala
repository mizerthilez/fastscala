package com.fastscala.templates.form7.mixins

import scala.util.chaining.scalaUtilChainingOps
import scala.xml.Elem

import com.fastscala.xml.scala_xml.ScalaXmlElemUtils.RichElem

trait F7FieldWithReadOnly extends F7FieldInputFieldMixin:
  var _readOnly: () => Boolean = () => false

  def readOnly = _readOnly()

  def isReadOnly: this.type = readOnly(true)

  def isNotReadOnly: this.type = readOnly(false)

  def readOnly(v: Boolean): this.type = mutate:
    _readOnly = () => v

  def readOnly(f: () => Boolean): this.type = mutate:
    _readOnly = f

  override def processInputElem(input: Elem): Elem = super
    .processInputElem(input)
    .pipe: input =>
      if _readOnly() then input.withAttr("readonly", "true") else input
