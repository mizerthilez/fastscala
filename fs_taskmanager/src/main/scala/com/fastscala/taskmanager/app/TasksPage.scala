package com.fastscala.taskmanager.app

import scala.xml.{ Elem, NodeSeq }

import com.fastscala.core.FSContext
import com.fastscala.demo.docs.components.Widget
import com.fastscala.js.Js
import com.fastscala.taskmanager.db.{ DB, Task, User }
import com.fastscala.templates.bootstrap5.modals.BSModal5WithForm7Base
import com.fastscala.templates.bootstrap5.tables.*
import com.fastscala.templates.bootstrap5.toast.BSToast2
import com.fastscala.templates.bootstrap5.utils.BSBtn
import com.fastscala.templates.form7.F7Field
import com.fastscala.templates.form7.fields.layout.F7VerticalField
import com.fastscala.templates.form7.fields.select.F7SelectOptField
import com.fastscala.templates.form7.fields.text.F7StringField
import com.fastscala.xml.scala_xml.given

class TasksPage extends BasePage:
  override def pageTitle: String = "Tasks Page"

  import com.fastscala.templates.bootstrap5.helpers.BSHelpers.{ given, * }

  override def renderPageContents()(implicit fsc: FSContext): NodeSeq =
//    fsc.page.rerendererDebugStatus = RerendererDebugStatus.Enabled

    new Widget:
      override def widgetTitle: String = "Tasks"

      val tasksTable = new Table5Base
        with Table5BaseBootrapSupport
        with Table5StandardColumns
        with Table5SeqSortableDataSource
        with Table5SelectableRowsWithActions
        with Table5Paginated:

        override type R = Task

        override def defaultPageSize = 10

        val ColStatus = ColNs(
          "Status",
          implicit fsc =>
            r =>
              BSBtn().sm
                .icn(_.biCheck)
                .toggle(
                  r.completed,
                  v =>
                    r.completed = v
                    if r.completed then
                      BSToast2
                        .VerySimple(<label>Task completed!</label>)(<label>Your task <b>{
                          r.name
                        }</b> has been marked as complete.</label>)
                        .onToastHeader(_.text_bg_success)
                        .onToast(_.mt_5)
                        .installAndShow()
                    else Js.void
                  ,
                  _.BtnSuccess.withStyle("border-radius: 15px;"),
                  _.BtnOutlineSecondary.withStyle("border-radius: 15px;"),
                ),
        )
        val ColAssignedTo = ColNsFullTd(
          "Assigned to",
          implicit fsc => {
            case (tableBodyRerenderer, trRerenderer, tdRerenderer, row, rowIdx, colIdx, rows) =>
              val cmd = fsc
                .callback: () =>
                  import com.fastscala.demo.docs.forms.DefaultFSDemoBSForm7Renderers.given
                  new BSModal5WithForm7Base(s"Assign task ${row.name} to..."):
                    lazy val rootField: F7Field = F7SelectOptField[User]()
                      .optionsNonEmpty(DB().users.toSeq)
                      .option2String(_.map(_.fullName).getOrElse("Unassigned"))
                      .rw(row.assignedTo, row.assignedTo = _)

                    override def postSubmitForm()(implicit fsc: FSContext): Js =
                      super.postSubmitForm() & tdRerenderer.rerenderer.rerender()
                  .installAndShow()
                .cmd
              td.apply:
                (row.assignedTo match
                  case Some(assignedTo) => badge.text_bg_primary.apply(assignedTo.firstName)
                  case None => badge.text_bg_secondary.apply("unassigned")
                ).withAttrs("onclick" -> cmd)
          },
        )
        val ColName = ColStr("Name", _.name)

        override def columns(): List[C] = List(
          ColStatus,
          ColName,
          ColAssignedTo,
          ColActionsDefault,
          ColSelectRow,
        )

        override def rowsSorter: PartialFunction[Table5StandardColumn[R], Seq[R] => Seq[R]] = {
          case ColStatus => _.sortBy(_.completed)
          case ColName => _.sortBy(_.name)
          case ColAssignedTo => _.sortBy(_.assignedTo.map(_.firstName))
        }

        override def seqRowsSource: Seq[Task] = DB().tasks.toSeq

        override def actionsBtnToIncludeInTopDropdown = super.actionsBtnToIncludeInTopDropdown.sm.mx_2

      override def widgetTopRight()(implicit fsc: FSContext): NodeSeq = super.widgetTopRight() ++
        BSBtn().BtnSuccess.sm
          .lbl("Add Task")
          .ajax:
            implicit fsc =>
              import com.fastscala.demo.docs.forms.DefaultFSDemoBSForm7Renderers.given

              new BSModal5WithForm7Base("New Task"):
                val task = new Task("", false)

                lazy val rootField: F7Field = F7VerticalField(
                  new F7StringField()
                    .label("Name")
                    .placeholder("Write a task name...")
                    .rw(task.name, task.name = _)
                )

                override def postSubmitForm()(implicit fsc: FSContext): Js =
                  super.postSubmitForm() `&`:
                    DB().tasks += task
                    tasksTable.rerenderTable()
              .installAndShow()
          .btn ++ tasksTable.actionsDropdownBtnRenderer.render()

      override def widgetContents()(implicit fsc: FSContext): NodeSeq = tasksTable.render()

      override def transformWidgetCardBody(elem: Elem): Elem = elem.p_0
    .renderWidget()
