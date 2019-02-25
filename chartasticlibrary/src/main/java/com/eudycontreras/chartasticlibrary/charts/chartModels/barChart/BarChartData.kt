package com.eudycontreras.chartasticlibrary.charts.chartModels.barChart

import com.eudycontreras.chartasticlibrary.charts.ChartData
import com.eudycontreras.chartasticlibrary.charts.data.DataTable
import com.eudycontreras.chartasticlibrary.charts.data.DataTableGroup
import com.eudycontreras.chartasticlibrary.charts.data.DataTableGrouper
import com.eudycontreras.chartasticlibrary.charts.data.DataTableValue

/**
 * Created by eudycontreras.
 */

class BarChartData(
    private val dataTable: DataTable,
    private val keyX: String = "",
    private val keyY: String = "",
    private val grouper: DataTableGrouper? = null
): ChartData {
    val valueX: List<DataTableValue>
        get() = dataTable.getValuesForColumn(keyX)

    val valueTypeX: Any
        get() = dataTable.getTypeForColumn(keyX)

    val valueY: List<DataTableValue>
        get() = dataTable.getValuesForColumn(keyY)

    val valueTypeY: Any
        get() = dataTable.getTypeForColumn(keyY)

    val groupBy: DataTableValue?
        get() =  dataTable.getAttribute(grouper?.first)

    val groupTarget: DataTableGroup.GroupPointer?
        get() = grouper?.second

    private val mChartItems = ArrayList<BarChartItem<out Any>>()

    fun addBarChartItem(chartItem: BarChartItem<out Any>) {
        mChartItems.add(chartItem)
    }

    fun addBarChartItem(vararg chartItem: BarChartItem<Any>) {
        mChartItems.addAll(chartItem)
    }

    fun getBarChartItems(): List<BarChartItem<out Any>> {
        return mChartItems
    }
}