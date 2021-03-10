package com.eudycontreras.chartasticlibrary.charts.chart_grid

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent
import com.eudycontreras.chartasticlibrary.Shape
import com.eudycontreras.chartasticlibrary.ShapeRenderer
import com.eudycontreras.chartasticlibrary.charts.ChartElement
import com.eudycontreras.chartasticlibrary.charts.ChartLayoutManager
import com.eudycontreras.chartasticlibrary.charts.chart_interceptor.ValueInterceptor
import com.eudycontreras.chartasticlibrary.charts.chart_interceptor.ZeroPointInterceptor
import com.eudycontreras.chartasticlibrary.charts.chart_model.bar_chart.BarChart
import com.eudycontreras.chartasticlibrary.charts.chart_model.bar_chart.BarChartItem
import com.eudycontreras.chartasticlibrary.charts.interfaces.ChartBoundsOwner
import com.eudycontreras.chartasticlibrary.charts.interfaces.TouchableElement
import com.eudycontreras.chartasticlibrary.properties.Bounds
import com.eudycontreras.chartasticlibrary.properties.Gradient
import com.eudycontreras.chartasticlibrary.properties.MutableColor
import com.eudycontreras.chartasticlibrary.shapes.BoundingBox
import com.eudycontreras.chartasticlibrary.utilities.extensions.asFloat
import com.eudycontreras.chartasticlibrary.utilities.extensions.dp
import com.eudycontreras.chartasticlibrary.utilities.global.BoundsChangeListener
import com.eudycontreras.chartasticlibrary.utilities.global.mapRange

/**
 * Created by eudycontreras.
 */
class ChartGridPlotArea(private val barChart: BarChart, private val layoutManager: ChartLayoutManager) : ChartBoundsOwner, ChartElement, TouchableElement {

    private val boundsBox: Shape by lazy {
        BoundingBox().apply {
            this.bounds.update(this@ChartGridPlotArea.bounds.drawableArea)
        }
    }

    override val changeListeners: ArrayList<BoundsChangeListener> by lazy { ArrayList<BoundsChangeListener>() }

    override var computeBounds: Boolean = true
        set(value) {
            field = value
            if (!value) {
                layoutManager.removeBoundsOwner(this)
            } else {
                layoutManager.addBoundsOwner(this)
            }
        }

    override val anchor: ChartLayoutManager.BoundsAnchor = ChartLayoutManager.BoundsAnchor.CENTER

    override var render: Boolean = true

    override val bounds: Bounds = Bounds(this)

    var spacingMultiplier: Float = 0.45f

    val zeroPadding: Float = 12.dp

    var barsRevealed: Boolean = false
        private set

    val gridBorder: ChartGridBorder = ChartGridBorder()

    val interceptor: ValueInterceptor = ValueInterceptor()

    val zeroPointInterceptor: ZeroPointInterceptor = ZeroPointInterceptor()

    var chartGrid: ChartGrid = ChartGrid(layoutManager)

    init {
        layoutManager.addBoundsOwner(this)
        build()
    }

    fun build(bounds: Bounds = Bounds()) {
        this.bounds.update(bounds)

        setUpGridBorder(this.chartGrid.bounds.drawableArea)
        setUpGridArea(this.bounds.drawableArea)
        setUpInterceptor(this.bounds.drawableArea)
    }

    private fun setUpGridBorder(bounds: Bounds){

        gridBorder.setBorderColor(MutableColor.rgb(255, 255, 255), ChartGridBorder.Border.ALL)

        gridBorder.showBorder(false, ChartGridBorder.Border.ALL)

        gridBorder.setBorderElevation(0f, ChartGridBorder.Border.ALL)

        gridBorder.setBorderThickness(1.5f.dp, ChartGridBorder.Border.ALL)

        gridBorder.build(bounds)
    }

