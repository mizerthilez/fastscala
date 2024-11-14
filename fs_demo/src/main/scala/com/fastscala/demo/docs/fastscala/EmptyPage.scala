package com.fastscala.demo.docs.fastscala

import scala.xml.NodeSeq

import com.fastscala.core.FSContext
import com.fastscala.demo.docs.BasePage
import com.fastscala.xml.scala_xml.given

class EmptyPage extends BasePage:
  override def pageTitle: String = "FastScala | Empty page"

  override def renderPageContents()(implicit fsc: FSContext): NodeSeq =
    <p>Empty page for you to do your testing 😁</p>
