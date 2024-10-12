package com.fastscala.demo.docs

import com.fastscala.demo.docs.about.{ AboutPage, AuthorPage, GettingStartedPage }
import com.fastscala.demo.docs.bootstrap.*
import com.fastscala.demo.docs.fastscala.*
import com.fastscala.demo.docs.forms.FormInputTypesPage
import com.fastscala.demo.docs.html.{ HtmlTagsPage, HtmlUtilsPage, ScalaTagsPage }
import com.fastscala.demo.docs.js.JsUtilsPage
import com.fastscala.demo.docs.navigation.*
import com.fastscala.demo.docs.navigation.DefaultBSMenuRenderers.given
import com.fastscala.demo.docs.tables.ModifyingTableExamplePage

object FSDemoMainMenu
    extends BSMenu(
      MenuSection("About FastScala")(
        RoutingMenuItem()("About", AboutPage()),
        RoutingMenuItem("getting_started")("Getting Started", GettingStartedPage()),
        RoutingMenuItem("author")("Author", AuthorPage()),
      ),
      MenuSection("FastScala Basics")(
        RoutingMenuItem("demo", "fastscala", "callbacks")(
          "Callbacks",
          CallbacksPage(),
        ),
        RoutingMenuItem("demo", "fastscala", "rerenderable")(
          "Rerenderable",
          RerenderablePage(),
        ),
        RoutingMenuItem("demo", "fastscala", "file_upload")(
          "File Upload",
          FileUploadPage(),
        ),
        RoutingMenuItem("demo", "fastscala", "anon_page")(
          "Anonymous Page",
          AnonymousPage(),
        ),
        RoutingMenuItem("demo", "fastscala", "file_download")(
          "File Download",
          FileDownloadPage(),
        ),
        RoutingMenuItem("demo", "fastscala", "server_side_push")(
          "Server Side Push",
          ServerSidePushPage(),
        ),
      ),
      MenuSection("HTML utils")(
        RoutingMenuItem("demo", "html", "tags")("tags", HtmlTagsPage()),
        RoutingMenuItem("demo", "html", "utils")("utils", HtmlUtilsPage()),
        RoutingMenuItem("demo", "html", "scala-tags")(
          "Integrating ScalaTags",
          ScalaTagsPage(),
        ),
      ),
      MenuSection("Js utils")(
        RoutingMenuItem("demo", "js", "overview")("Overview", JsUtilsPage()),
        SimpleMenuItem("BarChart", "/demo/chartjs/simple"),
      ),
      MenuSection("Bootstrap utils")(
        RoutingMenuItem("demo", "bootstrap", "buttons")(
          "Buttons",
          BootstrapButtonsPage(),
        ),
        RoutingMenuItem("demo", "bootstrap", "typography")(
          "Typography",
          BootstrapTypographyPage(),
        ),
        RoutingMenuItem("demo", "bootstrap", "images")(
          "Images",
          BootstrapImagesPage(),
        ),
        RoutingMenuItem("demo", "bootstrap", "modal")("Modal", BootstrapModalPage()),
      ),
      MenuSection("Forms Lib")(
        SimpleMenuItem("Simple", "/demo/simple_form"),
        RoutingMenuItem("demo", "forms", "input_types")(
          "Input Types",
          FormInputTypesPage(),
        ),
      ),
      MenuSection("Table Lib")(
        SimpleMenuItem("Simple", "/demo/simple_tables"),
        SimpleMenuItem("Sortable", "/demo/sortable_tables"),
        SimpleMenuItem("Paginated", "/demo/paginated_tables"),
        SimpleMenuItem("Selectable Rows", "/demo/selectable_rows_tables"),
        SimpleMenuItem("Selectable Columns", "/demo/tables_sel_cols"),
        RoutingMenuItem("demo", "tables", "modifying")("Modifying tables", ModifyingTableExamplePage()),
      ),
      //  MenuSection("chart.js integration")(
      //    SimpleMenuItem("Simple", "/demo/chartjs/simple")
      //  ),
    )
