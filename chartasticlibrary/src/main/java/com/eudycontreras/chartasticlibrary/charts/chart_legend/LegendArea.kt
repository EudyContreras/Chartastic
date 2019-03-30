package com.eudycontreras.chartasticlibrary.charts.chart_legend

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.eudycontreras.chartasticlibrary.ShapeRenderer
import com.eudycontreras.chartasticlibrary.charts.ChartElement
import com.eudycontreras.chartasticlibrary.charts.ChartLayoutManager
import com.eudycontreras.chartasticlibrary.charts.chart_model.bar_chart.BarChart
import com.eudycontreras.chartasticlibrary.charts.interfaces.ChartBoundsOwner
import com.eudycontreras.chartasticlibrary.properties.Bounds
import com.eudycontreras.chartasticlibrary.shapes.BoundingBox
import com.eudycontreras.chartasticlibrary.utilities.global.BoundsChangeListener

/**
 * Created by eudycontreras.
 */
class LegendArea(private val barChart: BarChart, private val layoutManager: ChartLayoutManager, position: Position = Position.TOP): ChartBoundsOwner, ChartElement {

    enum class Position {
        TOP,
        BOTTOM
    }

    var position: Position = position
        set(value) {
            layoutManager.removeBoundsOwner(this)
            field = value
            layoutManager.addBoundsOwner(this)
        }

    private val boundsBox: BoundingBox by lazy {
        BoundingBox().apply {
            this.bounds.update(this@LegendArea.bounds.drawableArea)
        }
    }

    override val changeListeners: ArrayList<BoundsChangeListener> by lazy { ArrayList<BoundsChangeListener>() }

    override var render: Boolean = true

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
            return when (position) {
                Position.TOP -> ChartLayoutManager.BoundsAnchor.TOP
                Position.BOTTOM -> ChartLayoutManager.BoundsAnchor.BOTTOM
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
        if(layoutManager.showBoundingBoxes){
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
        if(layoutManager.showBoundingBoxes){
            boundsBox.bounds.update(bounds.drawableArea, false)
        }
    }
}