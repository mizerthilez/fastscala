package com.fastscala.components.form7.mixins

import scala.util.chaining.scalaUtilChainingOps
import scala.xml.Elem

import com.fastscala.xml.scala_xml.ScalaXmlElemUtils.RichElem

trait F7FieldWithMax extends F7FieldInputFieldMixin:
  var _max: () => Option[String] = () => None

  def max: Option[String] = _max()

  def max(v: Option[String]): this.type = mutate:
    _max = () => v

  def max(v: String): this.type = mutate:
    _max = () => Some(v)

  def max(f: () => Option[String]): this.type = mutate:
    _max = f

  override def processInputElem(input: Elem): Elem = super
    .processInputElem(input)
    .pipe: input =>
      _max().map(max => input.withAttr("max", max)).getOrElse(input)
