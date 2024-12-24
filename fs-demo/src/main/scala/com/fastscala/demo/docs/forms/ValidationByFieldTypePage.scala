package com.fastscala.demo.docs.forms

import java.awt.Color
import scala.util.chaining.scalaUtilChainingOps

import com.fastscala.core.FSContext
import com.fastscala.demo.docs.MultipleCodeExamples2Page
import com.fastscala.js.Js
import com.fastscala.components.bootstrap5.form7.renderermodifiers.CheckboxStyle
import com.fastscala.components.bootstrap5.modals.BSModal5
import com.fastscala.components.bootstrap5.toast.BSToast2
import com.fastscala.components.bootstrap5.utils.BSBtn
import com.fastscala.components.form7.fields.*
import com.fastscala.components.form7.fields.layout.F7VerticalField
import com.fastscala.components.form7.fields.multiselect.F7MultiSelectField
import com.fastscala.components.form7.fields.radio.F7RadioField
import com.fastscala.components.form7.fields.select.F7SelectField
import com.fastscala.components.form7.fields.text.*
import com.fastscala.components.form7.{ DefaultForm7, F7Field }
import com.fastscala.xml.scala_xml.FSScalaXmlEnv.given
import com.fastscala.components.bootstrap5.form7.renderermodifiers.CheckboxAlignment

