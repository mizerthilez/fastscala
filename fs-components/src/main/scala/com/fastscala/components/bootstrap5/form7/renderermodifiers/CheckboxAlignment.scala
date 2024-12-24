package com.fastscala.components.bootstrap5.form7.renderermodifiers

enum CheckboxAlignment:
  case Vertical, Horizontal

object CheckboxAlignment:
  given default: CheckboxAlignment = CheckboxAlignment.Vertical
