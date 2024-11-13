package com.fastscala.js.rerenderers

import com.fastscala.core.FSXmlEnv
import com.fastscala.js.Js

object RerendererDebugStatus extends Enumeration:
  val Enabled = Value
  implicit val Disabled: Value = Value

  def Unsupported = Set("table", "thead", "tbody", "tfooter", "tr")

  implicit class RichValue(v: Value) extends AnyVal:
    private def style(bgColor: String = "rgb(147 211 255 / 6%)") =
      s"width: 100%; height: 100%; position: absolute; top: 0; left: 0; text-align: right; color: #4b4b4b; background-color: $bgColor; font-weight: bold; padding: 2px 4px; border: 2px solid #6290bd;pointer-events: none; z-index: 10;"

    def render[Env <: FSXmlEnv](using env: Env)(rendered: env.Elem): env.Elem =
      if v == RerendererDebugStatus.Enabled && !Unsupported.contains(env.label(rendered))
      then
        env.transformContents(
          env.transformAttribute(rendered, "style", _.getOrElse("") + ";position:relative;"),
          existing =>
            env.concat(
              existing,
              env.buildElem(
                "span",
                "for" -> rendered.toString,
                "style" -> style(),
                "id" -> rendered.getId.map(_ + "-overlay").getOrElse(null),
              )(),
            ),
        )
      else rendered

    def rerender(aroundId: String, rerenderJs: Js): Js =
      if v == RerendererDebugStatus.Enabled then
        Js.catchAndLogErrors(Js.setAttr(aroundId + "-overlay")("style", style("rgb(255 147 156 / 50%)"))) &
          Js(s"""$$("#$aroundId").fadeOut(1500, function() {${rerenderJs.cmd}});""")
      else rerenderJs
