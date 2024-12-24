package com.fastscala.demo.docs.navigation

import scala.xml.{ Elem, NodeSeq }

import com.fastscala.core.FSContext
import com.fastscala.utils.IdGen

object DefaultBSNavBarRenderers:
  given BSNavBarRenderer = DefaultBSNavBarRenderer
  given MenuSectionRenderer = DefaultBSNavBarSectionRenderer
  given SimpleMenuItemRenderer = DefaultSimpleBSNavBarItemRenderer
  given RoutingMenuItemRenderer = DefaultRoutingBSNavBarItemRenderer
  given HeaderMenuItemRenderer = DefaultHeaderBSNavBarItemRenderer

  object DefaultBSNavBarRenderer extends BSNavBarRenderer:
    def renderHeader(elem: BSNav): Elem = <a class="navbar-brand" href="#"></a>

    def renderToogleButton(elem: BSNav): Elem =
      <button class="navbar-toggler collapsed" type="button" data-bs-toggle="collapse" data-bs-target={
        "#" + elem.navBarId
      } aria-controls={elem.navBarId} aria-expanded="false">
        <span class="navbar-toggler-icon"></span>
      </button>

    def renderRightContents(elem: BSNav): NodeSeq = NodeSeq.Empty

    def render(elem: BSNav)(implicit fsc: FSContext): Elem =
      <nav class="navbar navbar-expand-lg bg-primary-subtle">
        <div class="container-fluid">
          {renderHeader(elem)}
          {renderToogleButton(elem)}
          <div class="navbar-collapse collapse" id={elem.navBarId}>
            <ul class="navbar-nav">
              {elem.items.map(_.render())}
            </ul>
          </div>
          {renderRightContents(elem)}
        </div>
      </nav>

  object DefaultBSNavBarSectionRenderer extends MenuSectionRenderer:
    def render(elem: MenuSection)(implicit fsc: FSContext): NodeSeq =
      val isOpen = elem.items.exists(_.matches(fsc.page.req.getHttpURI.getPath))
      val id = IdGen.id
      <li class="mb-1">
        <button class={
        "text-white btn bi btn-toggle d-inline-flex align-items-center rounded border-0" + (if isOpen
                                                                                            then ""
                                                                                            else " collapsed")
      } data-bs-toggle="collapse" data-bs-target={s"#$id"} aria-expanded={isOpen.toString}>
          {elem.name}
        </button>
        <div class={"collapse" + (if isOpen then " show" else "")} id={id}>
          <ul class="btn-toggle-nav list-unstyled fw-normal pb-1 small">
            {elem.items.map(_.render())}
          </ul>
        </div>
      </li>

  object DefaultSimpleBSNavBarItemRenderer extends SimpleMenuItemRenderer:
    def render(elem: SimpleMenuItem)(implicit fsc: FSContext): NodeSeq =
      <li class="nav-item"><a class="nav-link" href={elem.href}>{elem.name}</a></li>

  object DefaultRoutingBSNavBarItemRenderer extends RoutingMenuItemRenderer:
    def render(elem: RoutingMenuItem)(implicit fsc: FSContext): NodeSeq =
      <li class="nav-item"><a class="nav-link" href={elem.href}>{elem.name}</a></li>

  object DefaultHeaderBSNavBarItemRenderer extends HeaderMenuItemRenderer:
    def render(elem: HeaderMenuItem)(implicit fsc: FSContext): NodeSeq =
      <li class="mt-3"><span class="menu-heading fw-bold text-uppercase fs-7 ">{
        elem.title
      }</span></li>
