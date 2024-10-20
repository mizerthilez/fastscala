package com.fastscala.templates.form7.fields.text

import scala.xml.NodeSeq

import com.fastscala.templates.form7.F7Field
import com.fastscala.templates.form7.renderers.*
import com.fastscala.xml.scala_xml.FSScalaXmlEnv

class F7StringTextareaField(using renderer: TextareaF7FieldRenderer) extends F7TextareaField[String]:
  def defaultValue: String = ""

  def toString(value: String): String = value

  def fromString(str: String): Either[String, String] = Right(str)

  override def validate(): Seq[(F7Field, NodeSeq)] = super.validate() `++`:
    if required && currentValue == "" then
      Seq((this, FSScalaXmlEnv.buildText(renderer.defaultRequiredFieldLabel)))
    else Seq()
