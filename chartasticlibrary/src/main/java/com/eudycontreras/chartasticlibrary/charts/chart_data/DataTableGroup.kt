package com.eudycontreras.chartasticlibrary.charts.chart_data

/**
 * Created by eudycontreras.
 */

data class DataTableGroup(
    var key: DataTableAttribute,
    var group: List<DataTable> = ArrayList(),
    var groupPointer: GroupPointer = GroupPointer.NONE
) {
    enum class GroupPointer {
        TABLE_ATTRIBUTE,
        TABLE_COLUMN,
        NONE
    }
}