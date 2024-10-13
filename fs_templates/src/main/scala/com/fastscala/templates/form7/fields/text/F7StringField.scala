package com.fastscala.templates.form7.fields.text

import scala.xml.NodeSeq

import com.fastscala.templates.form7.F7Field
import com.fastscala.templates.form7.renderers.*
import com.fastscala.xml.scala_xml.FSScalaXmlEnv

class F7StringField()(implicit renderer: TextF7FieldRenderer) extends F7TextField[String]:
  override def defaultValue: String = ""

  def toString(value: String): String = value

  def fromString(str: String): Either[String, String] = Right(str)

  override def validate(): Seq[(F7Field, NodeSeq)] = super.validate() ++
    (if _required() && currentValue.trim == "" then
       Seq((this, FSScalaXmlEnv.buildText(renderer.defaultRequiredFieldLabel)))
     else Seq())
