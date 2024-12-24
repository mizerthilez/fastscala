package com.fastscala.components.form7.mixins

import scala.util.chaining.scalaUtilChainingOps
import scala.xml.Elem

import com.fastscala.xml.scala_xml.ScalaXmlElemUtils.RichElem

trait F7FieldWithSize extends F7FieldInputFieldMixin:
  var _size: () => Option[Int] = () => None

  def size: Option[Int] = _size()

  def size(v: Int): this.type = mutate:
    _size = () => Some(v)

  def size(v: Option[Int]): this.type = mutate:
    _size = () => v

  def size(f: () => Option[Int]): this.type = mutate:
    _size = f

  override def processInputElem(input: Elem): Elem = super
    .processInputElem(input)
    .pipe: input =>
      _size().map(size => input.withAttr("size", size.toString)).getOrElse(input)
