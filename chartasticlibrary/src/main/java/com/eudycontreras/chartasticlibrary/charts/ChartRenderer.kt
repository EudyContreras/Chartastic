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

    var mShapeRenderer = ShapeRenderer()

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
        this.mShapeRenderer = shapeRenderer
    }

    fun buildCharts(bounds: Bounds) {
        charts.forEach { it.build(bounds) }

        mShapeRenderer.addShape(charts.map { it.getBackground() })
        mShapeRenderer.addShape(charts.flatMap { it.getShapes() })

        val elements = charts.flatMap { it.getElements() }

        mShapeRenderer.renderCapsule = { canvas ->
            elements.forEach { it.render(canvas) }
        }
    }

    fun renderCharts(canvas: Canvas?) {
        mShapeRenderer.renderShape(canvas)
    }
}