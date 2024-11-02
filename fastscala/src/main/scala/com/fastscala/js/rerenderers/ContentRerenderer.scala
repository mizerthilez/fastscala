package com.fastscala.js.rerenderers

import com.fastscala.core.{FSContext, FSXmlEnv, FSXmlSupport}
import com.fastscala.js.{Js, JsUtils, JsXmlUtils}
import com.fastscala.utils.IdGen

import scala.util.chaining.scalaUtilChainingOps

class ContentRerenderer[E <: FSXmlEnv : FSXmlSupport](
                                                       renderFunc: ContentRerenderer[E] => FSContext => E#NodeSeq,
                                                       id: Option[String] = None,
                                                       debugLabel: Option[String] = None,
                                                       gcOldFSContext: Boolean = true
                                                     )(implicit debugStatus: RerendererDebugStatus.Value) {

  implicit val Js: JsXmlUtils[E] = JsUtils.generic

  import com.fastscala.core.FSXmlUtils._

  val outterElem: E#Elem = implicitly[FSXmlSupport[E]].buildElem("div")()

  val aroundId = id.getOrElse("around" + IdGen.id)
  var rootRenderContext: Option[FSContext] = None

  def render()(implicit fsc: FSContext): E#Elem = {
    rootRenderContext = Some(fsc)
    debugStatus.render(outterElem.withIdIfNotSet(aroundId).pipe(elem => {
      elem.withContents(renderFunc(this)({
        if (gcOldFSContext) fsc.createNewChildContextAndGCExistingOne(this, debugLabel = debugLabel)
        else fsc
      }))
    }))
  }

  def rerender(): Js = debugStatus.rerender(aroundId, Js.replace(aroundId, elem2NodeSeq(render()(rootRenderContext.getOrElse(throw new Exception("Missing context - did you call render() first?"))))))
}
