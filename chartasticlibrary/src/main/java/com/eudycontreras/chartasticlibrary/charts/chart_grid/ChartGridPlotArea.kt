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
import com.eudycontreras.chartasticlibrary.properties.Gradient
import com.eudycontreras.chartasticlibrary.properties.MutableColor
import com.eudycontreras.chartasticlibrary.properties.Padding
import com.eudycontreras.chartasticlibrary.shapes.BoundingBox
import com.eudycontreras.chartasticlibrary.utilities.extensions.dp
import com.eudycontreras.chartasticlibrary.utilities.extensions.roundToNearest
import com.eudycontreras.chartasticlibrary.utilities.global.mapRange

/**
 * Created by eudycontreras.
 */
class ChartGridPlotArea(private val barChart: BarChart, private val boundsManager: ChartBoundsManager) : ChartBoundsManager.ChartBoundsOwner, ChartElement, TouchableElement {

    override var computeBounds: Boolean = true
        set(value) {
            field = value
            if (!value) {
                boundsManager.removeBoundsOwner(this)
            } else {
                boundsManager.addBoundsOwner(this)
            }
        }

    override var drawBounds: Boolean = false

    override val anchor: ChartBoundsManager.BoundsAnchor = ChartBoundsManager.BoundsAnchor.CENTER

    override var render: Boolean = true

    override val bounds: Bounds = Bounds(this)

    var spacingMultiplier = 0.45f

    var barsRevealed: Boolean = false
        private set

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

    fun build(bounds: Bounds = Bounds()) {
        this.bounds.update(bounds)
        setUpGridArea(this.bounds.drawableArea)
        setUpInterceptor(this.bounds.drawableArea)
    }

    private fun setUpGridArea(bounds: Bounds) {
        chartGrid.data = barChart.data
        chartGrid.majorGridLineCount = 10
        chartGrid.minorGridLineCount = 0
        chartGrid.minorGridLineColor = MutableColor.rgba(100, 100, 110, 0.25f)
        chartGrid.majorGridLineColor = MutableColor.rgba(100, 100, 110, 0.25f)
        chartGrid.majorGridLineThickness = 1.dp
        chartGrid.minorGridLineThickness = 0.7f.dp

        chartGrid.setBorderColor(MutableColor.rgb(255, 255, 255), ChartGrid.Border.ALL)

        chartGrid.showBorder(false, ChartGrid.Border.TOP)
        chartGrid.showBorder(false, ChartGrid.Border.RIGHT)
        chartGrid.showBorder(false, ChartGrid.Border.LEFT)
        chartGrid.showBorder(false, ChartGrid.Border.BOTTOM)

        chartGrid.setBorderElevation(1.dp, ChartGrid.Border.ALL)

        chartGrid.setBorderThickness(2.dp, ChartGrid.Border.TOP)
        chartGrid.setBorderThickness(2.dp, ChartGrid.Border.LEFT)
        chartGrid.setBorderThickness(8.dp, ChartGrid.Border.RIGHT)
        chartGrid.setBorderThickness(8.dp, ChartGrid.Border.BOTTOM)

        chartGrid.showGridLines(true, ChartGrid.GridLineType.MAJOR)
        chartGrid.showGridLines(false, ChartGrid.GridLineType.MINOR)

        chartGrid.build(bounds)
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

    fun setUpBars(chartGrid: ChartGrid) {

        val data = barChart.data

        fun createSeriesBars() {
            val bounds = chartGrid.drawableZone

            var lastBarX = bounds.coordinate.x

            val size = data.getBarChartItems().size

            val min = 0f
            val max = barChart.chartAxisYLeft.maxY.toString().toFloat().roundToNearest()

            val thicknessDelta = (bounds.width / size)
            val thickness = thicknessDelta - (thicknessDelta * spacingMultiplier)
            val offset = thicknessDelta - ((thicknessDelta * spacingMultiplier) / size)

            for(bar in data.getBarChartItems()) {

                bar.length = mapRange(
                    bar.value.toString().toFloat(),
                    min,
                    max,
                    min,
                    bounds.height)

                bar.thickness = thickness
                bar.x = lastBarX + (thicknessDelta * spacingMultiplier)
                bar.y = (bounds.top + bounds.height) - bar.length
                bar.build()

                lastBarX += offset

                bar.backgroundOptions.height = bounds.dimension.height
                bar.backgroundOptions.y = bounds.coordinate.y
            }
        }

        fun applyCrossGradient(it: Gradient) {
            if (data.getBarChartItems().isNotEmpty()) {

                val last = data.getBarChartItems()[data.getBarChartItems().size -1]
                val longest = data.getBarChartItems().sortedByDescending { it.length }.first()

                val mainX = data.getBarChartItems()[0].x
                val mainY = longest.y

                val mainWidth = (last.x + last.thickness) - mainX
                val mainHeight = (longest.y + longest.length) - mainY

                for (bar in data.getBarChartItems()) {
                    bar.shape.shader = Shape.getShader(
                        it,
                        mainX,
                        mainY,
                        mainWidth,
                        mainHeight)
                }
            }
        }

        fun animateBarReveal() {
            if (!barsRevealed) {
                barsRevealed = true
                barChart.barRevealAnimation?.onEnd = {
                    data.getBarChartItems().forEach { bar ->
                        barChart.barHighlightCriteria?.let {
                            if (it.invoke(bar.data)) {
                                if (bar.highlightable) {
                                    bar.applyHighlight()
                                }
                            }
                        }
                    }
                }
                barChart.barRevealAnimation?.animate(barChart.view, data.getBarChartItems())
            }
        }

        createSeriesBars()

        barChart.acrossGradient?.run { applyCrossGradient(this) }

        barChart.view.onFullyVisible = { animateBarReveal() }
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
        chartGrid.build(bounds.drawableArea)
        interceptor.build(bounds.drawableArea)

      //  chartGrid.showYValues(bounds.drawableArea, barChart.data, 0.dp, 0.dp, ChartGridAxisY.LEFT, " LOC")
    }

    override fun render(
        path: Path,
        paint: Paint,
        canvas: Canvas,
        renderingProperties: ShapeRenderer.RenderingProperties
    ) {
        if (!render || !computeBounds) {
            return
        }
        if (drawBounds) {
            boundsBox.render(path, paint, canvas, renderingProperties)
        }

        chartGrid.render(path, paint, canvas, renderingProperties)
        barChart.data.getBarChartItems().forEach { it.render(path, paint, canvas, renderingProperties) }
        interceptor.render(path, paint, canvas, renderingProperties)
    }

    override fun onTouch(event: MotionEvent, x: Float, y: Float, shapeRenderer: ShapeRenderer) {
        interceptor.onTouch(event, x, y, shapeRenderer)
    }

    override fun onLongPressed(event: MotionEvent, x: Float, y: Float) {
        interceptor.onLongPressed(event, x, y)
    }
}