class ValidationByFieldTypePage extends MultipleCodeExamples2Page():
  def pageTitle: String = "Form 7 Input Types"

  import DefaultFSDemoBSForm7Renderers.given
  import com.fastscala.components.bootstrap5.helpers.BSHelpers.{ given, * }

  def renderContentsWithSnippets()(using FSContext): Unit =
    renderSnippet("String input"):
      new DefaultForm7:
        validateBeforeUserInput()

        lazy val rootField: F7Field = F7VerticalField(
          F7StringField()
            .label("Name")
            .addValidation(_.currentValue.length >= 5, _ => div("Error: minimum of 5 chars"))
            .help("Input at least 5 characters"),
          F7SubmitButtonField(_ => BSBtn().BtnPrimary.lbl("Submit").btn.d_block),
        )

        override def postSubmitForm()(using FSContext): Js =
          BSToast2
            .VerySimple(div(strong("Submitted!").me_auto))(div("Submission was successful").my_2)
            .installAndShow()
      .render()
        .pipe: renderedForm =>
          div.border.p_2.rounded.apply(renderedForm)

    renderSnippet("Textarea"):
      new DefaultForm7:
        validateBeforeUserInput()

        lazy val rootField: F7Field = F7VerticalField(
          F7StringTextareaField()
            .label("Your message")
            .addValidation(_.currentValue.split(" ").length >= 5, _ => div("Error: minimum of 5 words"))
            .help("Input at least 5 words"),
          F7SubmitButtonField(_ => BSBtn().BtnPrimary.lbl("Submit").btn.d_block),
        )

        override def postSubmitForm()(using FSContext): Js =
          BSToast2
            .VerySimple(div(strong("Submitted!").me_auto))(div("Submission was successful").my_2)
            .installAndShow()
      .render()
        .pipe: renderedForm =>
          div.border.p_2.rounded.apply(renderedForm)

    renderSnippet("Select"):
      val colors: List[(String, Color)] = List(
        "WHITE" -> java.awt.Color.WHITE,
        "LIGHT_GRAY" -> java.awt.Color.LIGHT_GRAY,
        "GRAY" -> java.awt.Color.GRAY,
        "DARK_GRAY" -> java.awt.Color.DARK_GRAY,
        "BLACK" -> java.awt.Color.BLACK,
        "RED" -> java.awt.Color.RED,
        "PINK" -> java.awt.Color.PINK,
        "ORANGE" -> java.awt.Color.ORANGE,
        "YELLOW" -> java.awt.Color.YELLOW,
        "GREEN" -> java.awt.Color.GREEN,
        "MAGENTA" -> java.awt.Color.MAGENTA,
        "CYAN" -> java.awt.Color.CYAN,
        "BLUE" -> java.awt.Color.BLUE,
      )
      val inputField = F7SelectField(colors)
        .label("Color")
        .option2String(_._1)
        .addValidation(_.currentValue != colors.head, _ => div("Error: cannot be white!"))
        .help("Choose a color different from white.")

      div.border.p_2.rounded:
        new DefaultForm7:
          validateBeforeUserInput()

          lazy val rootField: F7Field = F7VerticalField(
            inputField,
            F7SubmitButtonField(_ => BSBtn().BtnPrimary.lbl("Submit").btn.d_block),
          )

          override def postSubmitForm()(using FSContext): Js =
            BSToast2
              .VerySimple(div(strong("Submitted!").me_auto))(
                div("Submission was successful").my_2
              )
              .installAndShow()
        .render()

    renderSnippet("Multi Select"):
      val continents: List[String] = List(
        "Asia",
        "Africa",
        "North America",
        "South America",
        "Antarctica",
        "Europe",
        "Australia",
      )
      val inputField = F7MultiSelectField()
        .options(continents)
        .label("Continents")
        .size(10)
        .addValidation(
          _.currentValue.size >= 2,
          _ => div("Error: please select at least 2 continents."),
        )
        .help("Include at least two continents in your selection.")

      div.border.p_2.rounded:
        new DefaultForm7:
          validateBeforeUserInput()

          lazy val rootField: F7Field = F7VerticalField(
            inputField,
            F7SubmitButtonField(_ => BSBtn().BtnPrimary.lbl("Submit").btn.d_block),
          )

          override def postSubmitForm()(using FSContext): Js =
            BSModal5.verySimple("Your input", "Done"): modal =>
              _ => div(s"Your selected continents: " + inputField.currentValue.mkString(", "))
        .render()

    renderSnippet("Checkbox"):
      val inputField = F7CheckboxField()
        .label("I agree to the Terms of Service")
        .addValidation(_.currentValue == true, _ => div("Error: you must accept the Terms of Service."))

      div.border.p_2.rounded:
        new DefaultForm7:
          validateBeforeUserInput()

          lazy val rootField: F7Field = F7VerticalField(
            inputField,
            F7SubmitButtonField(_ => BSBtn().BtnPrimary.lbl("Submit").btn.d_block),
          )

          override def postSubmitForm()(using FSContext): Js =
            BSModal5.verySimple("Your input", "Done"): modal =>
              _ => div(s"Your selection: " + inputField.currentValue)
        .render()

    renderSnippet("Checkbox as switch"):
      val renderers = FSDemoBSForm7Renderers(using CheckboxAlignment.default, CheckboxStyle.Switch)
      import renderers.given

      // Pass the renderer which renders as switches:
      val inputField = F7CheckboxField()
        .label("I agree to the Terms of Service")
        .addValidation(_.currentValue == true, _ => div("Error: you must accept the Terms of Service."))

      div.border.p_2.rounded:
        new DefaultForm7:
          validateBeforeUserInput()

          lazy val rootField: F7Field = F7VerticalField(
            inputField,
            F7SubmitButtonField(_ => BSBtn().BtnPrimary.lbl("Submit").btn.d_block),
          )

          override def postSubmitForm()(using FSContext): Js =
            BSModal5.verySimple("Your input", "Done"): modal =>
              _ => div(s"Your selection: " + inputField.currentValue)
        .render()

    renderSnippet("Radio"):
      val inputField = F7RadioField(Seq("Android", "iOS", "Others"))
        .label("Your phone")
        .setInternalValue("Others")
        .help("Select your phone type, iOS or Android (others not supported)")
        .addValidation(_.currentValue != "Others", _ => <div>Only Android and iOS are supported.</div>)

      div.border.p_2.rounded:
        new DefaultForm7:
          validateBeforeUserInput()

          lazy val rootField: F7Field = F7VerticalField(
            inputField,
            F7SubmitButtonField(_ => BSBtn().BtnPrimary.lbl("Submit").btn.d_block),
          )

          override def postSubmitForm()(using FSContext): Js =
            BSModal5.verySimple("Your input", "Done"): modal =>
              _ => div(s"Your phone type: ${inputField.currentValue}")
        .render()

    closeSnippet()
