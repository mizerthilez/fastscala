package com.fastscala.templates.bootstrap5.classes

import com.fastscala.xml.scala_xml.{FSScalaXmlEnv, ScalaXmlElemUtils}

import scala.xml.Elem

object BSHelpers extends BSClassesHelper[Elem] with BasicElemsHelper:

  override protected def withClass(clas: String): Elem =
    FSScalaXmlEnv.buildElem("div")().addClass(clas)

  given Conversion[Elem, ScalaXmlElemUtils with BSClassesHelper[Elem]] =
    el => new ScalaXmlElemUtils with BSClassesHelper[Elem]:
      def elem: Elem = el

  given Conversion[String, BSClassesHelper[String]] =
    classes => new BSClassesHelper[String]:
      override protected def withClass(clas: String): String = classes.trim + " " + clas.trim

