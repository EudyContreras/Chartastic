package com.eudycontreras.chartasticlibrary.charts

import android.graphics.Canvas

/**
 * Created by eudycontreras.
 */
interface ChartElement {
    var render: Boolean

    fun build()
    fun render(canvas: Canvas?)
}