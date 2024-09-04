package com.fastscala.xml.scala_xml

import com.fastscala.js.{Js, JsXmlUtils, RichJsXmlUtils}
import com.fastscala.js.rerenderers.{ContentRerenderer, ContentRerendererP, Rerenderer, RerendererP}

object JS extends JsXmlUtils[FSScalaXmlEnv.type]:
  type ScalaXmlRerenderer = Rerenderer[FSScalaXmlEnv.type]
  type ScalaXmlContentRerenderer = ContentRerenderer[FSScalaXmlEnv.type]
  type ScalaXmlContentRerendererP[T] = ContentRerendererP[FSScalaXmlEnv.type, T]
  type ScalaXmlRerendererP[T] = RerendererP[FSScalaXmlEnv.type, T]

  given Conversion[Js, RichJsXmlUtils[FSScalaXmlEnv.type]] = RichJsXmlUtils(_, JS)
