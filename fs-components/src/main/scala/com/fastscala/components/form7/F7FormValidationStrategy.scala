package com.fastscala.components.form7

enum F7FormValidationStrategy:
  case ValidateBeforeUserInput
  case ValidateEachFieldAfterUserInput
  case ValidateOnAttemptSubmitOnly
