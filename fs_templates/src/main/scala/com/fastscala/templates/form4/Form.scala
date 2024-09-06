package com.fastscala.templates.form4

import scala.xml.NodeSeq

import com.fastscala.core.FSContext
import com.fastscala.js.Js
import com.fastscala.templates.utils.ElemWithRandomId

trait Form extends ElemWithRandomId:
  implicit def form: Form = this

  val rootField: FormField

  def reRender()(implicit fsc: FSContext): Js = rootField.reRender()

  def render()(implicit fsc: FSContext): NodeSeq = rootField.render()
