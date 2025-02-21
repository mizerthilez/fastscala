package com.fastscala.components.bootstrap5.offcanvas

import com.fastscala.components.form7.{ DefaultForm7, F7FormRenderer }
import com.fastscala.core.FSContext

import scala.xml.NodeSeq

abstract class BSOffcanvasWithForm7(
  val offcanvasHeaderTitle: String,
  val position: OffcanvasPosition,
)(using F7FormRenderer
) extends DefaultForm7
       with BSOffcanvasBase:
  override def offcanvasBodyContents()(using FSContext): NodeSeq = render()
