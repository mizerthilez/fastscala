package com.fastscala.demo.docs.chartjs

import scala.xml.NodeSeq

import com.fastscala.chartjs.*
import com.fastscala.core.FSContext
import com.fastscala.demo.docs.SingleCodeExamplePage
import com.fastscala.xml.scala_xml.JS

class SimpleChartjsPage extends SingleCodeExamplePage():
  override def pageTitle: String = "Simple chart.js Example"

  override def append2Body(): NodeSeq = super.append2Body() ++
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

  override def renderExampleContents()(implicit fsc: FSContext): NodeSeq =
    // === code snippet ===
    import com.fastscala.chartjs.ChartJsNullable2Option.*
    <canvas id="chart"></canvas> ++ JS.inScriptTag(
      ChartJs(
        `type` = BarChartType,
        data = ChartData(
          datasets = List(
            BarChartDataset(
              label = "dataset 1",
              data = SimpleNumbersChartDatasetData(List(12, 19, 3, 5, 2, 3.0)),
              borderColor = "#91C8E4",
              backgroundColor = "#4682A9",
            )
          ),
          labels = List("Test", "Test", "Test", "Test"),
        ),
      ).installInCanvas("chart").onDOMContentLoaded
    )
    // === code snippet ===
