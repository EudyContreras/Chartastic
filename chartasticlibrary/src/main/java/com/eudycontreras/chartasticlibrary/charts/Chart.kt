package com.eudycontreras.chartasticlibrary.charts

import com.eudycontreras.chartasticlibrary.Shape
import com.eudycontreras.chartasticlibrary.properties.Bounds

/**
 * Created by eudycontreras.
 */
abstract class Chart {
    abstract fun build(bounds: Bounds)
    abstract fun getBackground(): Shape
    abstract fun getShapes(): List<Shape>
    abstract fun getElements(): List<ChartElement>
}