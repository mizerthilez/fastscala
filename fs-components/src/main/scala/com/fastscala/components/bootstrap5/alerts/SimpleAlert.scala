package com.fastscala.components.bootstrap5.alerts

import scala.xml.{ Elem, NodeSeq }

import com.fastscala.js.rerenderers.Rerenderer
import com.fastscala.xml.scala_xml.{ FSScalaXmlEnv, JS }

object SimpleAlert:
  import com.fastscala.components.bootstrap5.helpers.BSHelpers.{ given, * }

  def apply(
    contents: NodeSeq,
    closeBtn: Elem,
  ): Rerenderer[FSScalaXmlEnv.type] = JS.rerenderable(rerenderer =>
    implicit fsc =>
      alert.alert_dismissible.fade.show
        .withRole("alert")
        .apply:
          contents ++ closeBtn
            .addClass("btn-close")
            .withType("button")
            .withAttr("data-bs-dismiss" -> "alert")
  )
