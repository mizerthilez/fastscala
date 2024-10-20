package com.fastscala.templates.form7.mixins

import com.fastscala.core.FSContext
import com.fastscala.js.Js
import com.fastscala.templates.form7.*
import com.fastscala.templates.utils.ElemWithRandomId
import com.fastscala.xml.scala_xml.JS

abstract class StandardF7Field() extends F7FieldWithValidations with ElemWithRandomId:
  val aroundId: String = randomElemId

  def reRender()(using Form7, FSContext, Seq[RenderHint]): Js = JS.replace(aroundId, render())

  def visible: () => Boolean = () => enabled

  override def onEvent(event: F7Event)(using Form7, FSContext, Seq[RenderHint]): Js =
    super.onEvent(event) `&`:
      event match
        case ChangedField(field) if deps.contains(field) =>
          reRender() & summon[Form7].onEvent(ChangedField(this))
        case _ => Js.void

  def disabled: Boolean

  def readOnly: Boolean

  def withFieldRenderHints[T](func: Seq[RenderHint] ?=> T)(using renderHints: Seq[RenderHint]): T =
    import RenderHint.*
    func(
      using List(DisableFieldsHint).filter(_ => disabled) ++
        List(ReadOnlyFieldsHint).filter(_ => readOnly) ++
        renderHints
    )
