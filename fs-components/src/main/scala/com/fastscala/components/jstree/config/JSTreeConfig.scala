package com.fastscala.components.jstree.config

import com.fastscala.js.Js

case class ContextMenu(
  select_node: Option[Boolean] = None,
  show_at_node: Option[Boolean] = None,
  items: Option[Js] = None,
)

case class Data(
  url: Option[String] = None,
  data: Option[Js] = None,
)

case class Themes(
  dots: Option[Boolean] = None,
  icons: Option[Boolean] = Some(true),
  ellipsis: Option[Boolean] = Some(true),
  stripes: Option[Boolean] = Some(true),
)

case class Core(
  check_callback: Option[Boolean] = None,
  data: Option[Data] = None,
  themes: Option[Themes] = None,
)

case class JSTreeConfig(
  core: Option[Core] = None,
  contextmenu: Option[ContextMenu] = None,
  plugins: Option[Seq[String]] = None,
)
