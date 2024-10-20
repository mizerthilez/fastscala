package com.fastscala.templates.form7.formmixins

import com.fastscala.templates.form7.F7FormValidationStrategy, F7FormValidationStrategy.*
import com.fastscala.templates.utils.Mutable

trait F7FormWithValidationStrategy extends Mutable:
  var _validationStrategy: () => F7FormValidationStrategy = () => ValidateOnAttemptSubmitOnly

  def validationStrategy: F7FormValidationStrategy = _validationStrategy()

  def validateBeforeUserInput(): this.type = mutate:
    _validationStrategy = () => ValidateBeforeUserInput

  def validateEachFieldAfterUserInput(): this.type = mutate:
    _validationStrategy = () => ValidateEachFieldAfterUserInput

  def validateOnAttemptSubmitOnly(): this.type = mutate:
    _validationStrategy = () => ValidateOnAttemptSubmitOnly

  def validationStrategy(f: () => F7FormValidationStrategy): this.type = mutate:
    _validationStrategy = f
