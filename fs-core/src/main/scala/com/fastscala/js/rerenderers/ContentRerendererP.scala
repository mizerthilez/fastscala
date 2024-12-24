package com.fastscala.js.rerenderers

import scala.util.chaining.scalaUtilChainingOps

import com.fastscala.core.{ FSContext, FSXmlEnv }
import com.fastscala.js.{ Js, JsUtils }
import com.fastscala.utils.{ IdGen, given }

class ContentRerendererP[Env <: FSXmlEnv, P](
  using val env: Env
)(
  renderFunc: ContentRerendererP[Env, P] => FSContext => P => env.NodeSeq,
  id: Option[String] = None,
  debugLabel: Option[String] = None,
):
  val outterElem: env.Elem = env.buildElem("div")()

  val aroundId = id.getOrElse("around" + IdGen.id)
  var rootRenderContext: Option[FSContext] = None

  def render(param: P)(using fsc: FSContext) =
    rootRenderContext = Some(fsc)
    RerendererDebugStatusState().render:
      outterElem
        .withIdIfNotSet(aroundId)
        .pipe: elem =>
          elem.withContents:
            fsc.inNewChildContextFor(this, debugLabel = debugLabel)(renderFunc(this)(_)(param))

  def rerender(param: P): Js = rootRenderContext
    .map: fsc ?=>
      RerendererDebugStatusState().rerender(
        aroundId,
        JsUtils.generic.replace(aroundId, render(param)),
      )
    .getOrElse(throw new Exception("Missing context - did you call render() first?"))
