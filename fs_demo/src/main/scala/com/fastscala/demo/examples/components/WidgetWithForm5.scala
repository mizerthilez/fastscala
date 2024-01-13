package com.fastscala.demo.examples.components

import com.fastscala.core.FSContext
import com.fastscala.js.Js
import com.fastscala.templates.form5.{Form5, FormRenderer}
import com.fastscala.utils.IdGen

import scala.util.chaining.scalaUtilChainingOps
import scala.xml.{Elem, NodeSeq}

abstract class WidgetWithForm5(
                                val widgetTitle: String
                              )(implicit val formRenderer: FormRenderer) extends Widget with Form5 {

  override def widgetContents()(implicit fsc: FSContext): NodeSeq = render()

}
