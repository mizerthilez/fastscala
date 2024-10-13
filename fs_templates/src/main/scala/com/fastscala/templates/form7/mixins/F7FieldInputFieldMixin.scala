package com.fastscala.templates.form7.fields.text

import scala.xml.Elem

import com.fastscala.templates.form7.F7Field

trait F7FieldInputFieldMixin extends F7Field:
  def processInputElem(input: Elem): Elem = input
