package com.fastscala.components.bootstrap5.offcanvas

import com.fastscala.core.FSContext
import com.fastscala.js.Js
import com.fastscala.xml.scala_xml.JS

import scala.xml.NodeSeq

object BSOffcanvas:
  import com.fastscala.components.bootstrap5.helpers.BSHelpers.{ *, given }

  def start(
    title: String,
    backdrop: Boolean = true,
    backdropStatic: Boolean = false,
    scroll: Boolean = true,
    keyboard: Boolean = true,
    onHidden: Js = JS.void,
  )(
    contents: BSOffcanvasBase => FSContext => NodeSeq
  )(using FSContext
  ): Js = apply(_.offcanvas_start, title, onHidden)(contents)

  def end(
    title: String,
    backdrop: Boolean = true,
    backdropStatic: Boolean = false,
    scroll: Boolean = true,
    keyboard: Boolean = true,
    onHidden: Js = JS.void,
  )(
    contents: BSOffcanvasBase => FSContext => NodeSeq
  )(using FSContext
  ): Js = apply(_.offcanvas_end, title, onHidden)(contents)

  def top(
    title: String,
    backdrop: Boolean = true,
    backdropStatic: Boolean = false,
    scroll: Boolean = true,
    keyboard: Boolean = true,
    onHidden: Js = JS.void,
  )(
    contents: BSOffcanvasBase => FSContext => NodeSeq
  )(using FSContext
  ): Js = apply(_.offcanvas_top, title, onHidden)(contents)

  def bottom(
    title: String,
    backdrop: Boolean = true,
    backdropStatic: Boolean = false,
    scroll: Boolean = true,
    keyboard: Boolean = true,
    onHidden: Js = JS.void,
  )(
    contents: BSOffcanvasBase => FSContext => NodeSeq
  )(using FSContext
  ): Js = apply(_.offcanvas_bottom, title, onHidden)(contents)

  private def apply(
    transform: BSOffcanvasBase => BSOffcanvasBase,
    title: String,
    onHidden: Js = JS.void,
  )(
    contents: BSOffcanvasBase => FSContext => NodeSeq
  )(using fsc: FSContext
  ): Js =
    val offcanvas = transform(new BSOffcanvasBase:
      override def offcanvasHeaderTitle: String = title

      override def offcanvasBodyContents()(using FSContext): NodeSeq = contents(this)(fsc)
    )
    offcanvas.installAndShow() & offcanvas.onHidden(onHidden)
