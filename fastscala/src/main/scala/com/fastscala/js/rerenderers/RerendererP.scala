package com.fastscala.js.rerenderers

import com.fastscala.core.{ FSContext, FSXmlEnv }
import com.fastscala.js.{ Js, JsUtils }
import com.fastscala.utils.IdGen

class RerendererP[Env <: FSXmlEnv, P](
  using val env: Env
)(
  renderFunc: RerendererP[Env, P] => FSContext => P => env.Elem,
  idOpt: Option[String] = None,
  debugLabel: Option[String] = None,
  gcOldFSContext: Boolean = true,
):
  self =>
  val Js = JsUtils.generic

  var aroundId = idOpt.getOrElse("around" + IdGen.id)
  var rootRenderContext: Option[FSContext] = None

  def render(param: P)(implicit fsc: FSContext): env.Elem =
    rootRenderContext = Some(fsc)
    val rendered = renderFunc(this) {
      if gcOldFSContext then
        fsc.createNewChildContextAndGCExistingOne(this, debugLabel = debugLabel)
      else fsc
    }(param)
    rendered.getId() match
      case Some(id) =>
        aroundId = id
        rendered
      case None => rendered.withIdIfNotSet(aroundId)

  def rerender(param: P): Js = Js.replace(
    aroundId,
    render(param)(
      rootRenderContext.getOrElse(
        throw new Exception("Missing context - did you call render() first?")
      )
    ),
  ) // & Js(s"""$$("#$aroundId").fadeOut(100).fadeIn(100).fadeOut(100).fadeIn(100)""")

  def replaceBy(elem: env.Elem): Js = Js.replace(aroundId, elem.withId(aroundId))

  def replaceContentsBy(elem: env.Elem): Js = Js.setContents(aroundId, elem)

  def map(f: env.Elem => env.Elem): RerendererP[env.type, P] =
    new RerendererP[env.type, P](null, None, None):
      override def render(param: P)(implicit fsc: FSContext): env.Elem = f(self.render(param))

      override def rerender(param: P): Js = Js.replace(
        self.aroundId,
        f(
          self.render(param)(
            self.rootRenderContext.getOrElse(
              throw new Exception("Missing context - did you call render() first?")
            )
          )
        ),
      ) // & Js(s"""$$("#$aroundId").fadeOut(100).fadeIn(100).fadeOut(100).fadeIn(100)""")

      override def replaceBy(elem: env.Elem): Js = self.replaceBy(elem)

      override def replaceContentsBy(elem: env.Elem): Js = self.replaceContentsBy(elem)
