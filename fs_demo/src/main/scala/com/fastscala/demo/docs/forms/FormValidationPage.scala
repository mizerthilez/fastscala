package com.fastscala.demo.docs.forms

import java.awt.Color
import java.time.format.DateTimeFormatter

import com.fastscala.core.FSContext
import com.fastscala.demo.docs.MultipleCodeExamples2Page
import com.fastscala.demo.docs.forms.DefaultBSForm7Renderer.given
import com.fastscala.js.Js
import com.fastscala.templates.bootstrap5.modals.BSModal5
import com.fastscala.templates.bootstrap5.utils.BSBtn
import com.fastscala.templates.form7.fields.*
import com.fastscala.templates.form7.fields.layout.F7VerticalField
import com.fastscala.templates.form7.fields.select.{ F7MultiSelectField, F7SelectField, F7SelectOptField }
import com.fastscala.templates.form7.fields.text.*
import com.fastscala.templates.form7.{ DefaultForm7, F7Field }
import com.fastscala.xml.scala_xml.FSScalaXmlEnv.given

class FormValidationPage extends MultipleCodeExamples2Page():
  override def pageTitle: String = "Form 7 Validation"

  import com.fastscala.templates.bootstrap5.helpers.BSHelpers.{ given, * }

  override def renderContentsWithSnippets()(implicit fsc: FSContext): Unit =
    renderSnippet("String input"):
      new DefaultForm7:
        lazy val rootField: F7Field = F7VerticalField(
          F7StringField()
            .label("Name")
            .addValidation(_.currentValue.length > 5, _ => <span>Minimum length of 5 chars</span>),
          F7SaveButtonField(_ => BSBtn().BtnPrimary.lbl("Submit").btn.d_block.w_100),
        )
      .render()
    renderSnippet("(Optional) String input"):
      val inputField = F7StringOptField().label("Name")

      div.border.p_2.rounded:
        new DefaultForm7:
          override def postSubmitForm()(using FSContext): Js =
            BSModal5.verySimple("Your input", "Done"): model =>
              _ => fs_4(s"Your name is ${inputField.currentValue.getOrElse("[None provided]")}")

          lazy val rootField: F7Field = F7VerticalField(
            inputField,
            F7HtmlField(p("(Experiment with submitting an empty input)")),
            F7SaveButtonField(_ => BSBtn().BtnPrimary.lbl("Submit").btn.d_block),
          )
        .render()
    renderSnippet("(Optional) Double input"):
      val inputField = F7DoubleOptField().label("Your height")

      div.border.p_2.rounded:
        new DefaultForm7:
          override def postSubmitForm()(using FSContext): Js =
            BSModal5.verySimple("Your input", "Done"): model =>
              _ => fs_4(s"Your input is: ${inputField.currentValue.getOrElse("[None provided]")}")

          lazy val rootField: F7Field = F7VerticalField(
            inputField,
            F7HtmlField(p("(Experiment with submitting an empty input)")),
            F7SaveButtonField(_ => BSBtn().BtnPrimary.lbl("Submit").btn.d_block),
          )
        .render()
    renderSnippet("Double input"):
      val inputField = F7DoubleField().label("Your height")

      div.border.p_2.rounded:
        new DefaultForm7:
          override def postSubmitForm()(using FSContext): Js =
            BSModal5.verySimple("Your input", "Done"): model =>
              _ => fs_4(s"Your input is: ${inputField.currentValue}")

          lazy val rootField: F7Field = F7VerticalField(
            inputField,
            F7HtmlField(p("(Experiment with submitting an empty input)")),
            F7SaveButtonField(_ => BSBtn().BtnPrimary.lbl("Submit").btn.d_block),
          )
        .render()
    renderSnippet("(Optional) Int input"):
      val inputField = F7IntOptField().label("Your age")

      div.border.p_2.rounded:
        new DefaultForm7:
          override def postSubmitForm()(using FSContext): Js =
            BSModal5.verySimple("Your input", "Done"): model =>
              _ => fs_4(s"Your input is: ${inputField.currentValue.getOrElse("[None provided]")}")

          lazy val rootField: F7Field = F7VerticalField(
            inputField,
            F7HtmlField(p("(Experiment with submitting an empty input)")),
            F7SaveButtonField(_ => BSBtn().BtnPrimary.lbl("Submit").btn.d_block),
          )
        .render()
    renderSnippet("java.time.LocalDate input"):
      val inputField = new F7LocalDateOptField().label("Date")

      div.border.p_2.rounded:
        new DefaultForm7:
          override def postSubmitForm()(using FSContext): Js =
            BSModal5.verySimple("Your input", "Done"): model =>
              _ =>
                fs_4(
                  s"Selected date is ${inputField.currentValue.map(_.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))).getOrElse("[None selected]")}"
                )

          lazy val rootField: F7Field = F7VerticalField(
            inputField,
            F7SaveButtonField(_ => BSBtn().BtnPrimary.lbl("Submit").btn.d_block),
          )
        .render()
    renderSnippet("java.time.LocalDateTime input"):
      val inputField = F7LocalDateTimeOptField().label("Date/time")

      div.border.p_2.rounded:
        new DefaultForm7:
          override def postSubmitForm()(using FSContext): Js =
            BSModal5.verySimple("Your input", "Done"): model =>
              _ =>
                fs_4:
                  s"Selected date/time is ${inputField.currentValue.map(_.format(DateTimeFormatter.ofPattern("HH:mm dd MMM yyyy"))).getOrElse("[None selected]")}"

          lazy val rootField: F7Field = F7VerticalField(
            inputField,
            F7SaveButtonField(_ => BSBtn().BtnPrimary.lbl("Submit").btn.d_block),
          )
        .render()
    renderSnippet("Textarea"):
      val inputField = F7StringOptTextareaField().rows(6).label("Your message")

      div.border.p_2.rounded:
        new DefaultForm7:
          override def postSubmitForm()(using FSContext): Js =
            BSModal5.verySimple("Your input", "Done"): model =>
              _ => fs_4(s"Your message:") ++ pre(inputField.currentValue.getOrElse("[No message provided]"))

          lazy val rootField: F7Field = F7VerticalField(
            inputField,
            F7SaveButtonField(_ => BSBtn().BtnPrimary.lbl("Submit").btn.d_block),
          )
        .render()
    renderSnippet("Select (Optional)") {
      val colors: List[Color] = List(
        java.awt.Color.WHITE,
        java.awt.Color.LIGHT_GRAY,
        java.awt.Color.GRAY,
        java.awt.Color.DARK_GRAY,
        java.awt.Color.BLACK,
        java.awt.Color.RED,
        java.awt.Color.PINK,
        java.awt.Color.ORANGE,
        java.awt.Color.YELLOW,
        java.awt.Color.GREEN,
        java.awt.Color.MAGENTA,
        java.awt.Color.CYAN,
        java.awt.Color.BLUE,
      )
      val inputField = F7SelectOptField[Color]().optionsNonEmpty(colors).label("Color")

      div.border.p_2.rounded:
        new DefaultForm7:
          override def postSubmitForm()(using FSContext): Js =
            BSModal5.verySimple("Your input", "Done"): model =>
              _ =>
                fs_4(s"Your selection:") ++
                  pre(inputField.currentValue.map(_.toString).getOrElse("[None selected]"))

          lazy val rootField: F7Field = F7VerticalField(
            inputField,
            F7SaveButtonField(_ => BSBtn().BtnPrimary.lbl("Submit").btn.d_block),
          )
        .render()
    }
    renderSnippet("Select (one always must be selected)") {
      val colors: List[Color] = List(
        java.awt.Color.WHITE,
        java.awt.Color.LIGHT_GRAY,
        java.awt.Color.GRAY,
        java.awt.Color.DARK_GRAY,
        java.awt.Color.BLACK,
        java.awt.Color.RED,
        java.awt.Color.PINK,
        java.awt.Color.ORANGE,
        java.awt.Color.YELLOW,
        java.awt.Color.GREEN,
        java.awt.Color.MAGENTA,
        java.awt.Color.CYAN,
        java.awt.Color.BLUE,
      )
      val inputField = F7SelectField[Color](colors).label("Color")

      div.border.p_2.rounded:
        new DefaultForm7:
          override def postSubmitForm()(using FSContext): Js =
            BSModal5.verySimple("Your input", "Done"): model =>
              _ => fs_4(s"Your selection:") ++ pre(inputField.currentValue.toString)

          lazy val rootField: F7Field = F7VerticalField(
            inputField,
            F7SaveButtonField(_ => BSBtn().BtnPrimary.lbl("Submit").btn.d_block),
          )
        .render()
    }
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
      val inputField = F7MultiSelectField().options(continents).label("Continents").size(10)

      div.border.p_2.rounded:
        new DefaultForm7:
          override def postSubmitForm()(using FSContext): Js =
            BSModal5.verySimple("Your input", "Done"): model =>
              _ => fs_4(s"Your selected continents:") ++ pre(inputField.currentValue.mkString(", "))

          lazy val rootField: F7Field = F7VerticalField(
            inputField,
            F7SaveButtonField(_ => BSBtn().BtnPrimary.lbl("Submit").btn.d_block),
          )
        .render()
    renderSnippet("Checkbox"):

      val inputField = F7CheckboxField().label("Has driving license")

      div.border.p_2.rounded:
        new DefaultForm7:
          override def postSubmitForm()(using FSContext): Js =
            BSModal5.verySimple("Your input", "Done"): model =>
              _ => fs_4(s"Your selection:") ++ pre(inputField.currentValue.toString)

          lazy val rootField: F7Field = F7VerticalField(
            inputField,
            F7SaveButtonField(_ => BSBtn().BtnPrimary.lbl("Submit").btn.d_block),
          )
        .render()
    renderSnippet("Enum-based"):

      object OutputState extends Enumeration:
        val High, Low, HighZ = Value

      val inputField = F7EnumField
        .Nullable(OutputState)
        .label("Output State")
        .option2String(_.map(_.toString).getOrElse("--"))

      div.border.p_2.rounded:
        new DefaultForm7:
          override def postSubmitForm()(using FSContext): Js =
            BSModal5.verySimple("Your input", "Done"): model =>
              _ => fs_4(s"Your selection:") ++ pre(inputField.currentValue.toString)

          lazy val rootField: F7Field = F7VerticalField(
            inputField,
            F7SaveButtonField(_ => BSBtn().BtnPrimary.lbl("Submit").btn.d_block),
          )
        .render()
    closeSnippet()
