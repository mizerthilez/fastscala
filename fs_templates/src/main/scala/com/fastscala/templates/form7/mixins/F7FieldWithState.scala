package com.fastscala.templates.form7.mixins

import com.fastscala.templates.form7.F7FieldState, F7FieldState.*
import com.fastscala.templates.utils.Mutable

trait F7FieldWithState extends Mutable:
  var _state: F7FieldState = AwaitingInput

  def state: F7FieldState = _state

  def isAwaitingInput = state == AwaitingInput

  def setAwaitingInput() = mutate:
    _state = AwaitingInput

  def isFilled = state == Filled

  def setFilled() = mutate:
    _state = Filled

  def state(state: F7FieldState): this.type = mutate:
    _state = state
