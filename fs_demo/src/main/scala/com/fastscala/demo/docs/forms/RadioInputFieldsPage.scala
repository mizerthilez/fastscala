package com.fastscala.demo.docs.forms

import com.fastscala.core.FSContext
import com.fastscala.demo.docs.MultipleCodeExamples2Page
import com.fastscala.js.Js
import com.fastscala.templates.bootstrap5.form7.renderermodifiers.*
import com.fastscala.templates.bootstrap5.modals.BSModal5
import com.fastscala.templates.bootstrap5.utils.BSBtn
import com.fastscala.templates.form7.fields.*
import com.fastscala.templates.form7.fields.layout.F7VerticalField
import com.fastscala.templates.form7.fields.radio.F7RadioField
import com.fastscala.templates.form7.{ DefaultForm7, F7Field }
import com.fastscala.xml.scala_xml.FSScalaXmlEnv.given

class RadioInputFieldsPage extends MultipleCodeExamples2Page:
  def pageTitle: String = "Form 7 Radio Input Fields"

  import DefaultFSDemoBSForm7Renderers.given
  import com.fastscala.templates.bootstrap5.helpers.BSHelpers.{ given, * }

  def renderContentsWithSnippets()(using FSContext): Unit =
    renderSnippet("Radio input-based fields"):

      val radioAsSwitchAndOpposite = FSDemoBSForm7Renderers(
        using checkboxAlignment = CheckboxAlignment.default,
        checkboxStyle = CheckboxStyle.Switch,
        checkboxSide = CheckboxSide.Opposite,
      ).given_RadioF7FieldRenderer

      val radioInline =
        FSDemoBSForm7Renderers(using checkboxAlignment =
          CheckboxAlignment.Horizontal
        ).given_RadioF7FieldRenderer

      val registrationType = Seq("Teacher", "Student", "Parent")
      val registrationTypeField = F7RadioField(registrationType).label("Registration type")

      val phoneType = Seq("Android", "iOS", "Other")
      val phoneTypeField = F7RadioField(phoneType)(using radioAsSwitchAndOpposite).label("Phone type")

      val marketingChannelsType = Seq("Android", "iOS", "Other")
      val marketingChannelsTypeField =
        F7RadioField(marketingChannelsType)(using radioInline).label("Preferred marketing channel")

      div:
        new DefaultForm7:
          lazy val rootField: F7Field = F7VerticalField(
            F7HtmlField(fs_5.mb_3.border_bottom.apply("Radio input")),
            registrationTypeField,
            //
            F7HtmlField(fs_5.mb_3.border_bottom.apply("Radio buttons as switches on the opposite side")),
            phoneTypeField,
            //
            F7HtmlField(fs_5.mb_3.border_bottom.apply("Radio buttons inline")),
            marketingChannelsTypeField,
            F7SaveButtonField(_ => BSBtn().BtnPrimary.lbl("Submit").btn.d_block.w_100),
          )

          override def postSubmitForm()(using FSContext): Js =
            BSModal5.verySimple("Submitted!", "Ok"): modal =>
              _ =>
                <h6>Your data:</h6> ++
                  <ul>
                  <li><b>Registration type:</b> {registrationTypeField.getInternalValue()}</li>
                  <li><b>Phone type:</b> {phoneTypeField.getInternalValue()}</li>
                  <li><b>Preferred marketing channel:</b> {marketingChannelsTypeField.getInternalValue()}</li>
                </ul>
        .render()

    closeSnippet()
