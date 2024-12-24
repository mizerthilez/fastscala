package com.fastscala.components.bootstrap5.form7.layout

import com.fastscala.core.FSContext
import com.fastscala.js.Js
import com.fastscala.components.bootstrap5.form7.mixins.F7FieldWithInputGroupSize
import com.fastscala.components.form7.*
import com.fastscala.components.form7.fields.F7HtmlField
import com.fastscala.components.form7.fields.layout.F7ContainerFieldBase
import com.fastscala.xml.scala_xml.JS

import scala.xml.{ Elem, Node, NodeSeq }

class F7BSFormInputGroup(groupChildren: F7Field*) extends F7ContainerFieldBase with F7FieldWithInputGroupSize:
  import com.fastscala.components.bootstrap5.helpers.BSHelpers.{ given, * }

  def aroundClass: String = ""

  def children: Seq[(Option[String], F7Field)] = groupChildren.map(None -> _)

  var showingValidation = false

  override def render()(using Form7, FSContext, Seq[RenderHint]): Elem =
    currentlyEnabled = enabled
    if !currentlyEnabled then <div style="display:none;" id={aroundId}></div>
    else
      withFieldRenderHints: hints ?=>
        val showErrors = shouldShowValidation && groupChildren.exists(_.validate().nonEmpty)
        showingValidation = showErrors

        val renderedChildren: Seq[(F7Field, Elem, Seq[Node], Seq[Node])] =
          groupChildren.map: field =>
            val rendered = field.render()

            val inputIdx: Option[Int] = Some(
              rendered.child.indexWhere:
                case elem: Elem
                     if elem.label == "input" || elem.label == "textarea" || elem.label == "select" =>
                  true
                case _ => false
            ).filter(_ != -1)

            val (beforeInput: Seq[Node], inputAndAfter: Seq[Node]) = inputIdx
              .map: inputElemIdx =>
                rendered.child.splitAt(inputElemIdx)
              .getOrElse((Nil, Nil))

            (
              field,
              rendered,
              beforeInput ++ inputAndAfter.collect:
                case elem: Elem if elem.getClassAttr.contains("form-check-label") => elem
              ,
              inputAndAfter
                .drop(1)
                .filter:
                  case elem: Elem if elem.getClassAttr.contains("form-check-label") => false
                  case _ => true,
            )

        div
          .withId(aroundId)
          .mb_3:
            renderedChildren.map(_._3).flatten.map(adaptNodes) ++
              input_group
                .withId(elemId)
                .withClassIf(showErrors, is_invalid.getClassAttr)
                .withClass(inputGroupSize.`class`)
                .apply(
                  renderedChildren
                    .map:
                      case (field: F7HtmlField, rendered, _, _) =>
                        rendered.child.map:
                          case elem: Elem if elem.label == "span" || elem.label == "label" =>
                            elem.input_group_text
                          case elem => elem
                      case (field, rendered, _, _) =>
                        rendered.child
                          .collect:
                            case elem: Elem if elem.label == "textarea" => Seq(elem)
                            case elem: Elem if elem.label == "select" => Seq(elem)
                            case elem: Elem
                                 if elem.label == "input" && Set("radio", "checkbox")
                                   .contains(elem.attr("type").getOrElse("")) =>
                              Seq(input_group_text(elem.mt_0))
                            case elem: Elem if elem.label == "input" => Seq(elem)
                            case _ => Seq()
                          .flatten
                    .flatten*
                ) ++
              renderedChildren.map(_._4).flatten.map(adaptNodes): NodeSeq

  def adaptNodes(node: Node): Node = node match
    case elem: Elem if elem.getClassAttr.contains("form-check-label") =>
      elem.removeClass("form-check-label").addClass("form-label")
    case other => other

  override def postValidation(errors: Seq[(F7Field, NodeSeq)])(using Form7, FSContext): Js =
    updateValidation()

  def updateValidation()(using Form7): Js =
    if shouldShowValidation then
      val hasErrors = groupChildren.exists(_.validate().nonEmpty)
      if hasErrors then
        showingValidation = true
        showValidation()
      else
        showingValidation = false
        hideValidation()
    else if showingValidation then
      showingValidation = false
      hideValidation()
    else Js.void

  def showValidation(): Js =
    JS.addClass(elemId, "is-invalid") &
      JS.removeClass(elemId, "is-valid")

  def hideValidation(): Js =
    JS.removeClass(elemId, "is-invalid")
