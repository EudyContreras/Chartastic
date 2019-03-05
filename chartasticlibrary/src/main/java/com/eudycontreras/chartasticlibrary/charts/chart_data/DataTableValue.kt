package com.eudycontreras.chartasticlibrary.charts.chart_data

import com.eudycontreras.chartasticlibrary.properties.MutableColor

/**
 * Created by eudycontreras.
 */

data class DataTableValue(var attribute: String, var value: String) {
    var color: MutableColor? = null
}
