package com.fastscala.components.form7.mixins

import scala.xml.NodeSeq

import com.fastscala.components.utils.Mutable
import com.fastscala.xml.scala_xml.FSScalaXmlEnv

trait F7FieldWithOptionsNsLabel[T] extends Mutable:
  var _option2NodeSeq: T => NodeSeq = opt => FSScalaXmlEnv.buildText(opt.toString)

  def option2NodeSeq(f: T => NodeSeq): this.type = mutate:
    _option2NodeSeq = f

  def option2String(f: T => String): this.type = mutate:
    _option2NodeSeq = opt => FSScalaXmlEnv.buildText(f(opt))
