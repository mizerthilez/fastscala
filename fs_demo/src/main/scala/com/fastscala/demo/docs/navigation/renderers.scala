package com.fastscala.demo.docs.navigation

import scala.xml.{ Elem, NodeSeq }

import com.fastscala.core.FSContext

trait BSMenuRenderer:
  def render(elem: BSMenu)(implicit fsc: FSContext): NodeSeq

trait BSNavBarRenderer:
  def render(elem: BSNav)(implicit fsc: FSContext): Elem

trait MenuSectionRenderer:
  def render(elem: MenuSection)(implicit fsc: FSContext): NodeSeq

trait SimpleMenuItemRenderer:
  def render(elem: SimpleMenuItem)(implicit fsc: FSContext): NodeSeq

trait RoutingMenuItemRenderer:
  def render(elem: RoutingMenuItem)(implicit fsc: FSContext): NodeSeq

trait HeaderMenuItemRenderer:
  def render(elem: HeaderMenuItem)(implicit fsc: FSContext): NodeSeq
