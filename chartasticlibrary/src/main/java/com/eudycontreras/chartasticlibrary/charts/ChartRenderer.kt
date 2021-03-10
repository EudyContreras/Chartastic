package com.eudycontreras.chartasticlibrary.charts

import android.graphics.Canvas
import android.view.MotionEvent
import com.eudycontreras.chartasticlibrary.ShapeRenderer
import com.eudycontreras.chartasticlibrary.charts.interfaces.TouchableElement
import com.eudycontreras.chartasticlibrary.properties.Bounds

/**
 * Created by eudycontreras.
 */

class ChartRenderer(private var view: ChartView) {

    private val charts =  ArrayList<Chart>()

    var shapeRenderer = ShapeRenderer()

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

    fun notifyFullyVisible() {

    }

    fun buildCharts(bounds: Bounds) {
        charts.forEach { it.build(view, bounds) }
        shapeRenderer.renderCapsule = { path, paint, canvas, properties ->
            charts.forEach { it.render(path, paint, canvas, properties) }
        }
    }

    fun renderCharts(canvas: Canvas) {
        shapeRenderer.renderShape(canvas)
    }

    fun delegateTouchEvent(motionEvent: MotionEvent, x: Float, y: Float) {
        charts.forEach {
            if (it is TouchableElement) {
                it.onTouch(motionEvent, x, y, shapeRenderer)
            }
        }
    }

    fun delegateLongPressEvent(motionEvent: MotionEvent, x: Float, y: Float) {
        charts.forEach {
            if (it is TouchableElement) {
                it.onLongPressed(motionEvent, x, y)
            }
        }
    }
}