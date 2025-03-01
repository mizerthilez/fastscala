package com.fastscala.components.jstree.editable

import scala.util.chaining.*

import io.circe.generic.semiauto.*

import com.fastscala.components.bootstrap5.toast.BSToast2
import com.fastscala.components.jstree.*
import com.fastscala.core.FSContext
import com.fastscala.js.Js

trait EditableJSTreeNode[T, N <: EditableJSTreeNode[T, N]](
  using jsTree: JSTreeWithContextMenu[T, N]
) extends JSTreeNodeWithContextMenu[T, N]:
  self: N =>
  var title: String
  def titleNs = scala.xml.Text(title)

  val children: collection.mutable.ArrayBuffer[N]
  def childrenF = () => children

  def allowDuplicated: Boolean = true

  import EditableJSTreeNode.{ *, given }

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
    override val run: Option[FSContext => String => Js] =
      def nextId(pid: String, childrenIds: Set[String]): String =
        s"${pid}_Sub${childrenIds.size}".pipe: subId =>
          if !childrenIds.contains(subId) then
            subId
          else
            (0 until childrenIds.size)
              .collectFirst:
                case idx if !childrenIds.contains(s"${pid}_Sub$idx") =>
                  s"${pid}_Sub$idx"
              .getOrElse:
                throw Exception(s"Cannot make nextId for childrenIds: ${childrenIds}")

      Some(implicit fsc => id =>
        fsc.callback: () =>
          jsTree.findNode(id).children.pipe: children =>
            val subId = nextId(id, children.map(_.id).toSet)
            children.append(onCreate(subId))
            jsTree.loadAndEditJSTreeNode(id, subId, onEditJs(onEdit))
      )
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
    override val run: Option[FSContext => String => Js] =
      Some(implicit fsc => id =>
        fsc.callback: () =>
          jsTree.editJSTreeNode(id, onEditJs(onEdit)))
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
    override val run: Option[FSContext => String => Js] =
      Some(implicit fsc => id =>
        fsc.callback: () =>
          val (pid, _) = id.splitAt(id.lastIndexOf("_"))
          if pid.nonEmpty then
            jsTree.findNode(pid).children.pipe: children =>
              onRemove(children.remove(children.indexWhere(_.id == id)), pid) &
                jsTree.refreshJSTreeNode(pid)
          else Js.void
      )
    // format: on

object EditableJSTreeNode:
  case class OnEditData(id: String, text: String)
  given io.circe.Decoder[OnEditData] = deriveDecoder
