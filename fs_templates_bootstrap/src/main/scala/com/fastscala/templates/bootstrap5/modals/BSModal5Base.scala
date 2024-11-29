package com.fastscala.templates.bootstrap5.modals

import scala.xml.{ Elem, NodeSeq }

import com.fastscala.core.FSContext
import com.fastscala.js.Js
import com.fastscala.templates.bootstrap5.helpers.ClassEnrichableMutable
import com.fastscala.templates.bootstrap5.utils.BSBtn
import com.fastscala.templates.utils.Mutable
import com.fastscala.utils.{ IdGen, given }
import com.fastscala.xml.scala_xml.JS
import com.fastscala.xml.scala_xml.JS.ScalaXmlRerenderer

object BSModal5Size extends Enumeration:
  val SM = Value("modal-sm")
  val NORMAL = Value("")
  val LG = Value("modal-lg")
  val XL = Value("modal-xl")

abstract class BSModal5Base extends ClassEnrichableMutable with Mutable:
  import com.fastscala.templates.bootstrap5.helpers.BSHelpers.{ given, * }

  val modalId = IdGen.id("modal")
  val modalContentId = IdGen.id("modal-content")

  def modalSize: BSModal5Size.Value = BSModal5Size.NORMAL

  var modalClasses = ""

  def addClass(clas: String): this.type =
    modalClasses += s" $clas"
    this

  def transformModalElem(elem: Elem): Elem =
    elem.modal.fade.withId(modalId).withClass(modalSize.toString)

  def transformModalDialogElem(elem: Elem): Elem = elem.modal_dialog

  def transformModalContentElem(elem: Elem): Elem = elem.modal_content.withId(modalContentId)

  def transformModalHeaderElem(elem: Elem): Elem = elem.modal_header

  def transformModalBodyElem(elem: Elem): Elem = elem.modal_body

  def transformModalFooterElem(elem: Elem): Elem = elem.modal_footer

  lazy val modalRenderer: ScalaXmlRerenderer =
    JS.rerenderable(_ => fsc ?=> renderModal(), debugLabel = Some("modal"))

  lazy val modalContentsRenderer: ScalaXmlRerenderer =
    JS.rerenderable(_ => fsc ?=> renderModalContent(), debugLabel = Some("modal-contents"))

  lazy val modalContentsFooterRenderer: ScalaXmlRerenderer =
    JS.rerenderable(_ => fsc ?=> renderModalFooterContent(), debugLabel = Some("modal-contents-footer"))

  def append2DOM()(using FSContext): Js = JS.append2Body(modalRenderer.render())

  var modalInstalled: Option[String] = None

  def installAndShow(
    backdrop: Boolean = true,
    backdropStatic: Boolean = false,
    focus: Boolean = true,
    keyboard: Boolean = true,
  )(using FSContext
  ): Js =
    install(backdrop, backdropStatic, focus, keyboard) & show() & removeOnHidden()

  def install(
    backdrop: Boolean = true,
    backdropStatic: Boolean = false,
    focus: Boolean = true,
    keyboard: Boolean = true,
  )(using fsc: FSContext
  ): Js =
    append2DOM()(using fsc.page.rootFSContext) `&`:
      modalInstalled = Some(modalId)
      Js(
        s""";new bootstrap.Modal(document.getElementById('$modalId'), {
           |  backdrop: ${if backdropStatic then "'static'" else backdrop.toString},
           |  keyboard: $keyboard,
           |  focus: $focus,
           |});""".stripMargin
      )

  def dispose(): Js = Js(s"""$$('#$modalId').modal('dispose')""")

  def deleteContext()(using fsc: FSContext): Unit = fsc.page.deleteContextFor(modalRenderer)

  def removeAndDeleteContext(modalId: String = modalId)(using fsc: FSContext): Js =
    JS.removeId(modalId) & fsc.callback: () =>
      deleteContext()
      Js.void

  def handleUpdate(): Js = Js(s"""$$('#$modalId').modal('handleUpdate')""")

  def hideAndRemoveAndDeleteContext()(using FSContext): Js = hide() & removeOnHidden()

  def hide(): Js = Js(s"""$$('#$modalId').modal('hide')""")

  def show(): Js = Js(s"""$$('#$modalId').modal('show')""")

  def toggle(): Js = Js(s"""$$('#$modalId').modal('toggle')""")

  def onShow(js: Js): Js = Js(s"""$$('#$modalId').on('show.bs.modal', function (e) {${js.cmd}});""")

  def onShown(js: Js): Js = Js(
    s"""$$('#$modalId').on('shown.bs.modal', function (e) {${js.cmd}});"""
  )

  def onHide(js: Js): Js = Js(s"""$$('#$modalId').on('hide.bs.modal', function (e) {${js.cmd}});""")

  def onHidden(js: Js): Js = Js(
    s"""$$('#$modalId').on('hidden.bs.modal', function (e) {${js.cmd}});"""
  )

  def removeOnHidden()(using fsc: FSContext): Js =
    modalInstalled
      .flatMap: modalId =>
        fsc.page.inContextForOption(modalRenderer): fsc ?=>
          modalInstalled = None
          onHidden(removeAndDeleteContext(modalId))
      .getOrElse(JS.void)

  def modalHeaderTitle: String

  def modalHeaderTitleNs: Elem = <h1 class="modal-title fs-5">{modalHeaderTitle}</h1>

  def modalHeaderContents()(using FSContext): NodeSeq =
    modalHeaderTitleNs ++
      <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>

  def modalBodyContents()(using FSContext): NodeSeq

  def modalFooterContents()(using FSContext): Option[NodeSeq]

  def rerenderModal()(using FSContext): Js = modalRenderer.rerender()

  def rerenderModalContent()(using FSContext): Js = modalContentsRenderer.rerender()

  def rerenderModalFooterContent()(using FSContext): Js =
    modalContentsFooterRenderer.rerender()

  def renderModalFooterContent()(using FSContext): Elem =
    modalFooterContents()
      .map: contents =>
        transformModalFooterElem(div(contents))
      .getOrElse(<div style="display:none;"></div>)

  def renderModalContent()(using FSContext): Elem =
    transformModalContentElem:
      div.apply:
        transformModalHeaderElem(div(modalHeaderContents()))
          ++
            transformModalBodyElem(div(modalBodyContents()))
            ++
            modalContentsFooterRenderer.render()

  def renderModal()(using fsc: FSContext): Elem =
    transformModalElem:
      div.withAttr("tabindex" -> "-1"):
        transformModalDialogElem:
          div.withClass(modalClasses):
            modalContentsRenderer.render()

