package com.fastscala.components.form7.mixins

import scala.util.chaining.scalaUtilChainingOps
import scala.xml.Elem

import com.fastscala.xml.scala_xml.ScalaXmlElemUtils.RichElem

trait F7FieldWithPlaceholder extends F7FieldInputFieldMixin:
  var _placeholder: () => Option[String] = () => None

  def placeholder: Option[String] = _placeholder()

  def placeholder(v: Option[String]): this.type = mutate:
    _placeholder = () => v

  def placeholder(v: String): this.type = mutate:
    _placeholder = () => Some(v)

  def placeholder(f: () => Option[String]): this.type = mutate:
    _placeholder = f

  override def processInputElem(input: Elem): Elem = super
    .processInputElem(input)
    .pipe: input =>
      _placeholder().map(placeholder => input.withAttr("placeholder", placeholder)).getOrElse(input)
