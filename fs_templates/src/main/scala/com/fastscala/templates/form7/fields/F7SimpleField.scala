package com.fastscala.templates.form7.fields

import com.fastscala.templates.form7.F7Field

trait F7SimpleField extends F7Field:
  def disabled: Boolean = false

  def readOnly: Boolean = false

  def deps: Set[F7Field] = Set()

  def enabled: Boolean = true

  def fieldAndChildrenMatchingPredicate(pf: PartialFunction[F7Field, Boolean]): List[F7Field] =
    if pf.applyOrElse(this, _ => false) then List(this) else Nil
