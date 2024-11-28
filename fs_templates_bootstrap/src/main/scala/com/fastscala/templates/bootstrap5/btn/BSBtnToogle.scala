package com.fastscala.templates.bootstrap5.utils

import scala.util.chaining.scalaUtilChainingOps
import scala.xml.Elem

import com.fastscala.core.FSContext
import com.fastscala.js.Js
import com.fastscala.xml.scala_xml.JS

object BSBtnToogle:
  implicit class RichBSBtnToogler(val btn: BSBtn) extends AnyVal:
    def toggler(
      get: () => Boolean,
      set: Boolean => Js,
      falseLbl: String,
      trueLbl: String,
      falseTransform: BSBtn => BSBtn = identity[BSBtn],
      trueTransform: BSBtn => BSBtn = identity[BSBtn],
    )(implicit fsc: FSContext
    ): Elem =
      var current = get()
      JS.rerenderable(rerenderer =>
        implicit fsc =>
          btn
            .lbl(if current then trueLbl else falseLbl)
            .ajax:
              implicit fsc =>
                current = !current
                set(current) & rerenderer.rerender()
            .pipe(if current then trueTransform else falseTransform)
            .btn
      ).render()