    private fun setUpGridArea(bounds: Bounds) {
        chartGrid.data = barChart.data
        chartGrid.minorGridLineCount = 0

        chartGrid.majorGridLineColor = MutableColor.rgba(100, 105, 110, 0.10f)
        chartGrid.minorGridLineColor = MutableColor.rgba(100, 100, 110, 0.10f)

        chartGrid.majorGridLineThickness = 1.5f.dp
        chartGrid.minorGridLineThickness = 0.4f.dp

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

    private fun setUpZeroPointInterceptor(bounds: Bounds, zeroPoint: Float) {
        zeroPointInterceptor.visible = true
        zeroPointInterceptor.lineColor = MutableColor.rgb(255, 255, 255)
        zeroPointInterceptor.markerColor = MutableColor.rgb(255)
        zeroPointInterceptor.markerOuterColor = MutableColor.rgb(255).updateAlpha(0.5f)
        zeroPointInterceptor.lineThickness = 2.dp
        zeroPointInterceptor.markerRadius = 10.dp
        zeroPointInterceptor.markerPaddingMultiplier = 0.5f
        zeroPointInterceptor.showShadows = true
        zeroPointInterceptor.positionY = zeroPoint
        zeroPointInterceptor.positionX = (bounds.left + (bounds.width / 2)) +  zeroPointInterceptor.markerRadius
        zeroPointInterceptor.build(bounds)
    }

    fun setUpBars() {

        chartGrid.build(bounds.drawableArea)
        chartGrid.buildMajorLines()

        if(chartGrid.maxY == null) {
           return
        }

        fun createBarSeries() {
            chartGrid.gridAxisY.values?.let { axisValuesY ->
                val bounds = chartGrid.bounds.drawableArea

                var lastBarX = bounds.coordinate.x

                val thicknessDelta = (bounds.width / barChart.data.chartItems.size)
                val thickness = thicknessDelta - (thicknessDelta * spacingMultiplier)
                val offset = thicknessDelta - ((thicknessDelta * spacingMultiplier) / barChart.data.chartItems.size)

                val zeroValue = 0f
                val zeroPoint = axisValuesY.valuesBuildData.zeroPoint.y

                setUpZeroPointInterceptor(bounds, zeroPoint)

                fun handlePositiveValues(bar: BarChartItem<out Any>, value: Float) {

                    val maxHeight = (zeroPoint - bounds.top) - zeroPadding

                    bar.length = mapRange(
                        value,
                        zeroValue,
                        axisValuesY.maxValueRounded.asFloat(),
                        zeroPadding,
                        maxHeight)

                    bar.thickness = thickness
                    bar.x = lastBarX + (thicknessDelta * spacingMultiplier)
                    bar.y = (bounds.top + maxHeight) - bar.length
                    bar.build()

                    lastBarX += offset

                    bar.backgroundOptions.height = (zeroPoint - bounds.top) - zeroPadding
                    bar.backgroundOptions.y = bounds.coordinate.y
                }

                fun handleNegativeValues(bar: BarChartItem<out Any>, value: Float) {

                    val maxHeight = (bounds.bottom - zeroPoint)

                    bar.negativeOrientation = true
                    bar.length = -mapRange(
                        value,
                        zeroValue,
                        axisValuesY.minValueRounded.asFloat(),
                        zeroPadding,
                        maxHeight - zeroPadding)

                    bar.thickness = thickness
                    bar.x = lastBarX + (thicknessDelta * spacingMultiplier)
                    bar.y = (zeroPoint - bar.length) + zeroPadding
                    bar.build()

                    lastBarX += offset

                    bar.backgroundOptions.height = (bounds.bottom - zeroPoint) - zeroPadding
                    bar.backgroundOptions.y = zeroPoint + zeroPadding
                }

                for(bar in barChart.data.chartItems) {

                    val value =  bar.value.toString().toFloat()

                    if (value >= 0) {
                        handlePositiveValues(bar, value)
                    } else {
                        handleNegativeValues(bar, value)
                    }
                }
            }
        }

        fun applyCrossGradient(it: Gradient) {
            if ( barChart.data.chartItems.isNotEmpty()) {

                val last =  barChart.data.chartItems[ barChart.data.chartItems.size -1]
                val longest =  barChart.data.chartItems.sortedByDescending { it.length }.first()

                val mainX =  barChart.data.chartItems[0].x
                val mainY = longest.y

                val mainWidth = (last.x + last.thickness) - mainX
                val mainHeight = (longest.y + longest.length) - mainY

                for (bar in  barChart.data.chartItems) {
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
                    barChart.data.chartItems.forEach { bar ->
                        barChart.barHighlightCriteria?.let {
                            if (it.invoke(bar.data)) {
                                if (bar.highlightable) {
                                    bar.applyHighlight()
                                }
                            }
                        }
                    }
                }
                barChart.barRevealAnimation?.animate(barChart.view,  barChart.data.chartItems)
            }
        }

        createBarSeries()

        barChart.chartAxisXTop.buildBarTicks(chartGrid.getMajorLines())
        barChart.chartAxisXBottom.buildBarTicks(chartGrid.getMajorLines())

        barChart.acrossGradient?.run { applyCrossGradient(this) }

        barChart.view.onFullyVisible = {
            if (barChart.barRevealAnimation != null) {
                animateBarReveal()
            } else {
                barsRevealed = true
                barChart.data.chartItems.forEach { bar ->
                    bar.onPreAnimation()
                    bar.onAnimate(1f)
                    bar.onPostAnimation()
                    barChart.view.updateView()
                    barChart.barHighlightCriteria?.let {
                        if (it.invoke(bar.data)) {
                            if (bar.highlightable) {
                                bar.applyHighlight()
                            }
                        }
                    }
                }
            }
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
        if (layoutManager.showBoundingBoxes) {
            boundsBox.bounds.update(bounds.drawableArea, false)
        }

        interceptor.build(bounds.drawableArea)
        zeroPointInterceptor.build(bounds.drawableArea)

        setUpBars()

        gridBorder.build(chartGrid.bounds.drawableArea)

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
        if (layoutManager.showBoundingBoxes) {
            boundsBox.render(path, paint, canvas, renderingProperties)
        }

        chartGrid.render(path, paint, canvas, renderingProperties)
        barChart.data.chartItems.forEach { it.render(path, paint, canvas, renderingProperties) }
        gridBorder.render(path, paint, canvas, renderingProperties)
        interceptor.render(path, paint, canvas, renderingProperties)
        zeroPointInterceptor.render(path, paint, canvas, renderingProperties)
    }

    override fun onTouch(event: MotionEvent, x: Float, y: Float, shapeRenderer: ShapeRenderer) {
        zeroPointInterceptor.onTouch(event, x, y, shapeRenderer)
        interceptor.onTouch(event, x, y, shapeRenderer)
    }

    override fun onLongPressed(event: MotionEvent, x: Float, y: Float) {
        zeroPointInterceptor.onLongPressed(event, x, y)
        interceptor.onLongPressed(event, x, y)
    }
}