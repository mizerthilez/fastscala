package com.fastscala.components.form7.mixins

import scala.xml.NodeSeq

import com.fastscala.components.form7.F7Field

trait F7FieldWithValidations extends F7Field:
  var _validations = collection.mutable.ListBuffer[() => Option[NodeSeq]]()

  def addValidation(validate: () => Option[NodeSeq]): this.type = mutate:
    _validations += validate

  def addValidation(valid: () => Boolean, error: () => NodeSeq): this.type = mutate:
    _validations += (() => if !valid() then Some(error()) else None)

  def addValidation(valid: this.type => Boolean, error: this.type => NodeSeq): this.type = mutate:
    _validations += (() => if !valid(this) then Some(error(this)) else None)

  override def validate(): Seq[(F7Field, NodeSeq)] = super.validate() ++
    _validations
      .flatMap:
        case validation => validation()
      .map(ns => this -> ns)
