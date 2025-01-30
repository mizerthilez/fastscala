package com.fastscala.js.rerenderers

import com.fastscala.core.{ FSContext, FSXmlEnv }
import com.fastscala.js.{ Js, JsUtils }
import com.fastscala.utils.{ IdGen, given }

class Rerenderer[Env <: FSXmlEnv](
  using val env: Env
)(
  renderFunc: Rerenderer[Env] => FSContext => env.Elem,
  idOpt: Option[String] = None,
  debugLabel: Option[String] = None,
):
  self =>
  val Js = JsUtils.generic

  var aroundId = idOpt.getOrElse("around" + IdGen.id)
  var rootRenderContext: Option[FSContext] = None

  def render()(using fsc: FSContext): env.Elem =
    rootRenderContext = Some(fsc)
    val rendered = fsc.runInNewOrRenewedChildContextFor(this, debugLabel = debugLabel)(renderFunc(this)(_))
    RerendererDebugStatusState().render:
      rendered.getId match
        case Some(id) =>
          aroundId = id
          rendered
        case None => rendered.withIdIfNotSet(aroundId)

  def rerender(): Js = rootRenderContext
    .map: fsc ?=>
      RerendererDebugStatusState().rerender(
        aroundId,
        Js.replace(aroundId, render()),
      )
    .getOrElse(throw new Exception("Missing context - did you call render() first?"))

  def replaceBy(elem: env.Elem): Js = Js.replace(aroundId, elem.withId(aroundId))

  def replaceContentsBy(elem: env.Elem): Js = Js.setContents(aroundId, elem)

  def map(f: env.Elem => env.Elem): Rerenderer[env.type] =
    new Rerenderer[env.type](null, None, None):
      override def render()(implicit fsc: FSContext): env.Elem = f(self.render())

      override def rerender(): Js = Js.replace(
        self.aroundId,
        f(
          self.render()(
            using self.rootRenderContext.getOrElse(
              throw new Exception("Missing context - did you call render() first?")
            )
          )
        ),
      ) // & Js(s"""$$("#$aroundId").fadeOut(100).fadeIn(100).fadeOut(100).fadeIn(100)""")

      override def replaceBy(elem: env.Elem): Js = self.replaceBy(elem)

      override def replaceContentsBy(elem: env.Elem): Js = self.replaceContentsBy(elem)
