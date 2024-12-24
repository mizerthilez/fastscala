package com.fastscala.components.form7

enum RenderHint:
  case FailedSaveStateHint, ShowValidationsHint, DisableFieldsHint, SaveOnEnterHint

trait Form7WithSaveOnEnterHint extends Form7:
  override def formRenderHints(): Seq[RenderHint] = super.formRenderHints() :+ RenderHint.SaveOnEnterHint
