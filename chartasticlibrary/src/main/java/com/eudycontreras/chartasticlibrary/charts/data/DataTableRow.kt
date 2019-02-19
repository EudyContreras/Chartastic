package com.eudycontreras.chartasticlibrary.charts.data

/**
 * Created by eudycontreras.
 */

class DataTableRow(vararg valueData: DataTableValue) {

    val recordValues = ArrayList<DataTableValue>()

    init {
        recordValues.addAll(valueData)
    }
}