package com.eudycontreras.chartasticlibrary.charts.chartModels.barChart

import com.eudycontreras.chartasticlibrary.charts.data.DataTable
import com.eudycontreras.chartasticlibrary.charts.data.DataTableGroup
import com.eudycontreras.chartasticlibrary.charts.data.DataTableGrouper
import com.eudycontreras.chartasticlibrary.charts.data.DataTableValue

/**
 * Created by eudycontreras.
 */

class ChartData(
    private val dataTable: DataTable,
    private val keyX: String,
    private val keyY: String,
    private val grouper: DataTableGrouper? = null
) {

    val valueXES: List<DataTableValue>
        get() = dataTable.getValuesForColumn(keyX)

    val valueY: List<DataTableValue>
        get() = dataTable.getValuesForColumn(keyY)

    val groupBy: DataTableValue?
        get() =  dataTable.getAttribute(grouper?.first)

    val groupTarget: DataTableGroup.GroupPointer?
        get() = grouper?.second

}