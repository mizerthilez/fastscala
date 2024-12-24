package com.fastscala.components.bootstrap5.form7.renderermodifiers

enum CheckboxStyle:
  case Checkbox, Switch

object CheckboxStyle:
  given default: CheckboxStyle = CheckboxStyle.Checkbox
