package com.fastscala.demo.docs.forms

import com.fastscala.components.bootstrap5.form7.BSForm7Renderers
import com.fastscala.components.bootstrap5.form7.renderermodifiers.*

object DefaultFSDemoBSForm7Renderers extends FSDemoBSForm7Renderers

class FSDemoBSForm7Renderers(using
  checkboxAlignment: CheckboxAlignment = CheckboxAlignment.default,
  checkboxStyle: CheckboxStyle = CheckboxStyle.default,
  checkboxSide: CheckboxSide = CheckboxSide.default,
) extends BSForm7Renderers:
  def defaultRequiredFieldLabel: String = "Required field"
