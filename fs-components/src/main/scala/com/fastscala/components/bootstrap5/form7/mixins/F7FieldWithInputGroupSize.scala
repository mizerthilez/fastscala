package com.fastscala.components.bootstrap5.form7.mixins

import com.fastscala.components.utils.Mutable

enum InputGroupSize:
  case Small, Default, Large

object InputGroupSize:
  extension (v: InputGroupSize)
    def `class` = v match
      case Small => "input-group-sm"
      case Default => ""
      case Large => "input-group-lg"

trait F7FieldWithInputGroupSize extends Mutable:
  var _inputGroupSize: () => InputGroupSize = () => InputGroupSize.Default

  def inputGroupSize: InputGroupSize = _inputGroupSize()

  def inputGroupSizeSmall(): this.type = inputGroupSize(InputGroupSize.Small)

  def inputGroupSizeLarge(): this.type = inputGroupSize(InputGroupSize.Large)

  def inputGroupSize(v: InputGroupSize): this.type = mutate:
    _inputGroupSize = () => v
