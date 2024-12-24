package com.fastscala.taskmanager.app

import com.fastscala.demo.docs.navigation.DefaultBSMenuRenderers.given
import com.fastscala.demo.docs.navigation.{ BSMenu, RoutingMenuItem }

object FSTaskmanagerMainMenu
    extends BSMenu(
//  MenuSection("Task Manager")(
      RoutingMenuItem("tasks")("Tasks", TasksPage()),
      RoutingMenuItem("users")("Users", UsersPage()),
//  )
    )
