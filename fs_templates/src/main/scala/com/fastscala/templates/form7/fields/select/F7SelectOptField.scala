package com.fastscala.templates.form7.fields.select

import scala.xml.NodeSeq

import com.fastscala.templates.form7.F7Field
import com.fastscala.templates.form7.renderers.*
import com.fastscala.xml.scala_xml.FSScalaXmlEnv

class F7SelectOptField[T]()(implicit renderer: SelectF7FieldRenderer) extends F7SelectFieldBase[Option[T]]:
  override def defaultValue: Option[T] = None

  def optionsNonEmpty(v: Seq[T]): F7SelectOptField.this.type = options(None +: v.map(Some(_)))

  override def validate(): Seq[(F7Field, NodeSeq)] = super.validate() ++
    (if required && currentValue.isEmpty then
       Seq((this, FSScalaXmlEnv.buildText(renderer.defaultRequiredFieldLabel)))
     else Seq())
