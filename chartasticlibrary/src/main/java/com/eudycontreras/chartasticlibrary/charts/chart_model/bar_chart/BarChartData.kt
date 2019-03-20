package com.eudycontreras.chartasticlibrary.charts.chart_model.bar_chart

import com.eudycontreras.chartasticlibrary.charts.ChartData
import com.eudycontreras.chartasticlibrary.charts.chart_data.DataTable
import com.eudycontreras.chartasticlibrary.charts.chart_data.DataTableGroup
import com.eudycontreras.chartasticlibrary.charts.chart_data.DataTableValue
import com.eudycontreras.chartasticlibrary.charts.chart_data.DataType
import com.eudycontreras.chartasticlibrary.utilities.global.DataTableGrouper

/**
 * Created by eudycontreras.
 */

class BarChartData(
    private val dataTable: DataTable,
    private val keyX: String = "",
    private val keyY: String = "",
    private val grouper: DataTableGrouper? = null
) : ChartData {
    var maxValueX: Any? = null

    var maxValueY: Any? = null

    var minValueX: Any? = null

    var minValueY: Any? = null

    val valueX: List<DataTableValue>
        get() = dataTable.getValuesForColumn(keyX)

    val valueTypeX: DataType
        get() = dataTable.getTypeForColumn(keyX)

    val valueY: List<DataTableValue>
        get() = dataTable.getValuesForColumn(keyY)

    val valueTypeY: DataType
        get() = dataTable.getTypeForColumn(keyY)

    val groupBy: DataTableValue?
        get() = dataTable.getAttribute(grouper?.first)

    val groupTarget: DataTableGroup.GroupPointer?
        get() = grouper?.second

    var zeroPoint: Any? = null

    val chartItems = ArrayList<BarChartItem<out Any>>()

    val chartClusters = ArrayList<BarChartItemGroup<out Any>>()

    fun addBarChartItem(chartItem: BarChartItem<out Any>) {
        chartItems.add(chartItem)
    }

    fun addBarChartItem(vararg chartItem: BarChartItem<Any>) {
        chartItems.addAll(chartItem)
    }
}