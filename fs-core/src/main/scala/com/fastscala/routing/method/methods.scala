package com.fastscala.routing.method

import org.eclipse.jetty.server.Request

trait Method(methodName: String):
  def unapplySeq(req: Request): Option[Seq[String]] =
    Some(req.getHttpURI.getPath.replaceAll("^/", "").split("/").toList.filter(_ != "")).filter(_ =>
      req.getMethod == methodName
    )

object Method:
  def fromString: PartialFunction[String, Method] =
    case "GET" => Get
    case "HEAD" => Head
    case "POST" => Post
    case "PUT" => Put
    case "DELETE" => Delete
    case "CONNECT" => Connect
    case "OPTIONS" => Options
    case "TRACE" => Trace
    case "PATCH" => Patch

object Get extends Method("GET")

object Head extends Method("HEAD")

object Post extends Method("POST")

object Put extends Method("PUT")

object Delete extends Method("DELETE")

object Connect extends Method("CONNECT")

object Options extends Method("OPTIONS")

object Trace extends Method("TRACE")

object Patch extends Method("PATCH")
