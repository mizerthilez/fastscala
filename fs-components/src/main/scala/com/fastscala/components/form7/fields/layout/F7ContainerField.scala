package com.fastscala.components.form7.fields.layout

import com.fastscala.components.form7.*

class F7ContainerField(val aroundClass: String)(childrenFields: (String, F7Field)*)
    extends F7ContainerFieldBase:
  val children: Seq[(Option[String], F7Field)] = childrenFields.map((clas, field) => (Some(clas), field))

object F7ContainerField:
  def apply(aroundClass: String)(children: (String, F7Field)*) = new F7ContainerField(aroundClass)(children*)
