package com.fastscala.components.bootstrap5.modals

import scala.xml.NodeSeq

import com.fastscala.core.FSContext
import com.fastscala.components.bootstrap5.utils.BSBtn
import com.fastscala.components.form6.{ F6FormRenderer, Form6 }
import com.fastscala.xml.scala_xml.{ ScalaXmlElemUtils, given }

abstract class BSModal5WithForm6Base(
  val modalHeaderTitle: String
)(implicit val formRenderer: F6FormRenderer
) extends BSModal5Base
       with Form6:
  import com.fastscala.components.bootstrap5.helpers.BSHelpers.{ given, * }

  def saveBtnLbl = "Save"

  def cancelBtnLbl = "Cancel"

  def cancelBtnEnabled: Boolean = false

  def saveBtn(implicit fsc: FSContext) =
    BSBtn().BtnPrimary.lbl(saveBtnLbl).onclick(form.onSaveClientSide())

  def cancelBtn(implicit fsc: FSContext) =
    BSBtn().BtnSecondary.lbl(cancelBtnLbl).onclick(hideAndRemoveAndDeleteContext())

  override def modalBodyContents()(implicit fsc: FSContext): NodeSeq = form.render()

  override def modalFooterContents()(implicit fsc: FSContext): Option[NodeSeq] = Some:
    ScalaXmlElemUtils.showIf(cancelBtnEnabled)(cancelBtn.btn.me_2) ++ saveBtn.btn
