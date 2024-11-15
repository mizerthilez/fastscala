package com.fastscala.demo.docs.fastscala

import java.util.Base64

import com.fastscala.core.FSContext
import com.fastscala.demo.docs.MultipleCodeExamples2Page
import com.fastscala.xml.scala_xml.JS

// === code snippet ===
class VisualizeUploadedImageAnonymousPage(
  contentType: String,
  contents: Array[Byte],
) extends MultipleCodeExamples2Page:
  def pageTitle: String = "Visualize Uploaded Image Anonymous Page Example"

  def renderContentsWithSnippets()(implicit fsc: FSContext): Unit =
    renderSnippet("Source"):
      import com.fastscala.templates.bootstrap5.helpers.BSHelpers.{ given, * }
      JS.rerenderable(rerenderer =>
        implicit fsc =>
          div.border.p_2.rounded.apply:
            h3.apply("Uploaded image:") ++
              <img class="w-100" src={
                s"data:$contentType;base64, " + Base64.getEncoder.encodeToString(contents)
              }></img>.mx_auto.my_4.d_block
      ).render()

    closeSnippet()
