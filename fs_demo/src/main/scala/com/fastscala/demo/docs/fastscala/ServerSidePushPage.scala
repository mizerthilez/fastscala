package com.fastscala.demo.docs.fastscala

import scala.concurrent.duration.DurationInt

import cats.effect.IO

import com.fastscala.core.FSContext
import com.fastscala.demo.docs.MultipleCodeExamples2Page
import com.fastscala.utils.IdGen
import com.fastscala.xml.scala_xml.JS

class ServerSidePushPage extends MultipleCodeExamples2Page:
  def pageTitle: String = "Server-Side push using Websockets"

  def renderContentsWithSnippets()(implicit fsc: FSContext): Unit =
    renderSnippet("Source"):

      import com.fastscala.templates.bootstrap5.helpers.BSHelpers.{ given, * }
      val id = IdGen.id
      val N = 30

      def factorial(n: BigInt): IO[BigInt] = for
        // _ <- IO(fsc.sendToPage(JS.prepend2(id, div(s"factorial($n)").text_white_50)))
        rslt <- if n == 0 then IO.pure(BigInt(1)) else factorial(n - 1).map(_ * n)
        _ <-
          if n < N then IO(fsc.sendToPage(JS.prepend2(id, div(s"factorial($n) = $rslt").text_white_50)))
          else IO.unit
        _ <- IO.sleep(100.millis)
      yield rslt

      import cats.effect.unsafe.implicits.global

      factorial(N).unsafeRunAsync:
        case Right(value) =>
          fsc.sendToPage:
            JS.prepend2(id, div(s"Finish computation. reuslt: $value").text_success_emphasis)
        case Left(_) => fsc.sendToPage(JS.prepend2(id, div("Error!").text_danger))

      h2(s"Calculating the factorial($N)") ++
        <div id={id}></div>.m_2.bg_secondary.p_1
          .withStyle("font-family: courier; min-height: 100px;") ++
        JS.inScriptTag(fsc.page.initWebSocket())

    closeSnippet()
