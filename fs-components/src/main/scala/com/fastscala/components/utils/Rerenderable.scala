package com.fastscala.components.utils

import scala.xml.NodeSeq

import com.fastscala.js.Js
import com.fastscala.xml.scala_xml.JS

trait Rerenderable extends ElemWithRandomId:
  def render(): NodeSeq

  def rerender(): Js = JS.replace(elemId, render())
