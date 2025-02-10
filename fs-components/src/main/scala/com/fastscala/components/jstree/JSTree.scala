package com.fastscala.components.jstree

import scala.util.chaining.scalaUtilChainingOps
import scala.xml.{ Elem, NodeSeq }

import org.eclipse.jetty.server.Request

import com.fastscala.components.jstree.config.*
import com.fastscala.components.utils.ElemWithRandomId
import com.fastscala.core.FSContext
import com.fastscala.js.Js
import com.fastscala.xml.scala_xml.JS
import com.fastscala.xml.scala_xml.JS.given
import com.fastscala.xml.scala_xml.ScalaXmlNodeSeqUtils.MkNSFromElems

class JSTreeLazyLoadNode[T](
  title: String,
  val value: T,
  val id: String,
  val open: Boolean = false,
  val disabled: Boolean = false,
  val icon: Option[String] = None,
)(
  val childrenF: () => Seq[JSTreeLazyLoadNode[T]]
) extends JSTreeNode[T, JSTreeLazyLoadNode[T]]:
  override def titleNs: NodeSeq = scala.xml.Text(title)

class JSTreeSimpleNode[T](
  title: String,
  val value: T,
  val id: String,
  val open: Boolean = false,
  val disabled: Boolean = false,
  val icon: Option[String] = None,
)(
  children: Seq[JSTreeSimpleNode[T]]
) extends JSTreeNode[T, JSTreeSimpleNode[T]]:
  override def titleNs: NodeSeq = scala.xml.Text(title)

  def childrenF = () => children

abstract class JSTreeNode[T, +N <: JSTreeNode[T, N]]:
  self: N =>
  def titleNs: NodeSeq

  def value: T

  def id: String

  def open: Boolean

  def disabled: Boolean

  def icon: Option[String]

  def childrenF: () => collection.Seq[N]

  def renderLi(): Elem =
    val appendedChildren = if open then <ul>{childrenF().map(_.renderLi()).mkNS}</ul> else NodeSeq.Empty
    val dataJSTree = Some(
      List(
        icon.map(icon => s""""icon":"$icon""""),
        Some(disabled).filter(_ == true).map(disabled => s""""disabled":$disabled"""),
      ).flatten
    ).filter(_.nonEmpty).map(_.mkString("{", ",", "}")).getOrElse(null)
    <li id={id} data-jstree={dataJSTree} class={if !open then "jstree-closed" else ""}>{titleNs}{
      appendedChildren
    }</li>

abstract class JSTree[T, N <: JSTreeNode[T, N]] extends ElemWithRandomId:
  def plugins: List[String] = Nil

  def rootNodes: Seq[N]

  def render()(using FSContext): Elem = <div id={elemId}></div>

  def renderAndInit()(using FSContext): NodeSeq = render() ++ init().onDOMContentLoaded.inScriptTag

  def rerender()(using FSContext): Js = JS.void

  //  protected val childrenOfId = collection.mutable.Map[String, Seq[N]]()
  protected val nodeById = collection.mutable.Map[String, N]()

  def findNode(id: String): N = nodeById(id)

  def jsTreeConfig(using FSContext): JSTreeConfig =
    implicit def nonOption2Option[T](v: T): Option[T] = Some(v)

    JSTreeConfig(
      core = Core(
        check_callback = true,
        data = Data(
          data = Js("""function (node) { return { 'id' : node.id }; }""")
        ),
        themes = Themes(),
      ),
      plugins = this.plugins,
    )

  def init(using fsc: FSContext)(config: JSTreeConfig = jsTreeConfig, onSelect: Js = Js.void): Js = Js:
    val callback = fsc.anonymousPageURL(
      implicit fsc =>
        Option(Request.getParameters(fsc.page.req).getValue("id")) match
          case Some("#") =>
            <ul>{rootNodes.tap(_.foreach(node => nodeById += (node.id -> node))).map(_.renderLi()).mkNS}</ul>
          case Some(id) =>
            <ul>{
              nodeById(id)
                .childrenF()
                .tap(_.foreach(node => nodeById += (node.id -> node)))
                .map(_.renderLi())
                .mkNS
            }</ul>
          case None => throw new Exception(s"Id parameter not found")
      ,
      "nodes.html",
    )

    import com.softwaremill.quicklens.*
    val jsTreeConfig = config
      .modify(_.core)
      .setToIf(config.core.isEmpty)(Some(Core()))
      .pipe: config =>
        config
          .modify(_.core.each.data)
          .setToIf(config.core.get.data.isEmpty)(Some(Data()))
          .modify(_.core.each.data.each.url)
          .setTo(Some(callback))

    import JSTree.{ *, given }
    import io.circe.syntax.given

    s"""$$('#$elemId').on("changed.jstree", function(e, data){${onSelect.cmd}}).jstree(${jsTreeConfig.asJson.toString.trimQuoteInData});"""

  def refreshJSTreeNode(node: String): Js =
    Js(s"""$$('#$elemId').jstree(true).refresh_node('$node')""")

  def loadAndEditJSTreeNode(parent: String, node: String, onEdit: Js): Js =
    Js:
      s"""$$('#$elemId').jstree(true).load_node('$parent', function(){ this.edit('$node', null, function(node, success, cancelled){ if (!cancelled && success) {${onEdit.cmd}} }) })"""

  def editJSTreeNode(node: String, onEdit: Js, text: Option[String] = None): Js =
    Js:
      s"""$$('#$elemId').jstree(true).edit('$node', ${text.map(t => s"'$t'").getOrElse("null")}, function(node, success, cancelled){ if (!cancelled && success) {${onEdit.cmd}} })"""

object JSTree:
  import io.circe.generic.semiauto.*
  import io.circe.Encoder

  given Encoder[Js] = Encoder.encodeString.contramap[Js](_.cmd)
  given Encoder[Data] = deriveEncoder[Data]
  given Encoder[Themes] = deriveEncoder[Themes]
  given Encoder[Core] = deriveEncoder[Core]
  given Encoder[ContextMenu] = deriveEncoder[ContextMenu]
  given Encoder[JSTreeConfig] = deriveEncoder[JSTreeConfig]

  extension (config: String)
    def trimQuoteInData =
      config.replace(""""function""", "function").replace("""}"""", "}").replace("""\"""", "'")
