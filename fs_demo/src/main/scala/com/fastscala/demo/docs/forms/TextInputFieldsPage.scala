package com.fastscala.demo.docs.forms

import java.time.{ LocalDate, LocalDateTime }

import com.fastscala.core.FSContext
import com.fastscala.demo.docs.MultipleCodeExamples2Page
import com.fastscala.js.Js
import com.fastscala.templates.bootstrap5.modals.BSModal5
import com.fastscala.templates.bootstrap5.utils.BSBtn
import com.fastscala.templates.form7.fields.*
import com.fastscala.templates.form7.fields.layout.F7VerticalField
import com.fastscala.templates.form7.fields.text.*
import com.fastscala.templates.form7.{ DefaultForm7, F7Field }
import com.fastscala.xml.scala_xml.FSScalaXmlEnv.given

class TextInputFieldsPage extends MultipleCodeExamples2Page:
  def pageTitle: String = "Form 7 Text Input Fields"

  import DefaultFSDemoBSForm7Renderers.given
  import com.fastscala.templates.bootstrap5.helpers.BSHelpers.{ given, * }

  def renderContentsWithSnippets()(using FSContext): Unit =
    renderSnippet("Text-based input fields"):
      val stringField = F7StringField()
        .label("Name (string input)")
        .help("An empty input translates into an empty string")
        .setInternalValue("John Doe")
      val stringOptField = F7StringOptField()
        .label("Job title")
        .help("An empty input translates into an empty option")
        .setInternalValue(Some("CEO"))
      val doubleField = F7DoubleField()
        .label("Your height")
        .help("An empty input translates into zero")
        .setInternalValue(1.8)
      val doubleOptField = F7DoubleOptField()
        .label("Your height")
        .help("An empty input translates into an empty option")
        .setInternalValue(Some(1.8))
      val intField = F7IntField()
        .label("Your age")
        .help("An empty input translates into zero")
        .setInternalValue(47)
      val intOptField = F7IntOptField()
        .label("Your age")
        .help("An empty input translates into an empty option")
        .setInternalValue(Some(47))
      val stringOptTextareaField = F7StringOptTextareaField()
        .rows(6)
        .label("Your message")
        .setInternalValue(Some("Fast Scala is great!"))
      val localDateOptField = F7LocalDateOptField()
        .label("Date of birth")
        .help("An empty input translates into an empty option")
        .setInternalValue(Some(LocalDate.now().minusYears(47)))
      val localDateTimeOptField = F7LocalDateTimeOptField()
        .label("Meeting date/time")
        .help("An empty input translates into an empty option")
        .setInternalValue(Some(LocalDateTime.now().plusDays(1)))

      div:
        new DefaultForm7:
          lazy val rootField: F7Field = F7VerticalField(
            stringField,
            stringOptField,
            doubleField,
            doubleOptField,
            intField,
            intOptField,
            stringOptTextareaField,
            localDateOptField,
            localDateTimeOptField,
            F7SubmitButtonField(_ => BSBtn().BtnPrimary.lbl("Submit").btn.d_block.w_100),
          )

          override def postSubmitForm()(using FSContext): Js =
            BSModal5.verySimple("Submitted!", "Ok"): modal =>
              _ =>
                <h6>Your data:</h6> ++
                  <ul>
                  <li><b>String Field:</b> {stringField.getInternalValue()}</li>
                  <li><b>Optional String Field:</b> {stringOptField.getInternalValue()}</li>
                  <li><b>Double Field:</b> {doubleField.getInternalValue()}</li>
                  <li><b>Optional Double Field:</b> {doubleOptField.getInternalValue()}</li>
                  <li><b>Int Field:</b> {intField.getInternalValue()}</li>
                  <li><b>Optional Int Field:</b> {intOptField.getInternalValue()}</li>
                  <li><b>Optional Textarea Field:</b> {stringOptTextareaField.getInternalValue()}</li>
                  <li><b>Optional LocalDate Field:</b> {localDateOptField.getInternalValue()}</li>
                  <li><b>Optional LocalDateTime Field:</b> {localDateTimeOptField.getInternalValue()}</li>
                </ul>
        .render()

    closeSnippet()
