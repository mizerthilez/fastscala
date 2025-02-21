package com.fastscala.components.bootstrap5.offcanvas

import scala.xml.{ Elem, NodeSeq }

import com.fastscala.components.bootstrap5.helpers.ClassEnrichableMutable
import com.fastscala.components.utils.Mutable
import com.fastscala.core.FSContext
import com.fastscala.js.Js
import com.fastscala.utils.IdGen
import com.fastscala.xml.scala_xml.JS
import com.fastscala.xml.scala_xml.JS.ScalaXmlRerenderer

trait BSOffcanvasBase extends ClassEnrichableMutable with Mutable:
  import com.fastscala.components.bootstrap5.helpers.BSHelpers.{ *, given }

  val offcanvasId = IdGen.id("offcanvas")

  val position: OffcanvasPosition

  var offcanvasClasses = ""

  override def addClass(clas: String): this.type =
    offcanvasClasses += s" $clas"
    this

  var onOffcanvasTransforms: Elem => Elem = identity[Elem]
  var onOffcanvasHeaderTransforms: Elem => Elem = identity[Elem]
  var onOffcanvasBodyTransforms: Elem => Elem = identity[Elem]

  def onOffcanvas(f: Elem => Elem): this.type = mutate:
    onOffcanvasTransforms = onOffcanvasTransforms andThen f

  def onOffcanvasHeader(f: Elem => Elem): this.type = mutate:
    onOffcanvasHeaderTransforms = onOffcanvasHeaderTransforms andThen f

  def onOffcanvasBody(f: Elem => Elem): this.type = mutate:
    onOffcanvasBodyTransforms = onOffcanvasBodyTransforms andThen f

  def transformOffcanvasElem(elem: Elem): Elem = onOffcanvasTransforms:
    elem.offcanvas.withId(offcanvasId).withClass(position.clas).withAttr("tabindex" -> "-1")

  def transformOffcanvasHeaderElem(elem: Elem): Elem = onOffcanvasHeaderTransforms(elem.offcanvas_header)

  def transformOffcanvasBodyElem(elem: Elem): Elem = onOffcanvasBodyTransforms(elem.offcanvas_body)

  lazy val offcanvasRenderer: ScalaXmlRerenderer =
    JS.rerenderable(_ => implicit fsc => renderOffcanvas(), debugLabel = Some("offcanvas"))

  lazy val offcanvasHeaderRenderer: ScalaXmlRerenderer =
    JS.rerenderable(_ => implicit fsc => renderOffcanvasHeader(), debugLabel = Some("offcanvas-header"))

  lazy val offcanvasBodyRenderer: ScalaXmlRerenderer =
    JS.rerenderable(_ => implicit fsc => renderOffcanvasBody(), debugLabel = Some("offcanvas-body"))

  def append2DOM()(using FSContext): Js = JS.append2Body(offcanvasRenderer.render())

  var offcanvasInstalled: Option[String] = None

  def installAndShow(
    backdrop: Boolean = true,
    backdropStatic: Boolean = false,
    scroll: Boolean = true,
    keyboard: Boolean = true,
  )(using FSContext
  ): Js =
    install(backdrop, backdropStatic, scroll, keyboard) & show() & removeOnHidden()

  def install(
    backdrop: Boolean = true,
    backdropStatic: Boolean = false,
    scroll: Boolean = true,
    keyboard: Boolean = true,
  )(using fsc: FSContext
  ): Js =
    append2DOM()(using fsc.page.rootFSContext) `&`:
      offcanvasInstalled = Some(offcanvasId)
      Js(
        s""";new bootstrap.Offcanvas(document.getElementById('$offcanvasId'), {
           |  backdrop: ${if backdropStatic then "'static'" else backdrop.toString},
           |  keyboard: $keyboard,
           |  scroll: $scroll,
           |});""".stripMargin
      )

  def hideAndRemoveAndDeleteContext()(using FSContext): Js = hide() & removeOnHidden()

  def dispose(): Js = Js(s"""bootstrap.Offcanvas.getOrCreateInstance('#$offcanvasId').dispose()""")

  def hide(): Js = Js(s"""bootstrap.Offcanvas.getOrCreateInstance('#$offcanvasId').hide()""")

  def show(): Js = Js(s"""bootstrap.Offcanvas.getOrCreateInstance('#$offcanvasId').show()""")

  def toggle(): Js = Js(s"""bootstrap.Offcanvas.getOrCreateInstance('#$offcanvasId').toggle()""")

  def onShow(js: Js): Js = Js(s"""$$('#$offcanvasId').on('show.bs.offcanvas', function (e) {${js.cmd}});""")

  def onShown(js: Js): Js = Js(s"""$$('#$offcanvasId').on('shown.bs.offcanvas', function (e) {${js.cmd}});""")

  def onHide(js: Js): Js = Js(s"""$$('#$offcanvasId').on('hide.bs.offcanvas', function (e) {${js.cmd}});""")

  def onHidden(js: Js): Js = Js(
    s"""$$('#$offcanvasId').on('hidden.bs.offcanvas', function (e) {${js.cmd}});"""
  )

  def onHidePrevented(js: Js): Js = Js(
    s"""$$('#$offcanvasId').on('hidePrevented.bs.offcanvas', function (e) {${js.cmd}});"""
  )

  def deleteContext()(using fsc: FSContext): Unit = fsc.page.deleteContextFor(offcanvasRenderer)

  def removeAndDeleteContext(offcanvasId: String = offcanvasId)(using fsc: FSContext): Js =
    JS.removeId(offcanvasId) & fsc.callback: () =>
      deleteContext()
      JS.void

  def removeOnHidden()(using fsc: FSContext): Js =
    offcanvasInstalled
      .flatMap: offcanvasId =>
        fsc.page.inContextForOption(offcanvasRenderer):
          implicit fsc =>
            offcanvasInstalled = None
            onHidden(removeAndDeleteContext(offcanvasId))
      .getOrElse(JS.void)

  def offcanvasHeaderTitle: String

  def offcanvasHeaderTitleNs: Elem = <h5 class="offcanvas-title">{offcanvasHeaderTitle}</h5>

  def offcanvasHeaderContents()(using FSContext): NodeSeq =
    offcanvasHeaderTitleNs ++
      <button type="button" class="btn-close" data-bs-dismiss="offcanvas" aria-label="Close"></button>

  def offcanvasBodyContents()(using FSContext): NodeSeq

  def rerenderOffcanvas()(using FSContext): Js = offcanvasRenderer.rerender()

  def rerenderOffcanvasHeader()(using FSContext): Js = offcanvasHeaderRenderer.rerender()

  def rerenderOffcanvasBody()(using FSContext): Js = offcanvasBodyRenderer.rerender()

  def renderOffcanvasHeader()(using FSContext): Elem =
    transformOffcanvasHeaderElem:
      div.apply:
        offcanvasHeaderContents()

  def renderOffcanvasBody()(using FSContext): Elem =
    transformOffcanvasBodyElem:
      div.apply:
        offcanvasBodyContents()

  def renderOffcanvas()(using FSContext): Elem =
    transformOffcanvasElem:
      div.withClass(offcanvasClasses):
        offcanvasHeaderRenderer.render() ++
          offcanvasBodyRenderer.render()
