package com.eudycontreras.chartasticlibrary.charts

import android.view.View
import com.eudycontreras.chartasticlibrary.Shape
import com.eudycontreras.chartasticlibrary.properties.Bounds

/**
 * Created by eudycontreras.
 */
abstract class Chart {
    abstract fun build(view: ChartView, bounds: Bounds)
    abstract fun getBackground(): Shape
    abstract fun getShapes(): List<Shape>
    abstract fun getElements(): List<ChartElement>
}