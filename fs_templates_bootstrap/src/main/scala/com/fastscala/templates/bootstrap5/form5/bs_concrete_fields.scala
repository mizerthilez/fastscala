package com.fastscala.templates.bootstrap5.form5

import com.fastscala.templates.form5.fields._


object BSHorizontalField:

  import com.fastscala.templates.bootstrap5.classes.BSHelpers.{given, *}

  def apply(
             enabled: () => Boolean = () => true
             , deps: Set[FormField] = Set()
           )(children: FormField*) =
    new F5HorizontalField(enabled, deps)(children.map[(String, FormField)](f => col.getClassAttr -> f): _*)
