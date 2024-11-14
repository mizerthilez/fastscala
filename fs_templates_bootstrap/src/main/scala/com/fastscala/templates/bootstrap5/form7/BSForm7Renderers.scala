package com.fastscala.templates.bootstrap5.form7

import scala.xml.{ Elem, NodeSeq }

import com.fastscala.templates.bootstrap5.form7.renderermodifiers.*
import com.fastscala.templates.form7.*
import com.fastscala.templates.form7.fields.*
import com.fastscala.templates.form7.renderers.*

abstract class BSForm7Renderers(using CheckboxAlignment, CheckboxStyle, CheckboxSide):
  import com.fastscala.templates.bootstrap5.helpers.BSHelpers.{ given, * }

  def defaultRequiredFieldLabel: String

  given BSForm7Renderers = this

  given TextF7FieldRenderer = new TextF7FieldRenderer with BSStandardF7FieldRendererImpl:
    def defaultRequiredFieldLabel: String = BSForm7Renderers.this.defaultRequiredFieldLabel

  given TextareaF7FieldRenderer = new TextareaF7FieldRenderer with BSStandardF7FieldRendererImpl:
    def defaultRequiredFieldLabel: String = BSForm7Renderers.this.defaultRequiredFieldLabel

  given SelectF7FieldRenderer = new SelectF7FieldRenderer with BSStandardF7FieldRendererImpl:
    def defaultRequiredFieldLabel: String = BSForm7Renderers.this.defaultRequiredFieldLabel

    def renderOption(selected: Boolean, value: String, label: NodeSeq)(using Seq[RenderHint]): Elem =
      <option selected={if selected then "true" else null} value={value}>{label}</option>

  given MultiSelectF7FieldRenderer = new MultiSelectF7FieldRenderer with BSStandardF7FieldRendererImpl:
    def defaultRequiredFieldLabel: String = BSForm7Renderers.this.defaultRequiredFieldLabel

    def renderOption(selected: Boolean, value: String, label: NodeSeq)(using Seq[RenderHint]): Elem =
      <option selected={if selected then "true" else null} value={value}>{label}</option>

  given CheckboxF7FieldRenderer = new BSCheckboxF7FieldRendererImpl:
    def defaultRequiredFieldLabel: String = BSForm7Renderers.this.defaultRequiredFieldLabel

  given RadioF7FieldRenderer = new BSRadioF7FieldRendererImpl:
    def defaultRequiredFieldLabel: String = BSForm7Renderers.this.defaultRequiredFieldLabel

  given ButtonF7FieldRenderer with
    def render(field: F7SubmitButtonField[?])(btn: Elem)(using hints: Seq[RenderHint]): Elem =
      import RenderHint.*
      if !field.enabled then div.withId(field.aroundId).withStyle(";display:none;")
      else
        div.mb_3
          .addClass("d-grid gap-2 d-md-flex justify-content-md-end")
          .withId(field.aroundId):
            btn
              .withAttrIf(hints.contains(DisableFieldsHint), "disabled" -> "true")

  given F7FormRenderer with
    def render(form: Elem): Elem = form.mb_5.w_100.addClass("form")
