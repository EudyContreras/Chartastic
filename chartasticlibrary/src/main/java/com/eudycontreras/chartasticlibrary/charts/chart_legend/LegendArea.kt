package com.eudycontreras.chartasticlibrary.charts.chart_legend

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.eudycontreras.chartasticlibrary.Shape
import com.eudycontreras.chartasticlibrary.ShapeRenderer
import com.eudycontreras.chartasticlibrary.charts.ChartBoundsManager
import com.eudycontreras.chartasticlibrary.charts.ChartElement
import com.eudycontreras.chartasticlibrary.charts.chart_model.bar_chart.BarChart
import com.eudycontreras.chartasticlibrary.properties.Bounds
import com.eudycontreras.chartasticlibrary.shapes.BoundingBox
import com.eudycontreras.chartasticlibrary.utilities.extensions.dp

/**
 * Created by eudycontreras.
 */
class LegendArea(private val barChart: BarChart, private val boundsManager: ChartBoundsManager): ChartBoundsManager.ChartBoundsOwner, ChartElement {

    enum class Position {
        TOP,
        BOTTOM
    }

    var position: Position = Position.TOP
        set(value) {
            boundsManager.removeBoundsOwner(this)
            field = value
            boundsManager.addBoundsOwner(this)
        }

    private val boundsBox: Shape by lazy {
        BoundingBox().apply {
            this.bounds.update(this@LegendArea.bounds)
        }
    }

    override var render: Boolean = true

    override var drawBounds: Boolean = true

    override var computeBounds: Boolean = true

    override val bounds: Bounds = Bounds(this)

    override val anchor: ChartBoundsManager.BoundsAnchor
        get() {
            return when (position) {
                Position.TOP -> ChartBoundsManager.BoundsAnchor.TOP
                Position.BOTTOM -> ChartBoundsManager.BoundsAnchor.BOTTOM
            }
        }

    init {
        boundsManager.addBoundsOwner(this)
        build(this.bounds.addDimension(0f, 80.dp))
    }

    override fun build(bounds: Bounds) {
        this.bounds.update(bounds)
    }

    override fun render(
        path: Path,
        paint: Paint,
        canvas: Canvas,
        renderingProperties: ShapeRenderer.RenderingProperties
    ){
        if (computeBounds) {
            if (drawBounds) {
                boundsBox.render(path, paint, canvas, renderingProperties)
            }
        }
    }

    override fun notifyBoundsChange(bounds: Bounds) {
        if(bounds != this.bounds) {
            this.bounds.update(bounds, false)
        }
        boundsManager.notifyBoundsChange(this)
    }

    override fun propagateNewBounds(bounds: Bounds) {
        this.bounds.update(bounds, false)
        if (drawBounds) {
            boundsBox.bounds.update(bounds, false)
        }
    }
}