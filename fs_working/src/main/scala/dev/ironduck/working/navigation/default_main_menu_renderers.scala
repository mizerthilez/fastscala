package dev.ironduck.working.navigation

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
    def render(elem: BSMenu)(implicit fsc: FSContext): NodeSeq =
      <div class="position-sticky p-3 sidebar-sticky">
        <ul class="list-unstyled ps-0">
          {elem.items.map(_.render())}
        </ul>
      </div>

  object DefaultMenuSectionRenderer extends MenuSectionRenderer:
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

  object DefaultSimpleMenuItemRenderer extends SimpleMenuItemRenderer:
    def render(elem: SimpleMenuItem)(implicit fsc: FSContext): NodeSeq = <li><a href={
      elem.href
    } class="text-white d-inline-flex text-decoration-none rounded">{elem.name}</a></li>

  object DefaultRoutingMenuItemRenderer extends RoutingMenuItemRenderer:
    def render(elem: RoutingMenuItem)(implicit fsc: FSContext): NodeSeq = <li><a href={
      elem.href
    } class="text-white d-inline-flex text-decoration-none rounded">{elem.name}</a></li>

  object DefaultHeaderMenuItemRenderer extends HeaderMenuItemRenderer:
    def render(elem: HeaderMenuItem)(implicit fsc: FSContext): NodeSeq =
      <li class="mt-3"><span class="menu-heading fw-bold text-uppercase fs-7 ">{
        elem.title
      }</span></li>
