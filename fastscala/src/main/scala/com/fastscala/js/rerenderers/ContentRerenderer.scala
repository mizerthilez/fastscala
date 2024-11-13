package com.fastscala.js.rerenderers

import scala.util.chaining.scalaUtilChainingOps

import com.fastscala.core.{ FSContext, FSXmlEnv }
import com.fastscala.js.{ Js, JsUtils }
import com.fastscala.utils.IdGen

class ContentRerenderer[Env <: FSXmlEnv](
  using val env: Env
)(
  renderFunc: ContentRerenderer[Env] => FSContext => env.NodeSeq,
  id: Option[String] = None,
  debugLabel: Option[String] = None,
  gcOldFSContext: Boolean = true,
):
  val outterElem: env.Elem = env.buildElem("div")()

  val aroundId = id.getOrElse("around" + IdGen.id)
  var rootRenderContext: Option[FSContext] = None

  def render()(using fsc: FSContext): env.Elem =
    rootRenderContext = Some(fsc)
    fsc.page.rerendererDebugStatus.render:
      outterElem
        .withIdIfNotSet(aroundId)
        .pipe: elem =>
          elem.withContents:
            renderFunc(this):
              if gcOldFSContext then fsc.createNewChildContextAndGCExistingOne(this, debugLabel = debugLabel)
              else fsc

  def rerender(): Js = rootRenderContext
    .map: fsc =>
      fsc.page.rerendererDebugStatus.rerender(
        aroundId,
        JsUtils.generic.replace(aroundId, render()(using fsc)),
      )
    .getOrElse(throw new Exception("Missing context - did you call render() first?"))
