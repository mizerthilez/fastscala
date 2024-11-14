package com.fastscala.demo.docs.forms

import com.fastscala.core.FSContext
import com.fastscala.demo.docs.MultipleCodeExamples2Page
import com.fastscala.js.Js
import com.fastscala.templates.bootstrap5.form7.renderermodifiers.*
import com.fastscala.templates.bootstrap5.modals.BSModal5
import com.fastscala.templates.bootstrap5.utils.BSBtn
import com.fastscala.templates.form7.fields.*
import com.fastscala.templates.form7.fields.layout.{ F7ContainerField, F7VerticalField }
import com.fastscala.templates.form7.{ DefaultForm7, F7Field }
import com.fastscala.xml.scala_xml.FSScalaXmlEnv.given

class CheckboxInputFieldsPage extends MultipleCodeExamples2Page:
  def pageTitle: String = "Form 7 Checkbox Input Fields"

  import DefaultFSDemoBSForm7Renderers.given
  import com.fastscala.templates.bootstrap5.helpers.BSHelpers.{ given, * }

  def renderContentsWithSnippets()(using FSContext): Unit =
    renderSnippet("Checkbox input-based fields"):

      val checkboxAsSwitchAndOpposite = FSDemoBSForm7Renderers(
        using checkboxAlignment = CheckboxAlignment.default,
        checkboxStyle = CheckboxStyle.Switch,
        checkboxSide = CheckboxSide.Opposite,
      ).given_CheckboxF7FieldRenderer

      val checkboxInline =
        FSDemoBSForm7Renderers(using checkboxAlignment =
          CheckboxAlignment.Horizontal
        ).given_CheckboxF7FieldRenderer

      val termsAndConditionsField = F7CheckboxField().label("Accept Terms and Conditions")
      val privacyPolicyField = F7CheckboxField().label("Accept Privacy Policy").setInternalValue(true)

      val hasACar = F7CheckboxOptField().label("Has car").setInternalValue(None)

      val subscribe2NewsletterField = F7CheckboxField(using checkboxInline).label("Subscribe to Newsletter")
      val subscribe2DiscountsField =
        F7CheckboxField(using checkboxInline).label("Subscribe to Discounts").setInternalValue(true)

      val marketingEmailField = F7CheckboxField(using checkboxAsSwitchAndOpposite).label("Email")
      val marketingSMSField =
        F7CheckboxField(using checkboxAsSwitchAndOpposite).label("SMS").setInternalValue(true)
      val marketingPhoneField = F7CheckboxField(using checkboxAsSwitchAndOpposite).label("Phone")

      div:
        new DefaultForm7:
          lazy val rootField: F7Field = F7VerticalField(
            F7HtmlField(fs_5.mb_3.border_bottom("Checkboxes")),
            termsAndConditionsField,
            privacyPolicyField,
            //
            F7HtmlField(fs_5.mb_3.border_bottom("Tri-state checkboxes (supports indeterminate)")),
            F7ContainerField("row")(
              "col" -> hasACar,
              "col" -> F7HtmlField(
                span("Checkbox state: " + hasACar.currentValue.map("set to " + _).getOrElse("not set"))
              ).dependsOn(hasACar),
            ),
            //
            F7HtmlField(fs_5.mb_3.border_bottom("Checkboxes inline")),
            subscribe2NewsletterField,
            subscribe2DiscountsField,
            //
            F7HtmlField(fs_5.mb_3.border_bottom("Checkboxes as switches and opposite side")),
            marketingEmailField,
            marketingSMSField,
            marketingPhoneField,
            F7SubmitButtonField(_ => BSBtn().BtnPrimary.lbl("Submit").btn.d_block.w_100),
          )

          override def postSubmitForm()(using FSContext): Js =
            BSModal5.verySimple("Submitted!", "Ok"): modal =>
              _ =>
                <h6>Your data:</h6> ++
                  <ul>
                    <li><b>Accept Terms and Conditions:</b> {termsAndConditionsField.getInternalValue()}</li>
                    <li><b>Accept Privacy Policy:</b> {privacyPolicyField.getInternalValue()}</li>
                    <li><b>Has car:</b> {hasACar.getInternalValue()}</li>
                    <li><b>Subscribe to Newsletter:</b> {subscribe2NewsletterField.getInternalValue()}</li>
                    <li><b>Subscribe to Discounts:</b> {subscribe2DiscountsField.getInternalValue()}</li>
                    <li><b>Email:</b> {marketingEmailField.getInternalValue()}</li>
                    <li><b>SMS:</b> {marketingSMSField.getInternalValue()}</li>
                    <li><b>Phone:</b> {marketingPhoneField.getInternalValue()}</li>
                  </ul>
        .render()

    closeSnippet()
