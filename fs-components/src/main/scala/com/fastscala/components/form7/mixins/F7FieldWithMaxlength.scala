package com.fastscala.components.form7.mixins

import scala.util.chaining.scalaUtilChainingOps
import scala.xml.Elem

import com.fastscala.xml.scala_xml.ScalaXmlElemUtils.RichElem

trait F7FieldWithMaxlength extends F7FieldInputFieldMixin:
  var _maxlength: () => Option[Int] = () => None

  def maxlength: Option[Int] = _maxlength()

  def maxlength(v: Option[Int]): this.type = mutate:
    _maxlength = () => v

  def maxlength(v: Int): this.type = mutate:
    _maxlength = () => Some(v)

  def maxlength(f: () => Option[Int]): this.type = mutate:
    _maxlength = f

  override def processInputElem(input: Elem): Elem = super
    .processInputElem(input)
    .pipe: input =>
      _maxlength().map(maxlength => input.withAttr("maxlength", maxlength.toString)).getOrElse(input)
