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

  given [T <: ClassEnrichableMutable]: Conversion[T, BSClassesHelper[T]] =
    enrichable =>
      new BSClassesHelper[T]:
        override protected def withClass(clas: String): T = enrichable.addClass(clas)

  given [T <: AttrEnrichableMutable]: Conversion[T, BSDataHelper[T]] =
    enrichable =>
      new BSDataHelper[T]:
        override protected def setAttribute(name: String, value: String): T =
          enrichable.setAttribute(name, value)

  given [R]: Conversion[ClassEnrichableImmutable[R], BSClassesHelper[R]] =
    enrichable =>
      new BSClassesHelper[R]:
        override protected def withClass(clas: String): R = enrichable.addClass(clas)

  given [R]: Conversion[AttrEnrichableImmutable[R], BSDataHelper[R]] =
    enrichable =>
      new BSDataHelper[R]:
        override protected def setAttribute(name: String, value: String): R =
          enrichable.setAttribute(name, value)
