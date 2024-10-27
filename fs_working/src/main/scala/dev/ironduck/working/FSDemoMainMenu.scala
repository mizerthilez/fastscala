package dev.ironduck.working

import dev.ironduck.working.pages.*
import dev.ironduck.working.navigation.*
import dev.ironduck.working.navigation.DefaultBSMenuRenderers.given

val FSDemoMainMenu = BSMenu(
  MenuSection("Demo")(
    RoutingMenuItem("demo", "modal")("Modal", BootstrapModalPage()),
    RoutingMenuItem("demo", "test")("Test", TestPage()),
  )
)
