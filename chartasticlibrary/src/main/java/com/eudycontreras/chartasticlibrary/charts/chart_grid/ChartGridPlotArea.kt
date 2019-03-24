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
import com.eudycontreras.chartasticlibrary.properties.LightSource
import com.eudycontreras.chartasticlibrary.properties.MutableColor
import com.eudycontreras.chartasticlibrary.shapes.BoundingBox
import com.eudycontreras.chartasticlibrary.shapes.Line
import com.eudycontreras.chartasticlibrary.utilities.extensions.asFloat
import com.eudycontreras.chartasticlibrary.utilities.extensions.dp
import com.eudycontreras.chartasticlibrary.utilities.global.mapRange

/**
 * Created by eudycontreras.
 */
class ChartGridPlotArea(private val barChart: BarChart, private val layoutManager: ChartLayoutManager) : ChartBoundsOwner, ChartElement, TouchableElement {

    enum class Border(var value: Int) {
        TOP(0),
        LEFT(1),
        RIGHT(2),
        BOTTOM(3),
        NONE(4),
        ALL(-1),
    }

    private val boundsBox: Shape by lazy {
        BoundingBox().apply {
            this.bounds.update(this@ChartGridPlotArea.bounds.drawableArea)
        }
    }

    override var computeBounds: Boolean = true
        set(value) {
            field = value
            if (!value) {
                layoutManager.removeBoundsOwner(this)
            } else {
                layoutManager.addBoundsOwner(this)
            }
        }

    override var drawBounds: Boolean = false

    override val anchor: ChartLayoutManager.BoundsAnchor = ChartLayoutManager.BoundsAnchor.CENTER

    override var render: Boolean = true

    override val bounds: Bounds = Bounds(this)

    private var borderLineThickness = 1.dp

    private val borders = arrayListOf(Line(), Line(), Line(), Line())

    var spacingMultiplier: Float = 0.45f

    val zeroPadding: Float = 0.dp

    var barsRevealed: Boolean = false
        private set

    val interceptor: ValueInterceptor = ValueInterceptor()

    val zeroPointInterceptor: ZeroPointInterceptor = ZeroPointInterceptor()

    var chartGrid: ChartGrid = ChartGrid(layoutManager)

    init {
        layoutManager.addBoundsOwner(this)
        build()
    }

    fun build(bounds: Bounds = Bounds()) {
        this.bounds.update(bounds)

        setBorderColor(MutableColor.rgb(255, 255, 255), Border.ALL)

        showBorder(false, Border.ALL)

        setBorderElevation(0f, Border.ALL)

        setBorderThickness(1.5f.dp, Border.ALL)

        setUpGridArea(this.bounds.drawableArea)
        //setUpInterceptor(this.bounds.drawableArea)
        setUpBorders(this.chartGrid.bounds.drawableArea)
    }

    private fun setUpGridArea(bounds: Bounds) {
        chartGrid.data = barChart.data
        chartGrid.minorGridLineCount = 0
        chartGrid.minorGridLineColor = MutableColor.rgba(100, 100, 110, 0.10f)
        chartGrid.majorGridLineColor = MutableColor.rgba(100, 105, 110, 0.10f)
        chartGrid.majorGridLineThickness = 1.5f.dp
        chartGrid.minorGridLineThickness = 0.7f.dp

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
        zeroPointInterceptor.lineThickness = 1f.dp
        zeroPointInterceptor.markerRadius = 10.dp
        zeroPointInterceptor.markerPaddingMultiplier = 0.5f
        zeroPointInterceptor.showShadows = true
        zeroPointInterceptor.positionY = zeroPoint
        zeroPointInterceptor.positionX = (bounds.left + (bounds.width / 2)) +  zeroPointInterceptor.markerRadius
        zeroPointInterceptor.build(bounds)
    }

