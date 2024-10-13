package com.fastscala.templates.form7.mixins

import scala.xml.Elem

import com.fastscala.templates.form7.fields.text.F7FieldInputFieldMixin

trait F7FieldWithLabel extends F7FieldInputFieldMixin:
  var _label: () => Option[Elem] = () => None

  def label = _label()

  def label(v: Option[Elem]): this.type = mutate {
    _label = () => v
  }

  def label(v: Elem): this.type = mutate {
    _label = () => Some(v)
  }

  def label(v: String): this.type = mutate:
    _label = () => Some(<label>{v}</label>)

  def labelStrF(f: () => String): this.type = mutate:
    _label = () => Some(<label>{f()}</label>)
