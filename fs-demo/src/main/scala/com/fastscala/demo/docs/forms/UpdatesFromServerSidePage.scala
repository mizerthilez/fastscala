package com.fastscala.demo.docs.forms

import com.fastscala.core.FSContext
import com.fastscala.demo.docs.MultipleCodeExamples2Page
import com.fastscala.demo.docs.data.{ Continents, Fruit }
import com.fastscala.js.Js
import com.fastscala.components.bootstrap5.form7.layout.F7BSFormInputGroup
import com.fastscala.components.bootstrap5.toast.BSToast2
import com.fastscala.components.bootstrap5.utils.BSBtn
import com.fastscala.components.form7.fields.*
import com.fastscala.components.form7.fields.layout.{ F7ContainerField, F7VerticalField }
import com.fastscala.components.form7.fields.multiselect.F7MultiSelectField
import com.fastscala.components.form7.fields.radio.F7RadioField
import com.fastscala.components.form7.fields.select.F7SelectField
import com.fastscala.components.form7.fields.text.*
import com.fastscala.components.form7.{ DefaultForm7, F7Field }
import com.fastscala.xml.scala_xml.FSScalaXmlEnv.given

import scala.util.Random

class UpdatesFromServerSidePage extends MultipleCodeExamples2Page():
  def pageTitle: String = "Form 7 - Update client values after changes on server side"

  import DefaultFSDemoBSForm7Renderers.given
  import com.fastscala.components.bootstrap5.helpers.BSHelpers.{ given, * }

  def renderContentsWithSnippets()(using FSContext): Unit =

    renderSnippet("Example"):

      val randomSeed: F7IntField = F7IntField().step(1).label("Random seed").doSyncToServerOnChange

      val textField = F7StringField().label("Text field example").dependsOn(randomSeed)
      val textFieldInInputGroup =
        F7StringField().label("Text field in input group example").dependsOn(randomSeed)
      val textFieldInputGroup = F7BSFormInputGroup(F7HtmlField.label("Field:"), textFieldInInputGroup)
      val textareaField = F7StringTextareaField().label("Textarea field example").dependsOn(randomSeed)
      val textareaFieldInInputGroup =
        F7StringTextareaField().label("Textarea field in input group example").dependsOn(randomSeed)
      val textareaFieldInputGroup =
        F7BSFormInputGroup(F7HtmlField.label("Field:"), textareaFieldInInputGroup)
      val checkboxField = F7CheckboxField().label("Checkbox field example").dependsOn(randomSeed)
      val checkboxFieldInInputGroup =
        F7CheckboxField().label("Checkbox field in input group example").dependsOn(randomSeed)
      val checkboxFieldInputGroup =
        F7BSFormInputGroup(F7HtmlField.label("Field:"), checkboxFieldInInputGroup)
      val selectField =
        F7SelectField[String](() => Fruit()).label("Select field example").dependsOn(randomSeed)
      val selectFieldInInputGroup = F7SelectField[String](() => Fruit())
        .label("Select field in input group example")
        .dependsOn(randomSeed)
      val selectFieldInputGroup =
        F7BSFormInputGroup(F7HtmlField.label("Field:"), selectFieldInInputGroup)
      val multiSelectField = F7MultiSelectField[String]()
        .options(Continents())
        .label("Multi-select field example")
        .dependsOn(randomSeed)
      val multiSelectFieldInInputGroup = F7MultiSelectField[String]()
        .options(Continents())
        .label("Multi-select field in input group example")
        .dependsOn(randomSeed)
      val multiSelectFieldInputGroup =
        F7BSFormInputGroup(F7HtmlField.label("Field:"), multiSelectFieldInInputGroup)
      val radioField =
        F7RadioField[String](() => Continents()).label("Radio field example").dependsOn(randomSeed)

      def setValues(seed: Int): Unit =
        val rand = Random(randomSeed.currentValue)
        textField.currentValue = (1 to 8).map(_ => rand.nextPrintableChar()).mkString
        textFieldInInputGroup.currentValue = textField.currentValue
        textareaField.currentValue =
          (1 to 5).map(_ => (1 to 6).map(_ => rand.nextPrintableChar()).mkString).mkString(" ")
        textareaFieldInInputGroup.currentValue = textareaField.currentValue
        checkboxField.currentValue = rand.nextBoolean()
        checkboxFieldInInputGroup.currentValue = checkboxField.currentValue
        selectField.currentValue = Fruit()(rand.nextInt(Fruit().size))
        selectFieldInInputGroup.currentValue = selectField.currentValue
        multiSelectField.currentValue = Continents().filter(_ => rand.nextBoolean()).toSet
        multiSelectFieldInInputGroup.currentValue = multiSelectField.currentValue
        radioField.currentValue = Continents()(rand.nextInt(Continents().size))

      randomSeed.addOnThisFieldChanged: f =>
        setValues(f.currentValue)
        Js.void

      div.border.p_2.rounded:
        new DefaultForm7():

          validateBeforeUserInput()

          setValues(randomSeed.currentValue)

          lazy val rootField: F7Field = F7VerticalField(
            randomSeed,
            F7ContainerField("row")("col" -> textField, "col" -> textFieldInputGroup),
            F7ContainerField("row")("col" -> checkboxField, "col" -> checkboxFieldInputGroup),
            F7ContainerField("row")("col" -> selectField, "col" -> selectFieldInputGroup),
            F7ContainerField("row")("col" -> textareaField, "col" -> textareaFieldInputGroup),
            F7ContainerField("row")("col" -> multiSelectField, "col" -> multiSelectFieldInputGroup),
            F7ContainerField("row")("col" -> radioField),
            F7SubmitButtonField(_ => BSBtn().BtnPrimary.lbl("Submit").btn.d_block.w_100),
          )

          override def postSubmitForm()(using FSContext): Js =
            BSToast2
              .VerySimple(label("Submitted"))(div("You submitted the form").my_1)
              .installAndShow()
        .render()

    closeSnippet()
