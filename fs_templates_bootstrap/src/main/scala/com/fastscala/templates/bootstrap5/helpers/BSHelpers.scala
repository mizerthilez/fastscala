package com.fastscala.templates.bootstrap5.helpers

import scala.xml.Elem

import com.fastscala.xml.scala_xml.{ FSScalaXmlEnv, ScalaXmlElemUtils }

object BSHelpers extends BSClassesHelper[Elem] with BasicElemsHelper:
  override protected def withClass(clas: String): Elem =
    FSScalaXmlEnv.buildElem("div")().addClass(clas)

  given Conversion[Elem, ScalaXmlElemUtils & BSClassesHelper[Elem] & BSDataHelper[Elem]] =
    el =>
      new ScalaXmlElemUtils with BSClassesHelper[Elem] with BSDataHelper[Elem]:
        def elem: Elem = el

        override protected def setAttribute(name: String, value: String): Elem = withAttr((name, value))

  given Conversion[String, BSClassesHelper[String]] =
    classes =>
      new BSClassesHelper[String]:
        override protected def withClass(clas: String): String = classes.trim + " " + clas.trim

  given [T <: ClassEnrichable]: Conversion[T, BSClassesHelper[T]] =
    enrichable =>
      new BSClassesHelper[T]:
        override protected def withClass(clas: String): T = enrichable.setClass(clas)

  given [T <: AttrEnrichable]: Conversion[T, BSDataHelper[T]] =
    enrichable =>
      new BSDataHelper[T]:
        override protected def setAttribute(name: String, value: String): T =
          enrichable.setAttribute(name, value)
