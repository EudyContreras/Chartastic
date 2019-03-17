package com.eudycontreras.chartasticlibrary.charts.chart_grid

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.eudycontreras.chartasticlibrary.Shape
import com.eudycontreras.chartasticlibrary.ShapeRenderer
import com.eudycontreras.chartasticlibrary.charts.ChartElement
import com.eudycontreras.chartasticlibrary.charts.ChartLayoutManager
import com.eudycontreras.chartasticlibrary.charts.chart_model.bar_chart.BarChart
import com.eudycontreras.chartasticlibrary.charts.interfaces.ChartBoundsOwner
import com.eudycontreras.chartasticlibrary.properties.Bounds
import com.eudycontreras.chartasticlibrary.shapes.BoundingBox

/**
 * Created by eudycontreras.
 */

class ChartGridAxisX(
    private val barChart: BarChart,
    private val layoutManager: ChartLayoutManager,
    _type: Type = Type.BOTTOM
) : ChartBoundsOwner, ChartElement {


    enum class Type {
        TOP,
        BOTTOM
    }

    enum class ValueType {
        ALPHABETIC,
        NUMERIC
    }

    var type: Type = _type
        set(value) {
            layoutManager.removeBoundsOwner(this)
            field = value
            layoutManager.addBoundsOwner(this)
        }

    var valueType: ValueType = ValueType.NUMERIC

    private val boundsBox: Shape by lazy {
        BoundingBox().apply {
            this.bounds.update(this@ChartGridAxisX.bounds.drawableArea)
        }
    }

    override var render: Boolean = true

    override var drawBounds: Boolean = false

    override var computeBounds: Boolean = true
        set(value) {
            field = value
            if (!value) {
                layoutManager.removeBoundsOwner(this)
            } else {
                layoutManager.addBoundsOwner(this)
            }
        }

    override val bounds: Bounds = Bounds(this)

    override val anchor: ChartLayoutManager.BoundsAnchor
        get() {
            return when (type) {
                Type.TOP -> ChartLayoutManager.BoundsAnchor.TOP
                Type.BOTTOM -> ChartLayoutManager.BoundsAnchor.BOTTOM
            }
        }

    init {
        layoutManager.addBoundsOwner(this)
        build(this.bounds)
    }

    fun build(bounds: Bounds = Bounds()) {
        this.bounds.update(bounds)
    }

    override fun render(
        path: Path,
        paint: Paint,
        canvas: Canvas,
        renderingProperties: ShapeRenderer.RenderingProperties
    ){
        if (!render || !computeBounds) {
            return
        }
        if (drawBounds) {
            boundsBox.render(path, paint, canvas, renderingProperties)
        }
    }

    override fun notifyBoundsChange(bounds: Bounds) {
        if(bounds != this.bounds) {
            this.bounds.update(bounds, false)
        }
        layoutManager.notifyBoundsChange(this)
    }

    override fun propagateNewBounds(bounds: Bounds) {
        this.bounds.update(bounds, false)
        if (drawBounds) {
            boundsBox.bounds.update(bounds.drawableArea, false)
        }
    }
}
