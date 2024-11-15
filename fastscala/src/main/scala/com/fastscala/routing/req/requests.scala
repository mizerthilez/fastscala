package com.fastscala.routing.req

import com.fastscala.routing.method.Method
import org.eclipse.jetty.server.Request

object Req:
  def unapply(req: Request): Option[(Method, List[String], String, Boolean)] =
    Method.fromString
      .unapply(req.getMethod)
      .map: method =>
        val path = req.getHttpURI.getPath
        val ext = path.replaceAll(".*\\.(\\w+)$", "$1").toLowerCase
        (
          method,
          path.replaceAll("^/", "").replaceAll(s"\\.$ext$$", "").split("/").toList.filter(_ != ""),
          ext,
          path.endsWith("/"),
        )
