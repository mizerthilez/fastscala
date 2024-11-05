package com.fastscala.core

trait FSXmlEnv:
  type NodeSeq
  type Elem

  def render(elem: NodeSeq): String

  def buildElemFrom[E <: FSXmlEnv](using e: E)(other: e.Elem): Elem

  def buildNodeSeqFrom[E <: FSXmlEnv](using e: E)(other: e.NodeSeq): NodeSeq

  def Empty: NodeSeq

  def buildUnparsed(unparsed: String): NodeSeq

  def buildText(text: String): NodeSeq

  def buildElem(label: String, attrs: (String, String)*)(children: NodeSeq*): Elem

  def label(elem: Elem): String

  def contents(elem: Elem): NodeSeq

  def attribute(elem: Elem, attrNamenv: String): Option[String]

  def attributes(elem: Elem): List[(String, String)]

  def transformAttribute(elem: Elem, attrNamenv: String, transform: Option[String] => String): Elem

  def transformContents[E <: FSXmlEnv](using e: E)(elem: Elem, transform: NodeSeq => e.NodeSeq): Elem

  def concat(ns1: NodeSeq, ns2: NodeSeq): NodeSeq

  def elem2NodeSeq(elem: Elem): NodeSeq

  given Conversion[Elem, NodeSeq] = elem2NodeSeq(_)

  extension (elem: Elem)
    def getId(): Option[String] = attribute(elem, "id")

    def withId(id: String): Elem = transformAttribute(elem, "id", _ => id)

    def withIdIfNotSet(id: String): Elem = transformAttribute(elem, "id", _.getOrElse(id))

    def withContents[E <: FSXmlEnv](using e: E)(contents: e.NodeSeq): Elem =
      transformContents(elem, _ => contents)

end FSXmlEnv
