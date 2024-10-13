package com.fastscala.templates.form7.mixins

import scala.xml.NodeSeq

import com.fastscala.templates.form7.F7Field

trait StringSerializableF7Field extends StandardF7Field:
  def loadFromString(str: String): Seq[(F7Field, NodeSeq)]

  def saveToString(): Option[String]
