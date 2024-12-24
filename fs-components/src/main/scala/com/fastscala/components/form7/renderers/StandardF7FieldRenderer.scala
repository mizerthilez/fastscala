package com.fastscala.components.form7.renderers

import scala.xml.Elem

import com.fastscala.components.form7.mixins.StandardF7Field

trait StandardF7FieldRenderer:
  def defaultRequiredFieldLabel: String

  extension (field: StandardF7Field)
    def labelId = field.elemId + "-label"

    def invalidFeedbackId = field.elemId + "-invalid-feedback"

    def validFeedbackId = field.elemId + "-valid-feedback"

    def helpId = field.elemId + "-help"

  def renderDisabled(field: StandardF7Field): Elem = <div style="display:none;" id={field.aroundId}></div>
