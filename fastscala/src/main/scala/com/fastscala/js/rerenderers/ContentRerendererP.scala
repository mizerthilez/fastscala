package com.fastscala.js.rerenderers

import scala.util.chaining.scalaUtilChainingOps

import com.fastscala.core.{ FSContext, FSXmlEnv }
import com.fastscala.js.JsUtils
import com.fastscala.utils.IdGen

class ContentRerendererP[Env <: FSXmlEnv, P](
  using val env: Env
)(
  renderFunc: ContentRerendererP[Env, P] => FSContext => P => env.NodeSeq,
  id: Option[String] = None,
  debugLabel: Option[String] = None,
  gcOldFSContext: Boolean = true,
)(using debugStatus: RerendererDebugStatus.Value
):
  val outterElem: env.Elem = env.buildElem("div")()

  val aroundId = id.getOrElse("around" + IdGen.id)
  var rootRenderContext: Option[FSContext] = None

  def render(param: P)(using fsc: FSContext) =
    rootRenderContext = Some(fsc)
    debugStatus.render:
      outterElem
        .withIdIfNotSet(aroundId)
        .pipe: elem =>
          elem.withContents:
            renderFunc(this) {
              if gcOldFSContext then fsc.createNewChildContextAndGCExistingOne(this, debugLabel = debugLabel)
              else fsc
            }(param)

  def rerender(param: P) = debugStatus.rerender(
    aroundId,
    JsUtils.generic.replace(
      aroundId,
      render(param)(
        using rootRenderContext.getOrElse(
          throw new Exception("Missing context - did you call render() first?")
        )
      ),
    ),
  )
