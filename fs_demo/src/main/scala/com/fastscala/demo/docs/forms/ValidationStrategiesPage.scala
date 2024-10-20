package com.fastscala.demo.docs.forms

import com.fastscala.core.FSContext
import com.fastscala.demo.docs.MultipleCodeExamples2Page
import com.fastscala.demo.docs.forms.DefaultBSForm7Renderer.given
import com.fastscala.templates.bootstrap5.utils.BSBtn
import com.fastscala.templates.form7.fields.*
import com.fastscala.templates.form7.fields.layout.F7VerticalField
import com.fastscala.templates.form7.fields.text.*
import com.fastscala.templates.form7.{ DefaultForm7, F7Field }
import com.fastscala.xml.scala_xml.FSScalaXmlEnv.given

class ValidationStrategiesPage extends MultipleCodeExamples2Page():
  override def pageTitle: String = "Form 7 Validation Strategies"

  import com.fastscala.templates.bootstrap5.helpers.BSHelpers.{ given, * }

  override def renderContentsWithSnippets()(implicit fsc: FSContext): Unit =
    renderSnippet("ValidateBeforeUserInput"):
      new DefaultForm7:
        validateBeforeUserInput()
        lazy val rootField: F7Field = F7VerticalField(
          F7StringField()
            .label("Name")
            .addValidation(_.currentValue.length > 5, _ => <span>Minimum length of 5 chars</span>),
          F7IntOptField().label("Integer field"),
          F7SaveButtonField(_ => BSBtn().BtnPrimary.lbl("Submit").btn.d_block.w_100),
        )
      .render()
    renderSnippet("ValidateEachFieldAfterUserInput"):
      new DefaultForm7:
        validateEachFieldAfterUserInput()
        lazy val rootField: F7Field = F7VerticalField(
          F7StringField()
            .label("Name")
            .addValidation(_.currentValue.length > 5, _ => <span>Minimum length of 5 chars</span>),
          F7IntOptField().label("Integer field"),
          F7SaveButtonField(_ => BSBtn().BtnPrimary.lbl("Submit").btn.d_block.w_100),
        )
      .render()
    renderSnippet("ValidateOnAttemptSubmitOnly"):
      new DefaultForm7:
        validateOnAttemptSubmitOnly()
        lazy val rootField: F7Field = F7VerticalField(
          F7StringField()
            .label("Name")
            .addValidation(_.currentValue.length > 5, _ => <span>Minimum length of 5 chars</span>),
          F7IntOptField().label("Integer field"),
          F7SaveButtonField(_ => BSBtn().BtnPrimary.lbl("Submit").btn.d_block.w_100),
        )
      .render()
    closeSnippet()
