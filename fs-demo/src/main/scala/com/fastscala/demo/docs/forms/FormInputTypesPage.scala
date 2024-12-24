package com.fastscala.demo.docs.forms

import com.fastscala.core.FSContext
import com.fastscala.demo.docs.MultipleCodeExamples2Page
import com.fastscala.js.Js
import com.fastscala.components.bootstrap5.modals.BSModal5
import com.fastscala.components.bootstrap5.utils.BSBtn
import com.fastscala.components.form7.fields.*
import com.fastscala.components.form7.fields.layout.F7VerticalField
import com.fastscala.components.form7.{ DefaultForm7, F7Field }
import com.fastscala.xml.scala_xml.FSScalaXmlEnv.given

class FormInputTypesPage extends MultipleCodeExamples2Page:
  def pageTitle: String = "Form 7 Input Types"

  import DefaultFSDemoBSForm7Renderers.given
  import com.fastscala.components.bootstrap5.helpers.BSHelpers.{ given, * }

  def renderContentsWithSnippets()(using FSContext): Unit =
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
            BSModal5.verySimple("Your input", "Done"): modal =>
              _ => fs_4(s"Your selection:") ++ pre(inputField.currentValue.toString)

          lazy val rootField: F7Field = F7VerticalField(
            inputField,
            F7SubmitButtonField(_ => BSBtn().BtnPrimary.lbl("Submit").btn.d_block),
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

      div.border.p_2.rounded:
        new DefaultForm7:
          override def postSubmitForm()(using FSContext): Js =
            BSModal5.verySimple("Your input", "Done"): modal =>
              _ =>
                fs_4(s"Your selection:") ++
                  pre(inputField.currentValue.toString) ++
                  pre(inputField2.currentValue.toString)

          lazy val rootField: F7Field = F7VerticalField(
            inputField,
            inputField2,
            F7SubmitButtonField(_ => BSBtn().BtnPrimary.lbl("Submit").btn.d_block),
          )
        .render()
    closeSnippet()
