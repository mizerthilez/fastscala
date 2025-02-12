package com.fastscala.components.form7.mixins

import scala.util.{ Try, Failure, Success }

import com.fastscala.core.FSContext
import com.fastscala.js.Js
import com.fastscala.components.form7.{ F7Field, Form7, RenderHint }
import com.fastscala.components.utils.Mutable

trait F7FieldWithOptions[T] extends F7Field with Mutable:
  var _options: () => Seq[T] = () => Nil

  def options: Seq[T] = _options()

  def options(v: Seq[T]): this.type = mutate:
    _options = () => v

  def options(f: () => Seq[T]): this.type = mutate:
    _options = f

  protected var currentRenderedOptions = Option.empty[(Seq[T], Map[String, T], Map[T, String])]

  override def updateFieldWithoutReRendering()(using Form7, FSContext, Seq[RenderHint]): Try[Js] =
    super
      .updateFieldWithoutReRendering()
      .flatMap: superJs =>
        val currentOptions = options
        // Check if options changed:
        if !currentRenderedOptions.exists(_._1 == currentOptions) then
          Failure(new Exception("Options to render changed, rerendering."))
        else Success(superJs)
