package com.fastscala.utils

import com.fastscala.core.{FSContext, FSXmlEnv}

trait Renderable[Env <: FSXmlEnv](using val env: Env):
  def render(): env.NodeSeq

trait RenderableWithFSContext[Env <: FSXmlEnv](using val env: Env):
  def render()(implicit fsc: FSContext): env.NodeSeq

object RenderableWithFSContext:
  given [Env <: FSXmlEnv](using e: Env)(using FSContext): Conversion[RenderableWithFSContext[e.type], Renderable[e.type]] =
    renderableWithFSC => new Renderable:
        override def render(): e.NodeSeq = renderableWithFSC.render()
