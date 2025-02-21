package com.fastscala.components.jstree

import io.circe.generic.semiauto.*

import com.fastscala.components.jstree.config.*
import com.fastscala.core.FSContext
import com.fastscala.js.Js
import com.fastscala.xml.scala_xml.JS

trait JSTreeContextMenuAction:
  /** Must be unique!
    */
  def label: String

  /** keyCode which will trigger the action if the menu is open
    */
  def shortcut: Option[Int]

  def shortcutLabel: Option[String]

  def separatorBefore: Boolean

  def separatorAfter: Boolean

  def disabled: Boolean

  def icon: Option[String]

  def subactions: Seq[JSTreeContextMenuAction]

  def run: Option[FSContext => String => Js]

abstract class DefaultJSTreeContextMenuAction(
  val label: String,
  val shortcut: Option[Int] = None,
  val shortcutLabel: Option[String] = None,
  val separatorBefore: Boolean = false,
  val separatorAfter: Boolean = true,
  val disabled: Boolean = false,
  val icon: Option[String] = None,
  val subactions: Seq[JSTreeContextMenuAction] = Nil,
) extends JSTreeContextMenuAction

object DefaultJSTreeContextMenuAction:
  def apply(
    label: String,
    action: Option[FSContext => String => Js],
    shortcut: Option[Int] = None,
    shortcutLabel: Option[String] = None,
    separatorBefore: Boolean = false,
    separatorAfter: Boolean = true,
    disabled: Boolean = false,
    icon: Option[String] = None,
    subactions: Seq[JSTreeContextMenuAction] = Nil,
  ): DefaultJSTreeContextMenuAction =
    new DefaultJSTreeContextMenuAction(
      label = label,
      shortcut = shortcut,
      shortcutLabel = shortcutLabel,
      separatorBefore = separatorBefore,
      separatorAfter = separatorAfter,
      disabled = disabled,
      icon = icon,
      subactions = subactions,
    ):
      override val run: Option[FSContext => String => Js] = action

trait JSTreeNodeWithContextMenu[T, N <: JSTreeNodeWithContextMenu[T, N]] extends JSTreeNode[T, N]:
  self: N =>
  def actions: Seq[JSTreeContextMenuAction]

case class RenderableMenuAction(
  action: Option[Js],
  _disabled: Option[Boolean],
  icon: Option[String],
  label: Option[String],
  separator_after: Option[Boolean],
  separator_before: Option[Boolean],
  shortcut: Option[Int],
  shortcut_label: Option[String],
  submenu: Option[Map[String, RenderableMenuAction]],
)

object RenderableMenuAction:
  import JSTree.given
  import com.fastscala.utils.toOption
  given encoder: io.circe.Encoder[RenderableMenuAction] = deriveEncoder

  def apply[T, N <: JSTreeNodeWithContextMenu[T, N]](
    menu: JSTreeWithContextMenu[T, N],
    node: N,
    action: JSTreeContextMenuAction,
  )(using fsc: FSContext
  ): RenderableMenuAction = new RenderableMenuAction(
    action =
      if action.subactions.nonEmpty then Some(JS._false)
      else
        Some(JS.function0:
          action.run
            .map: runF =>
              fsc.runInNewOrRenewedChildContextFor((menu, node.id, action.label))(f => runF(f)(node.id))
            .getOrElse(JS.void)
        )
    ,
    _disabled = Some(action.disabled),
    icon = action.icon,
    label = Some(action.label),
    separator_after = Some(action.separatorAfter),
    separator_before = Some(action.separatorBefore),
    shortcut = action.shortcut,
    shortcut_label = action.shortcutLabel,
    submenu = action.subactions.toOption.map: actions =>
      actions
        .map: action =>
          JS.asJsStr(action.label).cmd -> RenderableMenuAction(menu, node, action)
        .toMap,
  )

trait JSTreeWithContextMenu[T, N <: JSTreeNodeWithContextMenu[T, N]] extends JSTree[T, N]:
  override def plugins: List[String] = "contextmenu" :: super.plugins

  /** Indicates if the node should be selected when the context menu is invoked on it
    */
  def selectNode: Boolean = true

  /** Indicates if the menu should be shown aligned with the node. Otherwise the mouse coordinates are used.
    */
  def showAtNode: Boolean = true

  def renderJSTreeContextMenuAction(node: N, action: JSTreeContextMenuAction)(using fsc: FSContext): String =
    import io.circe.syntax.given
    import JSTree.trimQuoteInData
    RenderableMenuAction(this, node, action)
      .asJson(using RenderableMenuAction.encoder)
      .toString
      .trimQuoteInData

  override def jsTreeConfig(using fsc: FSContext): JSTreeConfig =
    import com.softwaremill.quicklens.*

    val menuItemsCallback = fsc.callback(
      Js("item.id"),
      id =>
        nodeById.get(id) match
          case Some(node) =>
            val menuItems =
              node.actions
                .map: action =>
                  JS.asJsStr(action.label).cmd + ": " + renderJSTreeContextMenuAction(node, action)
                .mkString("({", ",", "})")
            Js(s"env.callback(eval(${JS.asJsStr(menuItems)}));")
          case None => throw new Exception(s"Could not find node for id '$id'"),
      env = Js("{callback: callback}"),
    )

    super.jsTreeConfig
      .modify(_.contextmenu)
      .setTo:
        Some:
          ContextMenu(
            select_node = Some(selectNode),
            show_at_node = Some(showAtNode),
            items = Some(Js(s"function(item, callback) { $menuItemsCallback }")),
          )
