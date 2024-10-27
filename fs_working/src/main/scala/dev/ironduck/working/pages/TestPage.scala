package dev.ironduck.working.pages

import scala.xml.NodeSeq

import com.fastscala.js.Js
import com.fastscala.core.FSContext
import com.fastscala.templates.bootstrap5.utils.BSBtn
import com.fastscala.templates.form7.{ DefaultForm7, F7Field, Form7, RenderHint }
import com.fastscala.templates.form7.fields.*
import com.fastscala.templates.form7.fields.multiselect.*
import com.fastscala.templates.form7.fields.radio.*
import com.fastscala.templates.form7.fields.select.*
import com.fastscala.templates.form7.fields.text.*
import com.fastscala.templates.form7.fields.layout.*
import com.fastscala.xml.scala_xml.FSScalaXmlEnv.given

import dev.ironduck.working.PageWithTopTitle
import dev.ironduck.working.forms.DefaultFSDemoBSForm7Renderers

class TestPage extends PageWithTopTitle:
  def pageTitle: String = "Test Example"

  def renderStandardPageContents()(using FSContext): NodeSeq =
    import DefaultFSDemoBSForm7Renderers.given
    import com.fastscala.templates.bootstrap5.helpers.BSHelpers.{ given, * }

    div.d_grid.mx_auto.col_8.my_5.apply:
      BSBtn().BtnPrimary
        .lbl("Open Modal")
        .ajax:
          implicit fsc =>
            val stringField = F7StringField()
            val stringOptField = F7StringOptField()
            val intField = F7IntField()

            val textareaField = F7StringTextareaField()
            val textareaOptField = F7StringOptTextareaField()

            val selectField = F7SelectField(Seq("A", "B"))
            val selectOptField = F7SelectOptField()

            val multiSelectField = F7MultiSelectField()

            val radioField = F7RadioField(Seq("A", "B"))
            val radioOptField = F7RadioOptField()

            val verticalField = F7VerticalField()
            val containerField = F7ContainerField("")()

            val checkField = F7CheckboxField()
            val checkOptField = F7CheckboxOptField()

            val htmlField = F7HtmlField(<div/>)
            val htmlsurroundField = F7HtmlSurroundField(_ => <div/>)(htmlField)
            val btnField = F7SaveButtonField(_ => <div/>)

            given form: Form7 = new DefaultForm7:
              lazy val rootField: F7Field = selectField
            given Seq[RenderHint] = form.formRenderHints()

            println("---------stringField-----------")
            stringField.updateFieldStatus()
            println("---------stringOptField-----------")
            stringOptField.updateFieldStatus()
            println("---------intField-----------")
            intField.updateFieldStatus()

            println("---------textareaField-----------")
            textareaField.updateFieldStatus()
            println("---------textareaOptField-----------")
            textareaOptField.updateFieldStatus()

            println("---------selectField-----------")
            selectField.updateFieldStatus()
            println("---------selectOptField-----------")
            selectOptField.updateFieldStatus()

            println("---------multiSelectField-----------")
            multiSelectField.updateFieldStatus()

            println("---------radioField-----------")
            radioField.updateFieldStatus()
            println("---------radioOptField-----------")
            radioOptField.updateFieldStatus()

            println("---------verticalField-----------")
            verticalField.updateFieldStatus()
            println("---------containerField-----------")
            containerField.updateFieldStatus()

            println("---------checkField-----------")
            checkField.updateFieldStatus()
            println("---------checkOptField-----------")
            checkOptField.updateFieldStatus()

            println("---------htmlField-----------")
            htmlField.updateFieldStatus()
            println("---------htmlsurroundField-----------")
            htmlsurroundField.updateFieldStatus()
            println("---------btnField-----------")
            btnField.updateFieldStatus()

            Js.alert("OK")
        .btn
