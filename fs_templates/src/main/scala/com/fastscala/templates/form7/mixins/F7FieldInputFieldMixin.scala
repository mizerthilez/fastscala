package com.fastscala.templates.form7.mixins

import scala.xml.Elem

import com.fastscala.templates.utils.Mutable

trait F7FieldInputFieldMixin extends Mutable:
  def processInputElem(input: Elem): Elem = input
