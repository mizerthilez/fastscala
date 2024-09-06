package com.fastscala.xml.scala_xml

import com.fastscala.core.{ FSContext, FSXmlEnv }
import com.fastscala.utils.{ Renderable, RenderableWithFSContext }
import com.fastscala.xml.scala_xml.ScalaXmlNodeSeqUtils.MkNSFromNodeSeq

import scala.xml.*

object FSScalaXmlEnv extends FSXmlEnv:
  override type NodeSeq = scala.xml.NodeSeq
  override type Elem = scala.xml.Elem

  override def elem2NodeSeq(elem: Elem): NodeSeq = elem

  override def buildUnparsed(unparsed: String): NodeSeq = scala.xml.Unparsed(unparsed)

  override def concat(ns1: NodeSeq, ns2: NodeSeq): NodeSeq = ns1 ++ ns2

  override def buildText(text: String): NodeSeq = scala.xml.Text(text)

  override def buildElemFrom[E <: FSXmlEnv](using e: E)(other: e.Elem): Elem = other match
    case elem: scala.xml.Elem => elem
    case other => XML.loadString(e.render(e.elem2NodeSeq(other)))

  override def buildNodeSeqFrom[E <: FSXmlEnv](using e: E)(other: e.NodeSeq): NodeSeq = other match
    case ns: scala.xml.NodeSeq => ns
    case other => XML.loadString(e.render(other))

  override def buildElem(label: String, attrs: (String, String)*)(children: NodeSeq*): Elem =
    new ScalaXmlElemUtils.RichElem(
      new Elem(
        prefix = null,
        label = label,
        attributes1 = Null,
        scope = TopScope,
        minimizeEmpty = false,
        child = children.mkNS*,
      )
    ).withAttrs(attrs*)

  override def render(elem: NodeSeq): String = elem.toString()

  override def transformAttribute(elem: Elem, attrName: String, transform: Option[String] => String)
    : Elem = attributeTransform(elem, attrName, transform)

  override def attribute(elem: Elem, attrName: String): Option[String] =
    elem.attributes.get(attrName).map(_.mkString(""))

  override def attributes(elem: Elem): List[(String, String)] =
    elem.attributes.toList.map(a => a.key -> a.value.map(_.toString()).mkString(" "))

  override def contents(elem: Elem): NodeSeq = elem.child

  override def transformContents[E <: FSXmlEnv](
    using e: E
  )(
    elem: Elem,
    transform: NodeSeq => e.NodeSeq,
  ): Elem =
    transform(contents(elem)) match
      case ns: scala.xml.NodeSeq => ScalaXmlElemUtils.RichElem(elem).apply(ns)
      case newContents => ScalaXmlElemUtils.RichElem(elem).apply(buildNodeSeqFrom[E](newContents))

  override def Empty: NodeSeq = NodeSeq.Empty

  private def attributeTransform(elem: Elem, attrName: String, transform: Option[String] => String)
    : Elem =

    def updateMetaData(
      metaData: MetaData = Option(elem.attributes).getOrElse(Null),
      found: Boolean = false,
    ): MetaData = metaData match
      case Null if !found => new UnprefixedAttribute(attrName, transform(None), Null)
      case Null if found => Null
      case PrefixedAttribute((pre, key, value, next)) if key == attrName =>
        new PrefixedAttribute(
          pre,
          key,
          value match
            case null => Seq(Text(transform(None)))
            case Seq(Text(value)) => Seq(Text(transform(Some(value))))
            case other => other
          ,
          updateMetaData(next, found = true),
        )
      case UnprefixedAttribute((key, value, next)) if key == attrName =>
        new UnprefixedAttribute(
          key,
          value match
            case null => Seq(Text(transform(None)))
            case Seq(Text(value)) => Seq(Text(transform(Some(value))))
            case other => other
          ,
          updateMetaData(next, found = true),
        )
      case PrefixedAttribute((pre, key, value, next)) =>
        new PrefixedAttribute(pre, key, value, updateMetaData(next, found))
      case UnprefixedAttribute((key, value, next)) =>
        new UnprefixedAttribute(key, value, updateMetaData(next, found))
    new Elem(elem.prefix, elem.label, updateMetaData(), elem.scope, elem.minimizeEmpty, elem.child*)

  extension (elem: scala.xml.Elem)
    def asFSXml[E <: FSXmlEnv](using e: E): e.Elem = e.buildElemFrom(elem)

  extension (ns: scala.xml.NodeSeq)
    def asFSXml[E <: FSXmlEnv](using e: E): e.NodeSeq = e.buildNodeSeqFrom(ns)

  given [E <: FSXmlEnv](using e: E): Conversion[scala.xml.NodeSeq, e.NodeSeq] =
    ns => e.buildUnparsed(ns.toString())

  given [E <: FSXmlEnv](using e: E): Conversion[scala.xml.Elem, e.Elem] =
    elem =>
      e.buildElem(
        elem.label,
        elem.attributes.toList.map(a => a.key -> a.value.map(_.toString()).mkString(" "))*
      )(elem.child.map(node => e.buildUnparsed(node.toString()))*)

given FSScalaXmlEnv.type = FSScalaXmlEnv
