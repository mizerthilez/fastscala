package com.fastscala.components.bootstrap5.form7.renderermodifiers

enum CheckboxSide:
  case Normal, Opposite

object CheckboxSide:
  given default: CheckboxSide = CheckboxSide.Normal
