package com.eudycontreras.chartasticlibrary.charts.chart_model.bar_chart

import android.content.Context
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import com.eudycontreras.chartasticlibrary.R
import com.eudycontreras.chartasticlibrary.Shape
import com.eudycontreras.chartasticlibrary.ShapeRenderer
import com.eudycontreras.chartasticlibrary.charts.*
import com.eudycontreras.chartasticlibrary.charts.chart_grid.ChartGrid
import com.eudycontreras.chartasticlibrary.charts.chart_grid.ChartGridAxisY
import com.eudycontreras.chartasticlibrary.charts.chart_grid.ChartGridPlotArea
import com.eudycontreras.chartasticlibrary.charts.chart_legend.LegendArea
import com.eudycontreras.chartasticlibrary.charts.interfaces.TouchableElement
import com.eudycontreras.chartasticlibrary.properties.*
import com.eudycontreras.chartasticlibrary.shapes.Rectangle
import com.eudycontreras.chartasticlibrary.utilities.extensions.dp
import com.eudycontreras.chartasticlibrary.utilities.extensions.roundToNearest
import com.eudycontreras.chartasticlibrary.utilities.global.HighlightCriteria
import com.eudycontreras.chartasticlibrary.utilities.global.mapRange

/**
 * Created by eudycontreras.
 */
class BarChart(private val context: Context, var data: BarChartData) : Chart(), TouchableElement {

    private val rectangle: Rectangle = Rectangle()

    private val mShapes = ArrayList<Shape>()
    private val mElements = ArrayList<ChartElement>()

    private val mYValue = ChartGridAxisY.LEFT

    private var showBoundingBoxes = false

    var showDataLabels: Boolean = false

    var showAxixLabels: Boolean = false

    var showMajorTickMarks: Boolean = false

    var showMinorTickMarks: Boolean = false

    var showMajorGridLines: Boolean = false

    var showMinorGridLines: Boolean = false

    var showGroupDividers: Boolean = false

    var showAxisYLabels: Pair<Boolean, Int> = Pair(true, ChartGridAxisY.LEFT)

    var showGridLines: Boolean = false

    var showLegend: Boolean = false

    var showInterceptor: Boolean = false

    var showDataBarDecorations: Boolean = false

    var showDataBars: Boolean = false

    var showDataBarBackgrounds: Boolean = false

    var showDataBarTooltips: Boolean = false

    var showGridBorder: ChartGrid.Border = ChartGrid.Border.NONE

    var barHighlightCriteria: HighlightCriteria? = null

    var acrossGradient: Gradient? = null

    var barRevealAnimation: ChartAnimation<List<ChartAnimation.Animateable>>? = null

    private var barsRevealed: Boolean = false

    private lateinit var view: ChartView

    private lateinit var chartBoundsManager: ChartBoundsManager

    lateinit var chartLegendArea: LegendArea

    lateinit var chartGridPlotArea: ChartGridPlotArea

    lateinit var chartAxisY: ChartGridAxisY

    override fun build(view: ChartView, bounds: Bounds) {
        this.view = view

        val widthMultiplier = 1f
        val heightMultiplier = 1f

        val rectBounds = setBackground(
            x = (bounds.dimension.width / 2) - ((bounds.dimension.width / 2) * widthMultiplier),
            y = (bounds.dimension.height / 2) - ((bounds.dimension.height / 2) * heightMultiplier),
            width = (bounds.dimension.width * widthMultiplier),
            height = (bounds.dimension.height * heightMultiplier)
        )

        chartBoundsManager = ChartBoundsManager(rectBounds.add(-(12.dp)))

        chartLegendArea =  LegendArea(this, chartBoundsManager)
        chartGridPlotArea = ChartGridPlotArea(this, chartBoundsManager)
        chartAxisY = ChartGridAxisY(this, chartBoundsManager)


        val spacingMultiplier = 0.45f

      //  buildBars(mYValue, spacingMultiplier)
    }

    override fun getBackground(): Shape {
        return rectangle
    }

    override fun getShapes(): List<Shape> {
        if (mShapes.isEmpty()) {
            mShapes.addAll(data.getBarChartItems().flatMap { it.getShapes() })

        }
        return mShapes
    }

    override fun getElements(): List<ChartElement> {
        if (mElements.isEmpty()) {
            mElements.add(chartLegendArea)
            mElements.add(chartGridPlotArea)
           // mElements.addAll(chartGrid.getElements(mYValue))
        }
        return mElements
    }

    private fun setBackground(x: Float, y: Float, width: Float, height: Float): Bounds {
        rectangle.coordinate = Coordinate(x, y)
        rectangle.dimension = Dimension(width, height)
        rectangle.color = MutableColor(ContextCompat.getColor(context, R.color.colorPrimary))
        rectangle.strokeWidth = 1.dp

        return rectangle.bounds
    }



    private fun buildBars(value: Int, spacingMultiplier: Float, chartGrid: ChartGrid) {
        val bounds = chartGrid.drawableZone.copyProps()

        var lastBarX = bounds.coordinate.x

        for(bar in data.getBarChartItems()) {
            val max = chartGrid.getGridValueY(value).maxY.toString().toFloat().roundToNearest()
            bar.length = mapRange(
                bar.value.toString().toFloat(),
                0f,
                max,
                0f,
                bounds.dimension.height)
            val thickness = (bounds.dimension.width / data.getBarChartItems().size - 1)
            bar.thickness = (bounds.dimension.width / data.getBarChartItems().size - 1) - (thickness * spacingMultiplier)
            bar.x = lastBarX + (thickness * spacingMultiplier)
            bar.y = (bounds.coordinate.y + bounds.dimension.height) - bar.length
            bar.build()
            lastBarX += ((bounds.dimension.width) / (data.getBarChartItems().size)) - ((thickness * spacingMultiplier) / data.getBarChartItems().size)

            bar.backgroundOptions.height = bounds.dimension.height
            bar.backgroundOptions.y = bounds.coordinate.y
        }

        acrossGradient?.let {
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

        view.onFullyVisible = {
            if (!barsRevealed) {
                barsRevealed = true
                barRevealAnimation?.onEnd = {
                    data.getBarChartItems().forEach { bar ->
                        barHighlightCriteria?.let {
                            if (it.invoke(bar.data)) {
                                if (bar.highlightable) {
                                    bar.applyHighlight()
                                }
                            }
                        }
                    }
                }
                barRevealAnimation?.animate(view, data.getBarChartItems())
            }
        }
    }

    override fun onTouch(event: MotionEvent, x: Float, y: Float, shapeRenderer: ShapeRenderer) {
        chartGridPlotArea.onTouch(event, x, y, shapeRenderer)
    }

    override fun onLongPressed(event: MotionEvent, x: Float, y: Float) {
        chartGridPlotArea.onLongPressed(event, x, y)
    }
}