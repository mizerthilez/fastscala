package com.fastscala.templates.form7

enum RenderHint:
  case FailedSaveStateHint, ShowValidationsHint, DisableFieldsHint, ReadOnlyFieldsHint, SaveOnEnterHint

trait Form7WithSaveOnEnterHint extends Form7:
  override def formRenderHints(): Seq[RenderHint] = super.formRenderHints() :+ RenderHint.SaveOnEnterHint
