package dev.ironduck.working

import dev.ironduck.working.bootstrap.*
import dev.ironduck.working.navigation.*
import dev.ironduck.working.navigation.DefaultBSMenuRenderers.given

val FSDemoMainMenu = BSMenu(
  MenuSection("Demo")(
    RoutingMenuItem("demo", "modal")("Modal", BootstrapModalPage())
  )
)
