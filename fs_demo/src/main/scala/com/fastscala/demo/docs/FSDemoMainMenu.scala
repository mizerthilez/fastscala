package com.fastscala.demo.docs

import com.fastscala.demo.docs.about.{ AboutPage, AuthorPage, GettingStartedPage }
import com.fastscala.demo.docs.bootstrap.*
import com.fastscala.demo.docs.fastscala.*
import com.fastscala.demo.docs.forms.*
import com.fastscala.demo.docs.html.{ HtmlTagsPage, HtmlUtilsPage, ScalaTagsPage }
import com.fastscala.demo.docs.js.JsUtilsPage
import com.fastscala.demo.docs.loggedout.LoginPage
import com.fastscala.demo.docs.navigation.*
import com.fastscala.demo.docs.navigation.DefaultBSMenuRenderers.given
import com.fastscala.demo.docs.other.JSTreePage
import com.fastscala.demo.docs.tables.ModifyingTableExamplePage

object FSDemoMainMenu
    extends BSMenu(
      MenuSection("About FastScala")(
        RoutingMenuItem()("About", AboutPage()),
        RoutingMenuItem("getting_started")("Getting Started", GettingStartedPage()),
        RoutingMenuItem("author")("Author", AuthorPage()),
      ),
      MenuSection("FastScala")(
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
        RoutingMenuItem("demo", "fastscala", "internal_metrics")(
          "Internal Metrics",
          InternalMetricsPage(),
        ),
        RoutingMenuItem("demo", "fastscala", "discard_fs_context")(
          "FSContext discard",
          FSContextDiscardPage(),
        ),
        SimpleMenuItem(
          "Grafana",
          "https://grafana.fastscala.com/public-dashboards/e01e760c4321418e9b4903e7e6bfcfb0?orgId=1&refresh=5s",
        ),
        RoutingMenuItem("demo", "empty-page")("Empty page", EmptyPage()),
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
        RoutingMenuItem("demo", "js", "overview")("Overview", JsUtilsPage())
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
        SimpleMenuItem("Creating a form", "/demo/simple_form"),
        RoutingMenuItem("demo", "forms", "text_input")("Text Input Fields", TextInputFieldsPage()),
        RoutingMenuItem("demo", "forms", "select_input")("Select Input Fields", SelectInputFieldsPage()),
        RoutingMenuItem("demo", "forms", "checkbox_input")(
          "Checkbox Input Fields",
          CheckboxInputFieldsPage(),
        ),
        RoutingMenuItem("demo", "forms", "radio_input")("Radio Input Fields", RadioInputFieldsPage()),
        RoutingMenuItem("demo", "forms", "enum_input")("Enum Input Fields", FormInputTypesPage()),
        RoutingMenuItem("demo", "forms", "validation")("Validation", FormValidationPage()),
        RoutingMenuItem("demo", "forms", "validation-strategies")(
          "Validation Strategies",
          ValidationStrategiesPage(),
        ),
        RoutingMenuItem("demo", "forms", "validation-by-field-type")(
          "Validation by Field Type",
          ValidationByFieldTypePage(),
        ),
        RoutingMenuItem("demo", "forms", "input-groups")("Input Groups", FormInputGroupsPage()),
        RoutingMenuItem("demo", "forms", "server-side-update")(
          "Server-Side Update",
          UpdatesFromServerSidePage(),
        ),
        RoutingMenuItem("demo", "forms", "field-state")("Field state", FieldStatesPage()),
      ),
      MenuSection("Table Lib")(
        SimpleMenuItem("Simple", "/demo/simple_tables"),
        SimpleMenuItem("Sortable", "/demo/sortable_tables"),
        SimpleMenuItem("Paginated", "/demo/paginated_tables"),
        SimpleMenuItem("Selectable Rows", "/demo/selectable_rows_tables"),
        SimpleMenuItem("Selectable Columns", "/demo/tables_sel_cols"),
        RoutingMenuItem("demo", "tables", "modifying")("Modifying tables", ModifyingTableExamplePage()),
      ),
      MenuSection("Logged in")(
        RoutingMenuItem("demo", "login")("Login example", LoginPage())
      ),
      MenuSection("Other integrations")(
        SimpleMenuItem("ChartJS", "/demo/chartjs/simple"),
        RoutingMenuItem("demo", "jstree", "simple")("JSTree", JSTreePage()),
      ),
    )
