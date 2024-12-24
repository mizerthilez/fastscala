package com.fastscala.demo.docs.forms

import com.fastscala.core.FSContext
import com.fastscala.demo.docs.MultipleCodeExamples2Page
import com.fastscala.components.bootstrap5.utils.BSBtn
import com.fastscala.components.form7.fields.*
import com.fastscala.components.form7.fields.layout.F7VerticalField
import com.fastscala.components.form7.fields.text.*
import com.fastscala.components.form7.{ DefaultForm7, F7Field }
import com.fastscala.xml.scala_xml.FSScalaXmlEnv.given

class ValidationStrategiesPage extends MultipleCodeExamples2Page():
  override def pageTitle: String = "Form 7 Validation Strategies"

  import DefaultFSDemoBSForm7Renderers.given
  import com.fastscala.components.bootstrap5.helpers.BSHelpers.{ given, * }

  override def renderContentsWithSnippets()(using FSContext): Unit =
    renderSnippet("ValidateBeforeUserInput (always validates)"):
      new DefaultForm7:
        validateBeforeUserInput()
        lazy val rootField: F7Field = F7VerticalField(
          F7StringField()
            .label("Name")
            .addValidation(_.currentValue.length >= 5, _ => <span>Error: input less than 5 characters</span>),
          F7IntOptField().label("Integer field"),
          F7SubmitButtonField(_ => BSBtn().BtnPrimary.lbl("Submit").btn.d_block.w_100),
        )
      .render()
    renderSnippet("ValidateEachFieldAfterUserInput (validates immediately after input in a field)"):
      new DefaultForm7:
        validateEachFieldAfterUserInput()
        lazy val rootField: F7Field = F7VerticalField(
          F7StringField()
            .label("Name")
            .addValidation(_.currentValue.length >= 5, _ => <span>Error: input less than 5 characters</span>),
          F7IntOptField().label("Integer field"),
          F7SubmitButtonField(_ => BSBtn().BtnPrimary.lbl("Submit").btn.d_block.w_100),
        )
      .render()
    renderSnippet("ValidateOnAttemptSubmitOnly (only validates on submit)"):
      new DefaultForm7:
        validateOnAttemptSubmitOnly()
        lazy val rootField: F7Field = F7VerticalField(
          F7StringField()
            .label("Name")
            .addValidation(_.currentValue.length >= 5, _ => <span>Error: input less than 5 characters</span>),
          F7IntOptField().label("Integer field"),
          F7SubmitButtonField(_ => BSBtn().BtnPrimary.lbl("Submit").btn.d_block.w_100),
        )
      .render()
    closeSnippet()
