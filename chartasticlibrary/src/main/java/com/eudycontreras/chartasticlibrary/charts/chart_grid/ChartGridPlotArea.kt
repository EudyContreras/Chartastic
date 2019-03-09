package com.eudycontreras.chartasticlibrary.charts.chart_grid

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent
import com.eudycontreras.chartasticlibrary.Shape
import com.eudycontreras.chartasticlibrary.ShapeRenderer
import com.eudycontreras.chartasticlibrary.charts.ChartBoundsManager
import com.eudycontreras.chartasticlibrary.charts.ChartElement
import com.eudycontreras.chartasticlibrary.charts.chart_interceptor.ValueInterceptor
import com.eudycontreras.chartasticlibrary.charts.chart_model.bar_chart.BarChart
import com.eudycontreras.chartasticlibrary.charts.interfaces.TouchableElement
import com.eudycontreras.chartasticlibrary.properties.Bounds
import com.eudycontreras.chartasticlibrary.properties.MutableColor
import com.eudycontreras.chartasticlibrary.properties.Padding
import com.eudycontreras.chartasticlibrary.shapes.BoundingBox
import com.eudycontreras.chartasticlibrary.utilities.extensions.dp

/**
 * Created by eudycontreras.
 */
class ChartGridPlotArea(private val barChart: BarChart, private val boundsManager: ChartBoundsManager) : ChartBoundsManager.ChartBoundsOwner, ChartElement, TouchableElement {

    override var computeBounds: Boolean = true

    override var drawBounds: Boolean = true

    override val anchor: ChartBoundsManager.BoundsAnchor = ChartBoundsManager.BoundsAnchor.CENTER

    override var render: Boolean = true

    override val bounds: Bounds = Bounds(this)

    val interceptor: ValueInterceptor = ValueInterceptor()

    var chartGrid: ChartGrid = ChartGrid(boundsManager)

    private val boundsBox: Shape by lazy {
        BoundingBox().apply {
            this.bounds.update(this@ChartGridPlotArea.bounds.drawableArea)
        }
    }

    init {
        boundsManager.addBoundsOwner(this)
        build()
    }

    override fun build(bounds: Bounds) {
        this.bounds.update(bounds)
        setUpGridArea(this.bounds.drawableArea)
        setUpInterceptor(this.bounds.drawableArea)
    }

    private fun setUpGridArea(bounds: Bounds) {
        chartGrid.valueYPointCount = 16
        chartGrid.pointLineColor = MutableColor.rgba(130, 130, 130, 0.35f)
        chartGrid.pointLineThickness = 0.8f.dp
        chartGrid.setDataSource(barChart.data)
        chartGrid.setBorderColor(MutableColor.rgb(255, 255, 255), ChartGrid.Border.ALL)
        chartGrid.showBorder(false, ChartGrid.Border.TOP)
        chartGrid.showBorder(false, ChartGrid.Border.RIGHT)
        chartGrid.showBorder(false, ChartGrid.Border.LEFT)
        chartGrid.showBorder(true, ChartGrid.Border.BOTTOM)
        chartGrid.setBorderElevation(1.dp, ChartGrid.Border.ALL)
        chartGrid.setBorderThickness(1.dp, ChartGrid.Border.TOP)
        chartGrid.setBorderThickness(1.dp, ChartGrid.Border.LEFT)
        chartGrid.setBorderThickness(1.dp, ChartGrid.Border.RIGHT)
        chartGrid.setBorderThickness(6.dp, ChartGrid.Border.BOTTOM)
        chartGrid.showGridLines(true)
        chartGrid.showYTextValues(true, ChartGridAxisY.LEFT)
        chartGrid.showYValues(bounds, barChart.data, 6.dp, 6.dp, ChartGridAxisY.LEFT, " LOC")
    }

    private fun setUpInterceptor(bounds: Bounds) {
        interceptor.visible = true
        interceptor.lineColor = MutableColor.rgb(255, 255, 255)
        interceptor.markerFillColor = MutableColor.rgb(0, 150, 235)
        interceptor.markerStrokeColor = MutableColor.rgb(255, 255, 255)
        interceptor.lineThickness = 0.75f.dp
        interceptor.markerRadius = 20.dp
        interceptor.showShadows = true
        interceptor.showHorizontalLine = true
        interceptor.showVerticalLine = true
        interceptor.build(bounds)
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
            boundsBox.bounds.update(bounds.drawableArea, false)
        }
        this.bounds.drawableArea.paddings = Padding(top = 10.dp)
        interceptor.build(bounds.drawableArea)
        chartGrid.showYValues(bounds.drawableArea, barChart.data, 0.dp, 0.dp, ChartGridAxisY.LEFT, " LOC")
    }

    override fun render(
        path: Path,
        paint: Paint,
        canvas: Canvas,
        renderingProperties: ShapeRenderer.RenderingProperties
    ) {
        if (drawBounds) {
            boundsBox.render(path, paint, canvas, renderingProperties)
        }

        chartGrid.render(path, paint, canvas, renderingProperties)
        interceptor.render(path, paint, canvas, renderingProperties)
    }

    override fun onTouch(event: MotionEvent, x: Float, y: Float, shapeRenderer: ShapeRenderer) {
        interceptor.onTouch(event, x, y, shapeRenderer)
    }

    override fun onLongPressed(event: MotionEvent, x: Float, y: Float) {
        interceptor.onLongPressed(event, x, y)
    }
}