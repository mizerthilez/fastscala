package com.fastscala.templates.bootstrap5.modals

import scala.xml.NodeSeq

import com.fastscala.core.FSContext
import com.fastscala.js.Js
import com.fastscala.templates.bootstrap5.utils.BSBtn
import com.fastscala.templates.form7.{ F7FormRenderer, Form7 }
import com.fastscala.utils.given
import com.fastscala.xml.scala_xml.{ ScalaXmlElemUtils, given }

abstract class BSModal5WithForm7Base(
  val modalHeaderTitle: String
)(using val formRenderer: F7FormRenderer
) extends BSModal5Base
       with Form7:
  import com.fastscala.templates.bootstrap5.helpers.BSHelpers.{ given, * }

  def saveBtnLbl = "Save"

  def cancelBtnLbl = "Cancel"

  def cancelBtnEnabled: Boolean = false

  def saveBtn(using FSContext) =
    BSBtn().BtnPrimary.lbl(saveBtnLbl).ajax(fsc ?=> form.submitFormServerSide())

  def cancelBtn(using FSContext) =
    BSBtn().BtnSecondary.lbl(cancelBtnLbl).onclick(hideAndRemoveAndDeleteContext())

  def modalBodyContents()(using FSContext): NodeSeq = form.render()

  def modalFooterContents()(using FSContext): Option[NodeSeq] = Some:
    ScalaXmlElemUtils.showIf(cancelBtnEnabled)(cancelBtn.btn.me_2) ++ saveBtn.btn

  override def postSubmitForm()(implicit fsc: FSContext): Js =
    super.postSubmitForm() & hideAndRemoveAndDeleteContext()
