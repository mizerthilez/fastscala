package com.fastscala.demo.docs.forms

import java.time.LocalDate

import com.fastscala.core.FSContext
import com.fastscala.demo.docs.MultipleCodeExamples2Page
import com.fastscala.components.bootstrap5.utils.BSBtn
import com.fastscala.components.form7.fields.F7SubmitButtonField
import com.fastscala.components.form7.fields.layout.F7VerticalField
import com.fastscala.components.form7.fields.text.*
import com.fastscala.components.form7.{ DefaultForm7, F7Field }
import com.fastscala.xml.scala_xml.FSScalaXmlEnv.given

class FormValidationPage extends MultipleCodeExamples2Page:
  def pageTitle: String = "Form 7 Validation"

  import DefaultFSDemoBSForm7Renderers.given
  import com.fastscala.components.bootstrap5.helpers.BSHelpers.{ given, * }

  def renderContentsWithSnippets()(using FSContext): Unit =
    renderSnippet("String input"):
      new DefaultForm7:
        lazy val rootField: F7Field = F7VerticalField(
          F7StringField()
            .label("Name")
            .addValidation(
              valid = _.currentValue.length > 5,
              error = _ => <span>Minimum length of 5 chars</span>,
            ),
          F7DoubleField()
            .label("Your height")
            .addValidation(_.currentValue > 100, _ => <span>Minimum height 100cm</span>),
          F7LocalDateOptField()
            .label("Date")
            .addValidation(
              _.currentValue.exists(_.isAfter(LocalDate.now())),
              _ => <span>Must be a date in the future.</span>,
            ),
          F7SubmitButtonField(_ => BSBtn().BtnPrimary.lbl("Submit").btn.d_block.w_100),
        )
      .render()
    closeSnippet()
