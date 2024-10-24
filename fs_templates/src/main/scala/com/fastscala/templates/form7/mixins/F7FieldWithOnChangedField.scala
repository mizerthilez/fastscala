package com.fastscala.templates.form7.mixins

import com.fastscala.core.FSContext
import com.fastscala.js.Js
import com.fastscala.templates.form7.*

trait F7OnChangedFieldHandler:
  def onChanged(field: F7Field)(using Form7, FSContext, Seq[RenderHint]): Js

trait F7FieldWithOnChangedField extends F7Field:
  var _onChangedField = collection.mutable.ListBuffer[F7OnChangedFieldHandler]()

  def onChangedField: Seq[F7OnChangedFieldHandler] = _onChangedField.toSeq

  def addOnChangedField(onchange: F7OnChangedFieldHandler): this.type = mutate:
    _onChangedField += onchange

  override def onEvent(event: F7Event)(using Form7, FSContext, Seq[RenderHint]): Js =
    super.onEvent(event) `&`:
      event match
        case ChangedField(field) =>
          _onChangedField.map(_.onChanged(field)).reduceOption(_ & _).getOrElse(Js.void)
        case _ => Js.void
