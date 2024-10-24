package com.fastscala.templates.bootstrap5.form7.renderermodifiers

enum CheckboxSide:
  case Normal, Opposite

object CheckboxSide:
  given default: CheckboxSide = CheckboxSide.Normal
