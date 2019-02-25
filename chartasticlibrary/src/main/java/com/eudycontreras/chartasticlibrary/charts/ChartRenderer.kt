package com.eudycontreras.chartasticlibrary.charts

import android.graphics.Canvas
import com.eudycontreras.chartasticlibrary.ShapeRenderer
import com.eudycontreras.chartasticlibrary.properties.Bounds

/**
 * Created by eudycontreras.
 */

class ChartRenderer {

    private var mRendering = false

    private val charts =  ArrayList<Chart>()

    private var shapeRenderer = ShapeRenderer()

    var rendering: Boolean
        get() = mRendering
        set(value) {mRendering = value}

    fun <T : Chart> addChart(chart: T) {
        charts.add(chart)
    }

    fun <T : Chart> addChart(chart: Array<T>) {
        charts.addAll(chart)
    }

    fun <T : Chart> removeChart(chart: T) {
        charts.remove(chart)
    }

    fun <T : Chart> removeChart(vararg chart: T) {
        charts.removeAll(chart)
    }

    fun setShapeRenderer(shapeRenderer: ShapeRenderer) {
        this.shapeRenderer = shapeRenderer
    }

    fun buildCharts(bounds: Bounds) {
        for (chart in charts) {
            chart.build(bounds)
        }
    }

    fun renderCharts(canvas: Canvas?) {
        for (chart in charts) {
            shapeRenderer.renderShape(canvas, chart.getBackground())
            chart.getElements().forEach { it.render(canvas) }
            shapeRenderer.renderShape(canvas, chart.getShapes())
        }
    }
}