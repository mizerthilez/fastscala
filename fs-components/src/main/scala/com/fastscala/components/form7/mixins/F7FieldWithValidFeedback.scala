package com.fastscala.components.form7.mixins

import scala.xml.Elem

import com.fastscala.components.utils.Mutable

trait F7FieldWithValidFeedback extends Mutable:
  var _validFeedback: () => Option[Elem] = () => None

  def validFeedback: Option[Elem] = _validFeedback()

  def validFeedback(v: Option[Elem]): this.type = mutate:
    _validFeedback = () => v

  def validFeedback(v: Elem): this.type = mutate:
    _validFeedback = () => Some(v)

  def validFeedback(v: String): this.type = mutate:
    _validFeedback = () => Some(<div>{v}</div>)

  def validFeedbackStrF(f: () => String): this.type = mutate:
    _validFeedback = () => Some(<div>{f()}</div>)