    private fun setUpBorders(bounds: Bounds) {
        borders.forEach { it.shadowPosition = LightSource.Position.BOTTOM_LEFT }

        val left = bounds.left
        val right = bounds.right

        val top = bounds.top
        val bottom = bounds.bottom

        borders[Border.TOP.value].coordinate.x = left
        borders[Border.TOP.value].coordinate.y = top
        borders[Border.TOP.value].dimension.width = Math.abs(left - right)

        borders[Border.LEFT.value].coordinate.x = left
        borders[Border.LEFT.value].coordinate.y = top
        borders[Border.LEFT.value].dimension.height = Math.abs(bottom - top)

        borders[Border.BOTTOM.value].coordinate.x = left
        borders[Border.BOTTOM.value].coordinate.y = bottom - borders[Border.BOTTOM.value].dimension.height
        borders[Border.BOTTOM.value].dimension.width = Math.abs(left - right)

        borders[Border.RIGHT.value].coordinate.x = right - borders[Border.RIGHT.value].dimension.width
        borders[Border.RIGHT.value].coordinate.y = top
        borders[Border.RIGHT.value].dimension.height = Math.abs(bottom - top)
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

        barChart.acrossGradient?.run { applyCrossGradient(this) }

        barChart.view.onFullyVisible = { animateBarReveal() }
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

        interceptor.build(bounds.drawableArea)
        chartGrid.build(bounds.drawableArea)

        setUpBars()
        setUpBorders(chartGrid.bounds.drawableArea)
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
        barChart.data.chartItems.forEach { it.render(path, paint, canvas, renderingProperties) }
        //interceptor.render(path, paint, canvas, renderingProperties)
        zeroPointInterceptor.render(path, paint, canvas, renderingProperties)
        borders.forEach { it.render(path, paint, canvas, renderingProperties) }
    }

    override fun onTouch(event: MotionEvent, x: Float, y: Float, shapeRenderer: ShapeRenderer) {
        zeroPointInterceptor.onTouch(event, x, y, shapeRenderer)
    }

    override fun onLongPressed(event: MotionEvent, x: Float, y: Float) {
        zeroPointInterceptor.onLongPressed(event, x, y)
    }

    fun setBorderColor(color: MutableColor, border: Border = Border.ALL) {
        if (border == Border.ALL) {
            borders.forEach { it.color.setColor(color) }
            return
        }
        borders[border.value].color.setColor(color)
    }

    fun setBorderElevation(elevation: Float, border: Border = Border.ALL) {
        if (border == Border.ALL) {
            borders.forEach {
                if (it.render) {
                    it.drawShadow = elevation > 0
                    it.elevation = elevation
                }
            }
            return
        }

        if (borders[border.value].render) {
            borders[border.value].drawShadow = elevation > 0
            borders[border.value].elevation = elevation
        }
    }

    fun setBorderThickness(thickness: Float, border: Border = Border.ALL) {
        if (border == Border.ALL) {
            this.borderLineThickness = thickness
            borders[Border.TOP.value].dimension.height = thickness
            borders[Border.LEFT.value].dimension.width = thickness
            borders[Border.BOTTOM.value].dimension.height = thickness
            borders[Border.RIGHT.value].dimension.width = thickness
            return
        }
        when (border) {
            Border.RIGHT -> borders[Border.RIGHT.value].dimension.width = thickness
            Border.TOP -> borders[Border.TOP.value].dimension.height = thickness
            Border.LEFT -> borders[Border.LEFT.value].dimension.width = thickness
            Border.BOTTOM -> borders[Border.BOTTOM.value].dimension.height = thickness
            else -> this.borderLineThickness = thickness
        }
    }

    fun getBorderThickness(border: Border = Border.ALL): Float {
        if (border == Border.ALL) {
            return borderLineThickness
        }
        return when (border) {
            Border.RIGHT -> borders[Border.RIGHT.value].dimension.width
            Border.TOP -> borders[Border.TOP.value].dimension.height
            Border.LEFT -> borders[Border.LEFT.value].dimension.width
            Border.BOTTOM -> borders[Border.BOTTOM.value].dimension.height
            else -> borderLineThickness
        }
    }

    fun showBorder(show: Boolean, border: Border = Border.ALL) {
        if (border == Border.ALL) {
            borders.forEach { it.render = show }
            return
        }
        borders[border.value].render = show
    }

    fun isBorderVisible(border: Border = Border.ALL): Boolean{
        return if (border == Border.ALL) {
            borders.all { it.render }
        } else {
            borders[border.value].render
        }
    }

    fun getBorderColor(border: Border): MutableColor {
        return borders[border.value].color
    }

    fun getBorderElevation(border: Border): Float {
        return borders[border.value].elevation
    }

    fun getBorders(): Collection<Line> {
        return borders
    }
}