package com.fastscala.templates.form7.mixins

import scala.util.chaining.scalaUtilChainingOps
import scala.xml.Elem

import com.fastscala.core.FSContext
import com.fastscala.js.Js
import com.fastscala.templates.form7.{ Form7, F7Field, RenderHint }
import com.fastscala.xml.scala_xml.ScalaXmlElemUtils.RichElem

trait F7FieldWithReadOnly extends F7Field with F7FieldInputFieldMixin:
  var _readOnly: () => Boolean = () => false

  def readOnly: Boolean = _readOnly()

  def isReadOnly: this.type = readOnly(true)

  def isNotReadOnly: this.type = readOnly(false)

  def readOnly(v: Boolean): this.type = mutate:
    _readOnly = () => v

  def readOnly(f: () => Boolean): this.type = mutate:
    _readOnly = f

  var currentlyReadOnly: Boolean = false

  override def processInputElem(input: Elem): Elem = super
    .processInputElem(input)
    .pipe: input =>
      currentlyReadOnly = _readOnly()
      if currentlyReadOnly then input.withAttr("readonly", "true") else input

  def updateFieldReadOnlyStatus()(using Form7, FSContext, Seq[RenderHint]): Js =
    _readOnly().pipe: shouldBeReadOnly =>
      if shouldBeReadOnly != currentlyReadOnly then
        currentlyReadOnly = shouldBeReadOnly
        if currentlyReadOnly then Js.setAttr(elemId)("readonly", "true")
        else Js.removeAttr(elemId, "readonly")
      else Js.void

  override def updateFieldStatus()(using Form7, FSContext, Seq[RenderHint]): Js =
    super.updateFieldStatus() & updateFieldReadOnlyStatus()
