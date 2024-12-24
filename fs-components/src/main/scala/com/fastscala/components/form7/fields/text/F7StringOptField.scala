package com.fastscala.components.form7.fields.text

import scala.xml.NodeSeq

import com.fastscala.components.form7.F7Field
import com.fastscala.components.form7.renderers.*
import com.fastscala.xml.scala_xml.FSScalaXmlEnv

class F7StringOptField(using renderer: TextF7FieldRenderer) extends F7TextFieldBase[Option[String]]:
  def defaultValue: Option[String] = None

  def toString(value: Option[String]): String = value.getOrElse("")

  def fromString(str: String): Either[String, Option[String]] = Right(Some(str).filter(_ != ""))

  override def validate(): Seq[(F7Field, NodeSeq)] = super.validate() `++`:
    if required && currentValue.isEmpty then
      Seq((this, FSScalaXmlEnv.buildText(renderer.defaultRequiredFieldLabel)))
    else Seq()
