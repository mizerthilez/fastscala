package com.fastscala.templates.bootstrap5.toast

import scala.xml.Elem

import com.fastscala.core.FSContext
import com.fastscala.js.Js
import com.fastscala.templates.bootstrap5.helpers.ClassEnrichableMutable
import com.fastscala.templates.utils.Mutable
import com.fastscala.utils.IdGen
import com.fastscala.xml.scala_xml.JS

abstract class BSToast2Base extends ClassEnrichableMutable with Mutable:
  import com.fastscala.templates.bootstrap5.helpers.BSHelpers.{ given, * }

  val toastContainerId = IdGen.id("toast-container")
  val toastId = IdGen.id("toast")
  val toastHeaderId = IdGen.id("toast-header")
  val toastBodyId = IdGen.id("toast-body")

  var animation = true
  var autohide = true
  var delay = 5000

  var toastClasses = ""

  def addClass(clas: String): this.type =
    toastClasses += s" $clas"
    this

  def setAnimation(v: Boolean): this.type =
    animation = v
    this

  def setAutohide(v: Boolean): this.type =
    autohide = v
    this

  def setDelay(v: Int): this.type =
    delay = v
    this

  var toastContainerTransforms: Elem => Elem = identity[Elem]
  var toastTransforms: Elem => Elem = identity[Elem]
  var toastHeaderTransforms: Elem => Elem = identity[Elem]
  var toastBodyTransforms: Elem => Elem = identity[Elem]

  def addCloseBtn(): this.type = mutate:
    onToastHeader:
      _.withAppendedToContents:
        <button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Close"></button>

  def onToastContainer(f: Elem => Elem): this.type = mutate:
    toastContainerTransforms = toastContainerTransforms andThen f

  def onToast(f: Elem => Elem): this.type = mutate:
    toastTransforms = toastTransforms andThen f

  def onToastHeader(f: Elem => Elem): this.type = mutate:
    toastHeaderTransforms = toastHeaderTransforms andThen f

  def onToastBody(f: Elem => Elem): this.type = mutate:
    toastBodyTransforms = toastBodyTransforms andThen f

  def transformToastContainer(elem: Elem): Elem = toastContainerTransforms:
    elem.withId(toastContainerId).toast_container.p_3

  def transformToast(elem: Elem): Elem = toastTransforms:
    elem
      .withId(toastId)
      .toast
      .withRole("alert")
      .withAttrs(
        "aria-live" -> "assertive",
        "aria-atomic" -> "true",
        "data-bs-animation" -> animation.toString,
        "data-bs-autohide" -> autohide.toString,
        "data-bs-delay" -> delay.toString,
      )

  def transformToastHeader(elem: Elem): Elem = toastHeaderTransforms:
    elem.withId(toastHeaderId).toast_header

  def transformToastBody(elem: Elem): Elem = toastBodyTransforms:
    elem.withId(toastBodyId).toast_body

  def append2DOM()(using FSContext): Js = JS.append2Body(renderToast())

  def installAndShow(
    backdrop: Boolean = true,
    backdropStatic: Boolean = false,
    focus: Boolean = true,
    keyboard: Boolean = true,
  )(using FSContext
  ): Js =
    install(backdrop, backdropStatic, focus, keyboard) & show()

  def install(
    backdrop: Boolean = true,
    backdropStatic: Boolean = false,
    focus: Boolean = true,
    keyboard: Boolean = true,
  )(using FSContext
  ): Js = append2DOM()

  def dispose(): Js = Js(s"""bootstrap.Toast.getOrCreateInstance(${JS.elementById(toastId)}).dispose()""")

  def remove(): Js = JS.removeId(toastContainerId)

  def hideAndRemove(): Js = hide() & onHidden(remove())

  def hide(): Js = Js(s"""bootstrap.Toast.getOrCreateInstance(${JS.elementById(toastId)}).hide()""")

  def show(): Js = Js(s"""bootstrap.Toast.getOrCreateInstance(${JS.elementById(toastId)}).show()""")

  def onShow(js: Js): Js = Js(s"""$$('#$toastId').on('show.bs.toast', function (e) {${js.cmd}});""")

  def onShown(js: Js): Js = Js(s"""$$('#$toastId').on('shown.bs.toast', function (e) {${js.cmd}});""")

  def onHide(js: Js): Js = Js(s"""$$('#$toastId').on('hide.bs.toast', function (e) {${js.cmd}});""")

  def onHidden(js: Js): Js = Js(s"""$$('#$toastId').on('hidden.bs.toast', function (e) {${js.cmd}});""")

  def removeOnHidden(): Js = onHidden(remove())

  def toastHeader(): Elem

  def toastContents(): Elem

  def renderToast()(using FSContext): Elem =
    transformToastContainer:
      div:
        transformToast:
          div:
            transformToastHeader(toastHeader()) ++
              transformToastBody(toastContents())

object BSToast2:
  import com.fastscala.templates.bootstrap5.helpers.BSHelpers.{ given, * }

  def Simple(header: Elem)(contents: Elem): BSToast2Base = new BSToast2Base:
    def toastHeader(): Elem = <div>{header.me_auto}</div>

    def toastContents(): Elem = contents

  def VerySimple(header: Elem)(contents: Elem): BSToast2Base = new BSToast2Base:
    def toastHeader(): Elem = <div>{header.me_auto}</div>

    def toastContents(): Elem = contents
  .onToastContainer(_.position_fixed.top_0.end_0)
    .addCloseBtn()
