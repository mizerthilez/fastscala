package com.fastscala.demo.docs.navigation

import scala.xml.NodeSeq

import org.eclipse.jetty.server.Request

import com.fastscala.core.{ FSContext, FSSession }
import com.fastscala.routing.method.Get
import com.fastscala.utils.IdGen
import com.fastscala.xml.scala_xml.ScalaXmlRenderableWithFSContext

case class BSMenu(items: MenuItem*)(using renderer: BSMenuRenderer):
  def render()(using FSContext): NodeSeq = renderer.render(this)

  def serve()(using Request, FSSession): Option[ScalaXmlRenderableWithFSContext] =
    items.map(_.serve()).find(_.isDefined).flatten

case class BSNav(items: MenuItem*)(using renderer: BSNavBarRenderer):
  val navBarId = IdGen.id("navBar")

  def render()(using FSContext): NodeSeq = renderer.render(this)

  def serve()(using Request, FSSession): Option[ScalaXmlRenderableWithFSContext] =
    items.map(_.serve()).find(_.isDefined).flatten

trait MenuItem:
  def render()(using FSContext): NodeSeq

  def serve()(using Request, FSSession): Option[ScalaXmlRenderableWithFSContext]

  def matches(uri: String): Boolean

// format: off
case class MenuSection(name: String, icon: String = "")(val items: MenuItem*)
  (using renderer: MenuSectionRenderer) extends MenuItem:
  def matches(uri: String): Boolean = items.exists(_.matches(uri))

  override def render()(using FSContext): NodeSeq = renderer.render(this)

  override def serve()(using Request, FSSession): Option[ScalaXmlRenderableWithFSContext] =
    items.map(_.serve()).find(_.isDefined).flatten

case class SimpleMenuItem(name: String, href: String, icon: String = "")
  (using renderer: SimpleMenuItemRenderer) extends MenuItem:
  def matches(uri: String): Boolean = href == uri

  def serve()(using Request, FSSession): Option[ScalaXmlRenderableWithFSContext] =
    None

  def render()(using FSContext): NodeSeq = renderer.render(this)

class RoutingMenuItem(matching: String*)(val name: String, page: => ScalaXmlRenderableWithFSContext, val icon: String = "")
  (using renderer: RoutingMenuItemRenderer) extends MenuItem:
  def matches(uri: String): Boolean = href == uri

  val href: String = matching.mkString("/", "/", "")

  def render()(using FSContext): NodeSeq = renderer.render(this)

  def serve()(using req: Request, session: FSSession): Option[ScalaXmlRenderableWithFSContext] =
    Some(req).collect:
      case Get(path*) if path == matching => page
// format: on

class HeaderMenuItem(val title: String)(using renderer: HeaderMenuItemRenderer) extends MenuItem:
  override def render()(using FSContext): NodeSeq = renderer.render(this)

  override def serve()(using Request, FSSession): Option[ScalaXmlRenderableWithFSContext] =
    None

  override def matches(uri: String): Boolean = false
