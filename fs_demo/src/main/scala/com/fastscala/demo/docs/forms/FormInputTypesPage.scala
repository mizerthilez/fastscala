package com.fastscala.demo.docs.forms

import java.awt.Color
import java.time.format.DateTimeFormatter

import com.fastscala.core.FSContext
import com.fastscala.demo.docs.MultipleCodeExamples2Page
import com.fastscala.js.Js
import com.fastscala.templates.bootstrap5.modals.BSModal5
import com.fastscala.templates.bootstrap5.utils.BSBtn
import com.fastscala.templates.form7.fields.*
import com.fastscala.templates.form7.fields.layout.F7VerticalField
import com.fastscala.templates.form7.fields.select.{ F7MultiSelectField, F7SelectField, F7SelectOptField }
import com.fastscala.templates.form7.fields.text.*
import com.fastscala.templates.form7.{ DefaultForm7, F7Field }
import com.fastscala.xml.scala_xml.FSScalaXmlEnv.given

class FormInputTypesPage extends MultipleCodeExamples2Page():
  override def pageTitle: String = "Form 7 Input Types"

  import DefaultBSForm7Renderer.*
  import com.fastscala.templates.bootstrap5.helpers.BSHelpers.{ given, * }

  override def renderContentsWithSnippets()(implicit fsc: FSContext): Unit =
    renderSnippet("String input"):
      val inputField = new F7StringField().label("Name")

      div.border.p_2.rounded.apply:
        new DefaultForm7():
          override def postSubmitForm()(implicit fsc: FSContext): Js =
            BSModal5.verySimple("Your input", "Done")(modal =>
              implicit fsc => fs_4.apply(s"Your name is ${inputField.currentValue}")
            )

          override lazy val rootField: F7Field = F7VerticalField()(
            inputField,
            new F7SaveButtonField(implicit fsc => BSBtn().BtnPrimary.lbl("Submit").btn.d_block),
          )
        .render()
    renderSnippet("(Optional) String input"):
      val inputField = new F7StringOptField().label("Name")

      div.border.p_2.rounded.apply:
        new DefaultForm7():
          override def postSubmitForm()(implicit fsc: FSContext): Js =
            BSModal5.verySimple("Your input", "Done")(modal =>
              implicit fsc =>
                fs_4.apply(s"Your name is ${inputField.currentValue.getOrElse("[None provided]")}")
            )

          override lazy val rootField: F7Field = F7VerticalField()(
            inputField,
            new F7HtmlField(p.apply("(Experiment with submitting an empty input)")),
            new F7SaveButtonField(implicit fsc => BSBtn().BtnPrimary.lbl("Submit").btn.d_block),
          )
        .render()
    renderSnippet("(Optional) Double input"):
      val inputField = new F7DoubleOptField().label("Your height")

      div.border.p_2.rounded.apply:
        new DefaultForm7():
          override def postSubmitForm()(implicit fsc: FSContext): Js =
            BSModal5.verySimple("Your input", "Done")(modal =>
              implicit fsc =>
                fs_4.apply(
                  s"Your input is: ${inputField.currentValue.getOrElse("[None provided]")}"
                )
            )

          override lazy val rootField: F7Field = F7VerticalField()(
            inputField,
            new F7HtmlField(p.apply("(Experiment with submitting an empty input)")),
            new F7SaveButtonField(implicit fsc => BSBtn().BtnPrimary.lbl("Submit").btn.d_block),
          )
        .render()
    renderSnippet("Double input"):
      val inputField = new F7DoubleField().label("Your height")

      div.border.p_2.rounded.apply:
        new DefaultForm7():
          override def postSubmitForm()(implicit fsc: FSContext): Js =
            BSModal5.verySimple("Your input", "Done")(modal =>
              implicit fsc => fs_4.apply(s"Your input is: ${inputField.currentValue}")
            )

          override lazy val rootField: F7Field = F7VerticalField()(
            inputField,
            new F7HtmlField(p.apply("(Experiment with submitting an empty input)")),
            new F7SaveButtonField(implicit fsc => BSBtn().BtnPrimary.lbl("Submit").btn.d_block),
          )
        .render()
    renderSnippet("(Optional) Int input"):
      val inputField = new F7IntOptField().label("Your age")

      div.border.p_2.rounded.apply:
        new DefaultForm7():
          override def postSubmitForm()(implicit fsc: FSContext): Js =
            BSModal5.verySimple("Your input", "Done")(modal =>
              implicit fsc =>
                fs_4.apply(
                  s"Your input is: ${inputField.currentValue.getOrElse("[None provided]")}"
                )
            )

          override lazy val rootField: F7Field = F7VerticalField()(
            inputField,
            new F7HtmlField(p.apply("(Experiment with submitting an empty input)")),
            new F7SaveButtonField(implicit fsc => BSBtn().BtnPrimary.lbl("Submit").btn.d_block),
          )
        .render()
    renderSnippet("java.time.LocalDate input"):
      val inputField = new F7LocalDateOptField().label("Date")

      div.border.p_2.rounded.apply:
        new DefaultForm7():
          override def postSubmitForm()(implicit fsc: FSContext): Js =
            BSModal5.verySimple("Your input", "Done")(modal =>
              implicit fsc =>
                fs_4.apply(
                  s"Selected date is ${inputField.currentValue.map(_.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))).getOrElse("[None selected]")}"
                )
            )

          override lazy val rootField: F7Field = F7VerticalField()(
            inputField,
            new F7SaveButtonField(implicit fsc => BSBtn().BtnPrimary.lbl("Submit").btn.d_block),
          )
        .render()
    renderSnippet("java.time.LocalDateTime input"):
      val inputField = new F7LocalDateTimeOptField().label("Date/time")

      div.border.p_2.rounded.apply:
        new DefaultForm7():
          override def postSubmitForm()(implicit fsc: FSContext): Js =
            BSModal5.verySimple("Your input", "Done")(modal =>
              implicit fsc =>
                fs_4.apply(
                  s"Selected date/time is ${inputField.currentValue.map(_.format(DateTimeFormatter.ofPattern("HH:mm dd MMM yyyy"))).getOrElse("[None selected]")}"
                )
            )

          override lazy val rootField: F7Field = F7VerticalField()(
            inputField,
            new F7SaveButtonField(implicit fsc => BSBtn().BtnPrimary.lbl("Submit").btn.d_block),
          )
        .render()
    renderSnippet("Textarea"):
      val inputField = new F7StringOptTextareaField().rows(6).label("Your message")

      div.border.p_2.rounded.apply:
        new DefaultForm7():
          override def postSubmitForm()(implicit fsc: FSContext): Js =
            BSModal5.verySimple("Your input", "Done")(modal =>
              implicit fsc =>
                fs_4.apply(s"Your message:") ++
                  pre.apply(inputField.currentValue.getOrElse("[No message provided]"))
            )

          override lazy val rootField: F7Field = F7VerticalField()(
            inputField,
            new F7SaveButtonField(implicit fsc => BSBtn().BtnPrimary.lbl("Submit").btn.d_block),
          )
        .render()
    renderSnippet("Select (Optional)"):
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
      val inputField = new F7SelectOptField[Color]().optionsNonEmpty(colors).label("Color")

      div.border.p_2.rounded.apply:
        new DefaultForm7():
          override def postSubmitForm()(implicit fsc: FSContext): Js =
            BSModal5.verySimple("Your input", "Done")(modal =>
              implicit fsc =>
                fs_4.apply(s"Your selection:") ++
                  pre.apply(inputField.currentValue.map(_.toString).getOrElse("[None selected]"))
            )

          override lazy val rootField: F7Field = F7VerticalField()(
            inputField,
            new F7SaveButtonField(implicit fsc => BSBtn().BtnPrimary.lbl("Submit").btn.d_block),
          )
        .render()
    renderSnippet("Select (one always must be selected)"):
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
      val inputField = new F7SelectField[Color](colors).label("Color")

      div.border.p_2.rounded.apply:
        new DefaultForm7():
          override def postSubmitForm()(implicit fsc: FSContext): Js =
            BSModal5.verySimple("Your input", "Done")(modal =>
              implicit fsc =>
                fs_4.apply(s"Your selection:") ++
                  pre.apply(inputField.currentValue.toString)
            )

          override lazy val rootField: F7Field = F7VerticalField()(
            inputField,
            new F7SaveButtonField(implicit fsc => BSBtn().BtnPrimary.lbl("Submit").btn.d_block),
          )
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
      val inputField = new F7MultiSelectField().options(continents).label("Continents").size(10)

      div.border.p_2.rounded.apply:
        new DefaultForm7():
          override def postSubmitForm()(implicit fsc: FSContext): Js =
            BSModal5.verySimple("Your input", "Done")(modal =>
              implicit fsc =>
                fs_4.apply(s"Your selected continents:") ++
                  pre.apply(inputField.currentValue.mkString(", "))
            )

          override lazy val rootField: F7Field = F7VerticalField()(
            inputField,
            new F7SaveButtonField(implicit fsc => BSBtn().BtnPrimary.lbl("Submit").btn.d_block),
          )
        .render()
    renderSnippet("Checkbox"):

      val inputField = new F7CheckboxField().label("Has driving license")

      div.border.p_2.rounded.apply:
        new DefaultForm7():
          override def postSubmitForm()(implicit fsc: FSContext): Js =
            BSModal5.verySimple("Your input", "Done")(modal =>
              implicit fsc =>
                fs_4.apply(s"Your selection:") ++
                  pre.apply(inputField.currentValue.toString)
            )

          override lazy val rootField: F7Field = F7VerticalField()(
            inputField,
            new F7SaveButtonField(implicit fsc => BSBtn().BtnPrimary.lbl("Submit").btn.d_block),
          )
        .render()
    renderSnippet("Enum-based"):

      object OutputState extends Enumeration:
        val High, Low, HighZ = Value

      val inputField = F7EnumField
        .Nullable(OutputState)
        .label("Output State")
        .option2String(_.map(_.toString).getOrElse("--"))

      div.border.p_2.rounded.apply:
        new DefaultForm7():
          override def postSubmitForm()(implicit fsc: FSContext): Js =
            BSModal5.verySimple("Your input", "Done")(modal =>
              implicit fsc =>
                fs_4.apply(s"Your selection:") ++
                  pre.apply(inputField.currentValue.toString)
            )

          override lazy val rootField: F7Field = F7VerticalField()(
            inputField,
            new F7SaveButtonField(implicit fsc => BSBtn().BtnPrimary.lbl("Submit").btn.d_block),
          )
        .render()
    renderSnippet("New Enum-based"):

      sealed trait Family
      case class House(name: String) extends Family
      case object Dad extends Family
      case object Mum extends Family
      case object Baby extends Family

      val inputField = F7EnumField
        .NonNullable[Family]
        .label("Family")
        .option2String(_.toString)

      enum Color(val rgb: Int):
        case Custom(c: Int) extends Color(c)
        case Red extends Color(0xff0000)
        case Green extends Color(0x00ff00)
        case Blue extends Color(0x0000ff)

      val inputField2 = F7EnumField
        .Nullable[Color]
        .label("Color")
        .option2String(_.map(_.toString).getOrElse("--"))

      div.border.p_2.rounded.apply:
        new DefaultForm7():
          override def postSubmitForm()(implicit fsc: FSContext): Js =
            BSModal5.verySimple("Your input", "Done")(modal =>
              implicit fsc =>
                fs_4.apply(s"Your selection:") ++
                  pre.apply(inputField.currentValue.toString) ++
                  pre.apply(inputField2.currentValue.toString)
            )

          override lazy val rootField: F7Field = F7VerticalField()(
            inputField,
            inputField2,
            new F7SaveButtonField(implicit fsc => BSBtn().BtnPrimary.lbl("Submit").btn.d_block),
          )
        .render()
    closeSnippet()
