package com.fastscala.components.bootstrap5.helpers

import scala.xml.{ Elem, NodeSeq }

import com.fastscala.xml.scala_xml.FSScalaXmlEnv.{ Empty as EnvEmpty, * }

trait BasicElemsHelper:
  def Empty: NodeSeq = EnvEmpty

  def del: Elem = buildElem("del")()

  def s: Elem = buildElem("s")()

  def ins: Elem = buildElem("ins")()

  def u: Elem = buildElem("u")()

  def a: Elem = buildElem("a")()

  def small: Elem = buildElem("small")()

  def strong: Elem = buildElem("strong")()

  def em: Elem = buildElem("em")()

  def mark: Elem = buildElem("mark")()

  def pre: Elem = buildElem("pre")()

  def td: Elem = buildElem("td")()

  def tr: Elem = buildElem("tr")()

  def tbody: Elem = buildElem("tbody")()

  def thead: Elem = buildElem("thead")()

  def img: Elem = buildElem("img")()

  def div: Elem = buildElem("div")()

  def ul: Elem = buildElem("ul")()

  def li: Elem = buildElem("li")()

  def style: Elem = buildElem("style")()

  def input: Elem = buildElem("input")()

  def button: Elem = buildElem("button")()

  def span: Elem = buildElem("span")()

  def label: Elem = buildElem("label")()

  def b: Elem = buildElem("b")()

  def p: Elem = buildElem("p")()

  def abbr: Elem = buildElem("abbr")()

  def h1: Elem = buildElem("h1")()

  def h2: Elem = buildElem("h2")()

  def h3: Elem = buildElem("h3")()

  def h4: Elem = buildElem("h4")()

  def h5: Elem = buildElem("h5")()

  def h6: Elem = buildElem("h6")()

  def hr: Elem = buildElem("hr")()

  def br: Elem = buildElem("br")()
