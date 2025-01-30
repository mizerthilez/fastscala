package com.fastscala.components.bootstrap5.modals

import scala.xml.NodeSeq

import com.fastscala.core.FSContext
import com.fastscala.js.Js
import com.fastscala.components.bootstrap5.utils.BSBtn
import com.fastscala.utils.given
import com.fastscala.xml.scala_xml.JS

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
    import com.fastscala.components.bootstrap5.helpers.BSHelpers.{ given, * }
    val modal = new BSModal5Base:
      override def modalHeaderTitle: String = title

      override def modalBodyContents()(using fsc: FSContext): NodeSeq = contents(this)(fsc)

      override def modalFooterContents()(using FSContext): Option[NodeSeq] = Some:
        BSBtn().BtnSecondary.lbl(cancelBtnText).ajax(onCancel(this)).btn ++
          BSBtn().BtnPrimary.lbl(okBtnText).ajax(onOk(this)).btn.ms_2

    modal.installAndShow() & modal.onHidden(onHidden)
