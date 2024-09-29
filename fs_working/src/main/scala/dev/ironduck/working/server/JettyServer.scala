package dev.ironduck.working.server

import java.awt.Desktop

import org.eclipse.jetty.server.Handler

import com.fastscala.server.JettyServerHelper

object JettyServer extends JettyServerHelper():
  override def Port: Int = config.getInt("dev.ironduck.working.server.port")

  override def isLocal: Boolean = config.getBoolean("dev.ironduck.working.server.local")

  override def appName: String = "fs_working"

  override def buildMainHandler(): Handler = new RoutingHandler()

  override def postStart(): Unit =
    super.postStart()

    if isLocal && Desktop
          .isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)
    then
      // val desktop = Desktop.getDesktop()
      // desktop.browse(new URI(s"http://localhost:$Port"))
      println(s"Available at: http://localhost:$Port")
