package com.fastscala.demo.docs

import scala.xml.NodeSeq

import com.fastscala.core.FSContext

trait PageWithTopTitle extends BasePage:
  def pageTitle: String

  def pageTitleToolbar()(using FSContext): NodeSeq = NodeSeq.Empty

  def renderStandardPageContents()(using FSContext): NodeSeq

  def renderPageContents()(using FSContext): NodeSeq =
    <div class="app-content-header">
      <div class="container-fluid">
        <div class="row">
          <div class="col-sm-6">
            <h3 class="mb-0">{pageTitle}</h3>
          </div>
        </div>
      </div>
    </div>
    <div class="app-content">
      <div class="container-fluid">
        <div class="card card-outline card-secondary">
          <div class="btn-toolbar mb-2 mb-md-0">
            {pageTitleToolbar()}
          </div>
          {renderStandardPageContents()}
        </div>
      </div>
    </div>
