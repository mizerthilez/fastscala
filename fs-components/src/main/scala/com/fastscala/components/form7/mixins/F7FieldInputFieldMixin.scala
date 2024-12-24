package com.fastscala.components.form7.mixins

import scala.xml.Elem

import com.fastscala.components.utils.Mutable

trait F7FieldInputFieldMixin extends Mutable:
  def processInputElem(input: Elem): Elem = input
