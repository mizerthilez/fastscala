package com.fastscala.taskmanager.app

import scala.xml.NodeSeq

import com.fastscala.core.FSContext
import com.fastscala.taskmanager.db.DB
import com.fastscala.components.bootstrap5.helpers.BSHelpers
import com.fastscala.components.bootstrap5.toast.BSToast2
import com.fastscala.components.bootstrap5.utils.BSBtn

trait BasePage extends com.fastscala.demo.docs.BasePage:
  import BSHelpers.{ given, * }

  override def navBarTopRight()(implicit fsc: FSContext): NodeSeq =
    super.navBarTopRight() ++
      ul.withClass("navbar-nav ms-2"):
        li.withClass("nav-item"):
          BSBtn().BtnSecondary
            .lbl("Save")
            .ajax:
              implicit fsc =>
                DB.save()
                BSToast2.VerySimple(<label>Saved</label>)(<p>Saved DB data to disk.</p>).installAndShow()
            .btn
        ++ li.withClass("nav-item"):
          BSBtn().BtnSecondary
            .lbl("Load")
            .ajax:
              implicit fsc =>
                DB.load()
                BSToast2
                  .VerySimple(<label>Loaded</label>)(<p>Loaded DB data from disk.</p>)
                  .installAndShow()
            .btn
            .ms_2

  override def renderSideMenu()(implicit fsc: FSContext): NodeSeq =
    FSTaskmanagerMainMenu.render()
