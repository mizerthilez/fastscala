package com.fastscala.demo.docs.forms

import com.fastscala.core.FSContext
import com.fastscala.demo.docs.MultipleCodeExamples2Page
import com.fastscala.demo.docs.data.{ Continents, Fruit }
import com.fastscala.js.Js
import com.fastscala.templates.bootstrap5.toast.BSToast2
import com.fastscala.templates.bootstrap5.utils.BSBtn
import com.fastscala.templates.form7.fields.*
import com.fastscala.templates.form7.fields.layout.{ F7ContainerField, F7VerticalField }
import com.fastscala.templates.form7.fields.multiselect.F7MultiSelectField
import com.fastscala.templates.form7.fields.radio.F7RadioField
import com.fastscala.templates.form7.fields.select.F7SelectField
import com.fastscala.templates.form7.fields.text.*
import com.fastscala.templates.form7.{ DefaultForm7, F7Field }
import com.fastscala.xml.scala_xml.FSScalaXmlEnv.given

import scala.util.Random
import scala.xml.Elem

class FieldStatesPage extends MultipleCodeExamples2Page:
  def pageTitle: String = "Form 7 - Update client values after changes on server side"

  import DefaultFSDemoBSForm7Renderers.given
  import com.fastscala.templates.bootstrap5.helpers.BSHelpers.{ given, * }

  def renderContentsWithSnippets()(using FSContext): Unit =

    renderSnippet("Disabled example"):

      def render(startsDisabled: Boolean): Elem =

        val randomSeed: F7IntField = F7IntField().step(1).label("Random seed").doSyncToServerOnChange
        val fieldsShouldBeDisabled =
          F7CheckboxField().label("Disable all fields").setInternalValue(startsDisabled)

        val textField = F7StringField()
          .label("Text field example")
          .dependsOn(randomSeed, fieldsShouldBeDisabled)
          .disabled(() => fieldsShouldBeDisabled.currentValue)
        val textareaField = F7StringTextareaField()
          .label("Textarea field example")
          .dependsOn(randomSeed, fieldsShouldBeDisabled)
          .disabled(() => fieldsShouldBeDisabled.currentValue)
        val checkboxField = F7CheckboxField()
          .label("Checkbox field example")
          .dependsOn(randomSeed, fieldsShouldBeDisabled)
          .disabled(() => fieldsShouldBeDisabled.currentValue)
        val selectField = F7SelectField(Fruit())
          .label("Select field example")
          .dependsOn(randomSeed, fieldsShouldBeDisabled)
          .disabled(() => fieldsShouldBeDisabled.currentValue)
        val multiSelectField = F7MultiSelectField()
          .options(Continents())
          .label("Multi-select field example")
          .dependsOn(randomSeed, fieldsShouldBeDisabled)
          .disabled(() => fieldsShouldBeDisabled.currentValue)
        val radioField = F7RadioField(Continents())
          .label("Radio field example")
          .dependsOn(randomSeed, fieldsShouldBeDisabled)
          .disabled(() => fieldsShouldBeDisabled.currentValue)

        def setValues(seed: Int): Unit =
          val rand = Random(randomSeed.currentValue)
          textField.currentValue = (1 to 8).map(_ => rand.nextPrintableChar()).mkString
          textareaField.currentValue =
            (1 to 5).map(_ => (1 to 6).map(_ => rand.nextPrintableChar()).mkString).mkString(" ")
          checkboxField.currentValue = rand.nextBoolean()
          selectField.currentValue = Fruit()(rand.nextInt(Fruit().size))
          multiSelectField.currentValue = Continents().filter(_ => rand.nextBoolean()).toSet
          radioField.currentValue = Continents()(rand.nextInt(Continents().size))

        randomSeed.addOnThisFieldChanged: f =>
          setValues(f.currentValue)
          Js.void

        div.border.p_2
          .rounded:
            h5(s"Starts with disabled=$startsDisabled") ++
              new DefaultForm7():

                validateBeforeUserInput()

                setValues(randomSeed.currentValue)

                lazy val rootField: F7Field = F7VerticalField(
                  randomSeed,
                  fieldsShouldBeDisabled,
                  F7ContainerField("row")(
                    "col" -> textField,
                    "col" -> checkboxField,
                    "col" -> selectField,
                  ),
                  F7ContainerField("row")(
                    "col" -> textareaField,
                    "col" -> multiSelectField,
                    "col" -> radioField,
                  ),
                  F7SaveButtonField(_ => BSBtn().BtnPrimary.lbl("Submit").btn.d_block.w_100),
                )

                override def postSubmitForm()(using FSContext): Js =
                  BSToast2
                    .VerySimple(label.apply("Submitted"))(span.apply("You submitted the form"))
                    .installAndShow()
              .render()
          .mb_3

      render(true) ++ render(false)

    renderSnippet("Read/Write example"):

      def render(startsReadOnly: Boolean): Elem =

        val randomSeed: F7IntField = F7IntField().step(1).label("Random seed").doSyncToServerOnChange
        val fieldsShouldBeReadOnly =
          F7CheckboxField().label("Make all fields read-only").setInternalValue(startsReadOnly)

        val textField = F7StringField()
          .label("Text field example")
          .dependsOn(randomSeed, fieldsShouldBeReadOnly)
          .readOnly(() => fieldsShouldBeReadOnly.currentValue)
        val textareaField = F7StringTextareaField()
          .label("Textarea field example")
          .dependsOn(randomSeed, fieldsShouldBeReadOnly)
          .readOnly(() => fieldsShouldBeReadOnly.currentValue)

        def setValues(seed: Int): Unit =
          val rand = Random(randomSeed.currentValue)
          textField.currentValue = (1 to 8).map(_ => rand.nextPrintableChar()).mkString
          textareaField.currentValue =
            (1 to 5).map(_ => (1 to 6).map(_ => rand.nextPrintableChar()).mkString).mkString(" ")

        randomSeed.addOnThisFieldChanged: f =>
          setValues(f.currentValue)
          Js.void

        div.border.p_2
          .rounded:
            h5(s"Starts with readOnly=$startsReadOnly") ++
              new DefaultForm7():

                validateBeforeUserInput()

                setValues(randomSeed.currentValue)

                lazy val rootField: F7Field = F7VerticalField(
                  randomSeed,
                  fieldsShouldBeReadOnly,
                  F7ContainerField("row")(
                    "col" -> textField,
                    "col" -> textareaField,
                  ),
                  F7SaveButtonField(_ => BSBtn().BtnPrimary.lbl("Submit").btn.d_block.w_100),
                )

                override def postSubmitForm()(using FSContext): Js =
                  BSToast2
                    .VerySimple(label.apply("Submitted"))(span.apply("You submitted the form"))
                    .installAndShow()
              .render()
          .mb_3

      render(true) ++ render(false)

    closeSnippet()
