package com.fastscala.js.rerenderers

import com.fastscala.core.{FSContext, FSXmlEnv}
import com.fastscala.js.{Js, JsUtils, JsXmlUtils}
import com.fastscala.utils.IdGen

import scala.util.chaining.scalaUtilChainingOps

class ContentRerendererP[Env <: FSXmlEnv, P]
        (using val env: Env)
        (
          renderFunc: ContentRerendererP[Env, P] => FSContext => P => env.NodeSeq,
          id: Option[String] = None,
          debugLabel: Option[String] = None,
          gcOldFSContext: Boolean = true
        ):

  val outterElem: env.Elem = env.buildElem("div")()

  val aroundId = id.getOrElse("around" + IdGen.id)
  var rootRenderContext: Option[FSContext] = None

  def render(param: P)(implicit fsc: FSContext) =
    rootRenderContext = Some(fsc)
    outterElem.withIdIfNotSet(aroundId).pipe(elem => {
      elem.withContents(renderFunc(this)({
        if gcOldFSContext then fsc.createNewChildContextAndGCExistingOne(this, debugLabel = debugLabel)
        else fsc
      })(param))
    })

  def rerender(param: P) = JsUtils.generic.replace(aroundId, render(param)(rootRenderContext.getOrElse(throw new Exception("Missing context - did you call render() first?")))) // & Js(s"""$$("#$aroundId").fadeOut(100).fadeIn(100).fadeOut(100).fadeIn(100)""")
