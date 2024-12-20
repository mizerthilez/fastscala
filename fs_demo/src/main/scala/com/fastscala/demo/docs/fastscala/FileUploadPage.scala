package com.fastscala.demo.docs.fastscala

import java.util.Base64

import com.fastscala.core.{ FSContext, FSSessionVarOpt, FSUploadedFile }
import com.fastscala.demo.docs.MultipleCodeExamples2Page
import com.fastscala.templates.bootstrap5.utils.FileUpload
import com.fastscala.xml.scala_xml.JS

// === code snippet ===
object UploadedImage extends FSSessionVarOpt[FSUploadedFile]
// === code snippet ===

class FileUploadPage extends MultipleCodeExamples2Page:
  def pageTitle: String = "File Upload Example"

  def renderContentsWithSnippets()(implicit fsc: FSContext): Unit =
    renderSnippet("Source"):
      import com.fastscala.templates.bootstrap5.helpers.BSHelpers.{ given, * }
      JS.rerenderable(rerenderer =>
        implicit fsc =>
          div.border.p_2.rounded.apply:
            UploadedImage() match
              case Some(uploadedFile) =>
                h3.apply("Uploaded image:") ++
                  <img class="w-100" src={
                    s"data:${uploadedFile.contentType};base64, " + Base64.getEncoder.encodeToString(
                      uploadedFile.content
                    )
                  }></img>.mx_auto.my_4.d_block
              case None =>
                h3.apply("Upload an image:") ++
                  FileUpload: uploadedFile =>
                    UploadedImage() = uploadedFile.head
                    rerenderer.rerender()
      ).render()

    closeSnippet()
