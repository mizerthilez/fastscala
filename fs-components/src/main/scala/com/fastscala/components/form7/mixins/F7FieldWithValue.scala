package com.fastscala.components.form7.mixins

import com.fastscala.js.Js
import com.fastscala.components.utils.Mutable
import com.fastscala.utils.Lazy

trait F7FieldWithValue[T] extends Mutable:
  def defaultValue: T

  private lazy val currentValueHolder: Lazy[T] = Lazy(_getter())
  private lazy val internalValue: Lazy[T] = Lazy(defaultValue)

  /** This is the value that is currently visible on the client side.
    */
  protected var currentRenderedValue: Option[T] = None

  def currentValue = currentValueHolder()

  /** Changing this value will update on the client side.
    */
  def currentValue_=(v: T) = currentValueHolder() = v

  var _getter: () => T = () => internalValue()

  def get(): T = _getter()

  def getInternalValue(): T = internalValue()

  var _setter: T => Js = v =>
    Js.void: () =>
      internalValue() = v

  def setInternalValue(value: T): this.type = mutate:
    internalValue() = value

  def set(value: T): Js = _setter(value)

  def setter(setter: T => Js): this.type = mutate:
    _setter = setter

  def getter(getter: () => T): this.type = mutate:
    _getter = getter

  def rw(get: => T, set: T => Unit): this.type = mutate:
    _getter = () => get
    _setter = v =>
      set(v)
      Js.void
