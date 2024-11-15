package com.fastscala.xml.scala_xml

import com.fastscala.js.rerenderers.*
import com.fastscala.js.{ Js, JsXmlUtils }

object JS extends JsXmlUtils[FSScalaXmlEnv.type]:
  type ScalaXmlRerenderer = Rerenderer[FSScalaXmlEnv.type]
  type ScalaXmlContentRerenderer = ContentRerenderer[FSScalaXmlEnv.type]
  type ScalaXmlContentRerendererP[T] = ContentRerendererP[FSScalaXmlEnv.type, T]
  type ScalaXmlRerendererP[T] = RerendererP[FSScalaXmlEnv.type, T]

  class Js2RichJsXmlUtilsConversion(val js: Js) extends AnyVal:
    def inScriptTag: env.Elem = JS.inScriptTag(js)

    def printBeforeExec: Js =
      println("> " + js.cmd)
      JS.consoleLog(js.cmd) & js

  given Conversion[Js, Js2RichJsXmlUtilsConversion] = Js2RichJsXmlUtilsConversion(_)
