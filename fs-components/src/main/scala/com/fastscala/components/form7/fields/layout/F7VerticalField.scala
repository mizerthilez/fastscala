package com.fastscala.components.form7.fields.layout

import com.fastscala.components.form7.*

class F7VerticalField(childrenFields: F7Field*) extends F7ContainerFieldBase:
  def aroundClass: String = ""

  def children: Seq[(Option[String], F7Field)] = childrenFields.map(None -> _)

object F7VerticalField:
  def apply(children: F7Field*) = new F7VerticalField(children*)
