package com.fastscala.templates.form7.fields.radio

import scala.xml.NodeSeq

import com.fastscala.templates.form7.F7Field
import com.fastscala.templates.form7.renderers.*
import com.fastscala.xml.scala_xml.FSScalaXmlEnv

class F7RadioOptField[T](using RadioF7FieldRenderer) extends F7RadioFieldBase[Option[T]]:
  def defaultValue: Option[T] = None

  def optionsNonEmpty(v: Seq[T]): this.type = options(None +: v.map(Some(_)))

  override def validate(): Seq[(F7Field, NodeSeq)] = super.validate() `++`:
    if required && currentValue.isEmpty then
      Seq((this, FSScalaXmlEnv.buildText(renderer.defaultRequiredFieldLabel)))
    else Seq()
