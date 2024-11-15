package com.fastscala.templates.bootstrap5.helpers

import scala.xml.Elem

import com.fastscala.xml.scala_xml.{ FSScalaXmlEnv, ScalaXmlElemUtils }

object BSHelpers extends BSClassesHelper[Elem] with BasicElemsHelper:
  override protected def withClass(clas: String): Elem =
    FSScalaXmlEnv.buildElem("div")().addClass(clas)

  class Elem2ScalaXmlElemUtilsConversion(val el: Elem)
      extends AnyVal
         with ScalaXmlElemUtils
         with BSClassesHelper[Elem]
         with BSDataHelper[Elem]:
    def elem: Elem = el
    protected def setAttribute(name: String, value: String): Elem = withAttr((name, value))

  given Conversion[Elem, ScalaXmlElemUtils & BSClassesHelper[Elem] & BSDataHelper[Elem]] =
    Elem2ScalaXmlElemUtilsConversion(_)

  class String2BSClassesHelperConversion(val classes: String) extends AnyVal with BSClassesHelper[String]:
    protected def withClass(clas: String): String = classes.trim + " " + clas.trim

  given Conversion[String, BSClassesHelper[String]] = String2BSClassesHelperConversion(_)

  class ClassEnrichableMutable2BSClassesHelperConversion[T <: ClassEnrichableMutable](val enrichable: T)
      extends AnyVal
         with BSClassesHelper[T]:
    protected def withClass(clas: String): T = enrichable.addClass(clas)

  given [T <: ClassEnrichableMutable]: Conversion[T, BSClassesHelper[T]] =
    ClassEnrichableMutable2BSClassesHelperConversion(_)

  class AttrEnrichableMutable2BSDataHelperConversion[T <: AttrEnrichableMutable](val enrichable: T)
      extends AnyVal
         with BSDataHelper[T]:
    protected def setAttribute(name: String, value: String): T = enrichable.setAttribute(name, value)

  given [T <: AttrEnrichableMutable]: Conversion[T, BSDataHelper[T]] =
    AttrEnrichableMutable2BSDataHelperConversion(_)

  class ClassEnrichableImmutable2BSClassesHelperConversion[R](val enrichable: ClassEnrichableImmutable[R])
      extends AnyVal
         with BSClassesHelper[R]:
    protected def withClass(clas: String): R = enrichable.addClass(clas)

  given [R]: Conversion[ClassEnrichableImmutable[R], BSClassesHelper[R]] =
    ClassEnrichableImmutable2BSClassesHelperConversion(_)

  class AttrEnrichableImmutable2BSDataHelperConversion[R](val enrichable: AttrEnrichableImmutable[R])
      extends AnyVal
         with BSDataHelper[R]:
    protected def setAttribute(name: String, value: String): R = enrichable.setAttribute(name, value)

  given [R]: Conversion[AttrEnrichableImmutable[R], BSDataHelper[R]] =
    AttrEnrichableImmutable2BSDataHelperConversion(_)
