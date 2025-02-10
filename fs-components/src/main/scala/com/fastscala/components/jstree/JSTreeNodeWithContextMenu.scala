package com.fastscala.components.jstree

import scala.util.chaining.*

import io.circe.generic.semiauto.*

import com.fastscala.components.bootstrap5.toast.BSToast2
import com.fastscala.components.jstree.config.*
import com.fastscala.core.FSContext
import com.fastscala.js.Js
import com.fastscala.xml.scala_xml.JS

trait JSTreeContextMenuAction:
  def separatorBefore: Boolean

  def separatorAfter: Boolean

  def disabled: Boolean

  /** Must be unique!
    */
  def label: String

  /** keyCode which will trigger the action if the menu is open
    */
  def shortcut: Option[Int]

  def shortcutLabel: Option[String]

  def icon: Option[String]

  def subactions: Seq[JSTreeContextMenuAction]

  def runF: FSContext => String => Js

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
    run: FSContext => String => Js,
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
      override val runF: FSContext => String => Js = run

trait JSTreeNodeWithContextMenu[T, N <: JSTreeNodeWithContextMenu[T, N]](
  using jsTree: JSTreeWithContextMenu[T, N]
) extends JSTreeNode[T, N]:
  self: N =>
  def actions: Seq[JSTreeContextMenuAction]

  var title: String
  def titleNs = scala.xml.Text(title)

  val children: collection.mutable.ArrayBuffer[N]
  def childrenF = () => children

  def allowDuplicated: Boolean = true

  import JSTreeNodeWithContextMenu.{ *, given }

  def onEditJs(onEdit: (N, String) => Js)(using fsc: FSContext): Js =
    import com.fastscala.core.circe.CirceSupport.given

    fsc.callbackJSONDecoded[OnEditData](
      Js("{id: node.id, text: node.text}"),
      data =>
        val node = jsTree.findNode(data.id)
        if node.title != data.text && !allowDuplicated then
          val (pid, _) = data.id.splitAt(data.id.lastIndexOf("_"))
          if pid.nonEmpty && jsTree.findNode(pid).children.exists(_.title == data.text) then
            BSToast2
              .VerySimple(<label class="text-danger">Error</label>):
                <p class="text-danger">Duplicated title found: {data.text}.</p>
              .installAndShow() &
              jsTree.editJSTreeNode(data.id, onEditJs(onEdit), text = Some(node.title))
          else onEdit(node, data.text)
        else onEdit(node, data.text),
    )

  class DefaultCreateAction(
    label: String,
    shortcut: Option[Int] = None,
    shortcutLabel: Option[String] = None,
    separatorBefore: Boolean = false,
    separatorAfter: Boolean = true,
    disabled: Boolean = false,
    icon: Option[String] = None,
    subactions: Seq[JSTreeContextMenuAction] = Nil,
    onCreate: String => N,
    onEdit: (N, String) => Js,
  ) extends DefaultJSTreeContextMenuAction(
        label = label,
        shortcut = shortcut,
        shortcutLabel = shortcutLabel,
        separatorBefore = separatorBefore,
        separatorAfter = separatorAfter,
        disabled = disabled,
        icon = icon,
        subactions = subactions,
      ):
    // format: off
    override val runF: FSContext => String => Js =
      implicit fsc => id =>
        jsTree.findNode(id).children.pipe: children =>
          val subId = s"${id}_Sub${children.length}"
          children.append(onCreate(subId))
          jsTree.loadAndEditJSTreeNode(id, subId, onEditJs(onEdit))
    // format: on

  class DefaultRenameAction(
    label: String,
    shortcut: Option[Int] = None,
    shortcutLabel: Option[String] = None,
    separatorBefore: Boolean = false,
    separatorAfter: Boolean = true,
    disabled: Boolean = false,
    icon: Option[String] = None,
    subactions: Seq[JSTreeContextMenuAction] = Nil,
    onEdit: (N, String) => Js,
  ) extends DefaultJSTreeContextMenuAction(
        label = label,
        shortcut = shortcut,
        shortcutLabel = shortcutLabel,
        separatorBefore = separatorBefore,
        separatorAfter = separatorAfter,
        disabled = disabled,
        icon = icon,
        subactions = subactions,
      ):
    // format: off
    override val runF: FSContext => String => Js =
      implicit fsc => id => jsTree.editJSTreeNode(id, onEditJs(onEdit))
    // format: on

  class DefaultRemoveAction(
    label: String,
    shortcut: Option[Int] = None,
    shortcutLabel: Option[String] = None,
    separatorBefore: Boolean = false,
    separatorAfter: Boolean = true,
    disabled: Boolean = false,
    icon: Option[String] = None,
    subactions: Seq[JSTreeContextMenuAction] = Nil,
    onRemove: (N, String) => Js,
  ) extends DefaultJSTreeContextMenuAction(
        label = label,
        shortcut = shortcut,
        shortcutLabel = shortcutLabel,
        separatorBefore = separatorBefore,
        separatorAfter = separatorAfter,
        disabled = disabled,
        icon = icon,
        subactions = subactions,
      ):
    // format: off
    override val runF: FSContext => String => Js =
      implicit fsc => id =>
        val (pid, _) = id.splitAt(id.lastIndexOf("_"))
        if pid.nonEmpty then
          jsTree.findNode(pid).children.pipe: children =>
            onRemove(children.remove(children.indexWhere(_.id == id)), pid) &
              jsTree.refreshJSTreeNode(pid)
        else Js.void
    // format: on

object JSTreeNodeWithContextMenu:
  case class OnEditData(id: String, text: String)
  given io.circe.Decoder[OnEditData] = deriveDecoder

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
        fsc.runInNewOrRenewedChildContextFor((menu, node.id, action.label)):
          implicit fsc => Some(JS.function()(fsc.callback(() => action.runF(fsc)(node.id))))
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
