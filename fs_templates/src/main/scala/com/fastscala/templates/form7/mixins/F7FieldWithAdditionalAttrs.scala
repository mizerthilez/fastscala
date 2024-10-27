package com.fastscala.templates.form7.mixins

import scala.util.chaining.scalaUtilChainingOps
import scala.xml.Elem

import com.fastscala.xml.scala_xml.ScalaXmlElemUtils.RichElem

trait F7FieldWithAdditionalAttrs extends F7FieldInputFieldMixin:
  var _additionalAttrs: () => Seq[(String, String)] = () => Nil

  def additionalAttrs: Seq[(String, String)] = _additionalAttrs()

  def additionalAttrs(v: Seq[(String, String)]): this.type = mutate:
    _additionalAttrs = () => v

  def additionalAttrs(f: () => Seq[(String, String)]): this.type = mutate:
    _additionalAttrs = f

  override def processInputElem(input: Elem): Elem = super
    .processInputElem(input)
    .pipe: input =>
      input.withAttrs(_additionalAttrs()*)
