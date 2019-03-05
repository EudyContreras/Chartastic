package com.eudycontreras.chartasticlibrary.charts

/**
 * Created by eudycontreras.
 */
interface ChartView {
    var onFullyVisible: ((ChartView) -> Unit)?
    fun fullyVisible(): Boolean
    fun updateView()
}