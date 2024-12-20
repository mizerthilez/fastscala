package com.fastscala.demo.docs.components

import scala.util.chaining.scalaUtilChainingOps
import scala.xml.{ Elem, NodeSeq }

import com.fastscala.core.FSContext
import com.fastscala.js.Js
import com.fastscala.templates.utils.Mutable
import com.fastscala.utils.{ IdGen, given }
import com.fastscala.xml.scala_xml.JS

abstract class Widget extends Mutable:
  lazy val widgetId = IdGen.id("widget")
  lazy val widgetHeaderId = IdGen.id("widget-header")
  lazy val widgetContentsId = IdGen.id("widget-contents")

  var onCardTransforms: Elem => Elem = identity[Elem]
  var onCardHeaderTransforms: Elem => Elem = identity[Elem]
  var onCardBodyTransforms: Elem => Elem = identity[Elem]

  def onCard(f: Elem => Elem): this.type = mutate:
    onCardTransforms = onCardTransforms andThen f

  def onCardHeader(f: Elem => Elem): this.type = mutate:
    onCardHeaderTransforms = onCardHeaderTransforms andThen f

  def onCardBody(f: Elem => Elem): this.type = mutate:
    onCardBodyTransforms = onCardBodyTransforms andThen f

  lazy val widgetRenderer =
    JS.rerenderable(
      rerenderer => fsc ?=> renderWidgetCard(),
      Some(widgetId),
      debugLabel = Some("widget"),
    )

  lazy val widgetHeaderRenderer =
    JS.rerenderable(
      rerenderer => fsc ?=> renderWidgetHeader(),
      Some(widgetHeaderId),
      debugLabel = Some("widget-header"),
    )

  lazy val widgetContentsRenderer =
    JS.rerenderable(
      rerenderer => fsc ?=> renderWidgetContents(),
      Some(widgetContentsId),
      debugLabel = Some("widget-contents"),
    )

  def widgetTitle: String

  def widgetTitleNs: NodeSeq = <h4 class="m-0">{widgetTitle}</h4>

  def widgetTopRight()(implicit fsc: FSContext): NodeSeq = NodeSeq.Empty

  def widgetContents()(implicit fsc: FSContext): NodeSeq

  def rerenderWidget()(implicit fsc: FSContext): Js = widgetRenderer.rerender()

  def rerenderWidgetHeader()(implicit fsc: FSContext): Js = widgetHeaderRenderer.rerender()

  def rerenderWidgetContents()(implicit fsc: FSContext): Js = widgetContentsRenderer.rerender()

  def transformWidgetCardHeader(elem: Elem): Elem = onCardHeaderTransforms(elem)

  def transformWidgetCardBody(elem: Elem): Elem = onCardBodyTransforms(elem)

  def transformWidgetCard(elem: Elem): Elem = onCardTransforms(elem)

  def renderWidgetHeader()(implicit fsc: FSContext): Elem =
    import com.fastscala.templates.bootstrap5.helpers.BSHelpers.{ given, * }
    card_header.d_flex.justify_content_between.align_items_center.apply {
      widgetTitleNs ++ <div class="ms-auto">{widgetTopRight()}</div>
    } pipe transformWidgetCardHeader

  def renderWidgetContents()(implicit fsc: FSContext): Elem =
    import com.fastscala.templates.bootstrap5.helpers.BSHelpers.{ given, * }
    card_body.apply {
      widgetContents()
    } pipe transformWidgetCardBody

  def renderWidgetCard()(implicit fsc: FSContext): Elem =
    import com.fastscala.templates.bootstrap5.helpers.BSHelpers.{ given, * }
    card.withId(widgetId).apply {
      widgetHeaderRenderer.render() ++
        widgetContentsRenderer.render()
    } pipe transformWidgetCard

  def renderWidget()(implicit fsc: FSContext): Elem = widgetRenderer.render()