object BSModal5:
  def verySimple(
    title: String,
    closeBtnText: String,
    onHidden: Js = JS.void,
  )(
    contents: BSModal5Base => FSContext => NodeSeq
  )(using FSContext
  ): Js =
    val modal = new BSModal5Base:
      override def modalHeaderTitle: String = title

      override def modalBodyContents()(using fsc: FSContext): NodeSeq = contents(this)(fsc)

      override def modalFooterContents()(using FSContext): Option[NodeSeq] = Some:
        BSBtn().BtnPrimary.lbl(closeBtnText).onclick(hideAndRemoveAndDeleteContext()).btn

    modal.installAndShow() & modal.onHidden(onHidden)

  def okCancel(
    title: String,
    onOk: BSModal5Base => FSContext => Js,
    onCancel: BSModal5Base => FSContext => Js = modal => _ ?=> modal.hideAndRemoveAndDeleteContext(),
    okBtnText: String = "OK",
    cancelBtnText: String = "Cancel",
    onHidden: Js = JS.void,
  )(
    contents: BSModal5Base => FSContext => NodeSeq
  )(using FSContext
  ): Js =
    import com.fastscala.templates.bootstrap5.helpers.BSHelpers.{ given, * }
    val modal = new BSModal5Base:
      override def modalHeaderTitle: String = title

      override def modalBodyContents()(using fsc: FSContext): NodeSeq = contents(this)(fsc)

      override def modalFooterContents()(using FSContext): Option[NodeSeq] = Some:
        BSBtn().BtnSecondary.lbl(cancelBtnText).ajax(onCancel(this)).btn ++
          BSBtn().BtnPrimary.lbl(okBtnText).ajax(onOk(this)).btn.ms_2

    modal.installAndShow() & modal.onHidden(onHidden)
