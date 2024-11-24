package com.fastscala.demo.docs.navigation

import scala.xml.NodeSeq

import com.fastscala.core.FSContext
import com.fastscala.utils.IdGen

object DefaultBSMenuRenderers:
  given BSMenuRenderer = DefaultBSMenuRenderer
  given MenuSectionRenderer = DefaultMenuSectionRenderer
  given SimpleMenuItemRenderer = DefaultSimpleMenuItemRenderer
  given RoutingMenuItemRenderer = DefaultRoutingMenuItemRenderer
  given HeaderMenuItemRenderer = DefaultHeaderMenuItemRenderer

  object DefaultBSMenuRenderer extends BSMenuRenderer:
    def render(elem: BSMenu)(using FSContext): NodeSeq =
      <ul class="nav sidebar-menu flex-column" data-lte-toggle="treeview" role="menu" data-accordion="false">
        {elem.items.map(_.render())}
      </ul>

  object DefaultMenuSectionRenderer extends MenuSectionRenderer:
    def render(elem: MenuSection)(using fsc: FSContext): NodeSeq =
      val isOpen = elem.items.exists(_.matches(fsc.page.req.getHttpURI.getPath))
      val id = IdGen.id
      <li class={"nav-item" + (if isOpen then " menu-open" else "")}>
        <a href="#" class="nav-link">
          <i class={elem.icon}></i>
          <p>
            {elem.name}
            <i class="nav-arrow bi bi-chevron-right"></i>
          </p>
        </a>
        <ul class="nav nav-treeview">
          {elem.items.map(_.render())}
        </ul>
      </li>

  object DefaultSimpleMenuItemRenderer extends SimpleMenuItemRenderer:
    def render(elem: SimpleMenuItem)(using fsc: FSContext): NodeSeq =
      val isOpen = elem.matches(fsc.page.req.getHttpURI.getPath)
      <li class="nav-item">
        <a href={elem.href} class={"nav-link" + (if isOpen then " active" else "")}>
          <i class={elem.icon}></i>
          <p>{elem.name}</p>
        </a>
      </li>

  object DefaultRoutingMenuItemRenderer extends RoutingMenuItemRenderer:
    def render(elem: RoutingMenuItem)(using fsc: FSContext): NodeSeq =
      val isOpen = elem.matches(fsc.page.req.getHttpURI.getPath)
      <li class="nav-item">
        <a href={elem.href} class={"nav-link" + (if isOpen then " active" else "")}>
          <i class={elem.icon}></i>
          <p>{elem.name}</p>
        </a>
      </li>

  object DefaultHeaderMenuItemRenderer extends HeaderMenuItemRenderer:
    def render(elem: HeaderMenuItem)(using FSContext): NodeSeq =
      <li class="nav-header">{elem.title}</li>
