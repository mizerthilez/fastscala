package com.fastscala.demo.docs.html

import com.fastscala.core.FSContext
import com.fastscala.demo.docs.MultipleCodeExamples2Page

class HtmlTagsPage extends MultipleCodeExamples2Page():
  override def pageTitle: String = "HTML tags"

  override def renderContentsWithSnippets()(implicit fsc: FSContext): Unit =

    renderSnippet("Creating div tags"):
      import com.fastscala.templates.bootstrap5.helpers.BSHelpers.{ given, * }
      div.apply("Lorem ipsum dolor sit amet, consectetur adipiscing elit.")
    renderSnippet("Creating span tags"):
      import com.fastscala.templates.bootstrap5.helpers.BSHelpers.{ given, * }
      span.apply("Lorem ipsum dolor sit amet, consectetur adipiscing elit.")
    renderSnippet("Creating pre tags"):
      import com.fastscala.templates.bootstrap5.helpers.BSHelpers.{ given, * }
      pre.apply("Lorem ipsum dolor sit amet, consectetur adipiscing elit.")
    renderSnippet("Creating s tags"):
      import com.fastscala.templates.bootstrap5.helpers.BSHelpers.{ given, * }
      s.apply("Lorem ipsum dolor sit amet, consectetur adipiscing elit.")
    renderSnippet("Creating u tags"):
      import com.fastscala.templates.bootstrap5.helpers.BSHelpers.{ given, * }
      u.apply("Lorem ipsum dolor sit amet, consectetur adipiscing elit.")
    renderSnippet("Creating small tags"):
      import com.fastscala.templates.bootstrap5.helpers.BSHelpers.{ given, * }
      small.apply("Lorem ipsum dolor sit amet, consectetur adipiscing elit.")
    renderSnippet("Creating strong tags"):
      import com.fastscala.templates.bootstrap5.helpers.BSHelpers.{ given, * }
      strong.apply("Lorem ipsum dolor sit amet, consectetur adipiscing elit.")
    renderSnippet("Creating em tags"):
      import com.fastscala.templates.bootstrap5.helpers.BSHelpers.{ given, * }
      em.apply("Lorem ipsum dolor sit amet, consectetur adipiscing elit.")
    renderSnippet("Creating b tags"):
      import com.fastscala.templates.bootstrap5.helpers.BSHelpers.{ given, * }
      b.apply("Lorem ipsum dolor sit amet, consectetur adipiscing elit.")
    renderSnippet("Creating p tags"):
      import com.fastscala.templates.bootstrap5.helpers.BSHelpers.{ given, * }
      p.apply("Lorem ipsum dolor sit amet, consectetur adipiscing elit.")
    renderSnippet("Creating h1 tags"):
      import com.fastscala.templates.bootstrap5.helpers.BSHelpers.{ given, * }
      h1.apply("h1 element")
    renderSnippet("Creating h2 tags"):
      import com.fastscala.templates.bootstrap5.helpers.BSHelpers.{ given, * }
      h2.apply("h2 element")
    renderSnippet("Creating h3 tags"):
      import com.fastscala.templates.bootstrap5.helpers.BSHelpers.{ given, * }
      h3.apply("h3 element")
    renderSnippet("Creating h4 tags"):
      import com.fastscala.templates.bootstrap5.helpers.BSHelpers.{ given, * }
      h4.apply("h4 element")
    renderSnippet("Creating h5 tags"):
      import com.fastscala.templates.bootstrap5.helpers.BSHelpers.{ given, * }
      h5.apply("h5 element")
    renderSnippet("Creating h6 tags"):
      import com.fastscala.templates.bootstrap5.helpers.BSHelpers.{ given, * }
      h6.apply("h6 element")
    renderSnippet("Creating hr tags"):
      import com.fastscala.templates.bootstrap5.helpers.BSHelpers.*
      hr
    closeSnippet()
