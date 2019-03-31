package com.eudycontreras.chartasticlibrary.charts.chart_model.bar_chart

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import com.eudycontreras.chartasticlibrary.R
import com.eudycontreras.chartasticlibrary.Shape
import com.eudycontreras.chartasticlibrary.ShapeRenderer
import com.eudycontreras.chartasticlibrary.charts.*
import com.eudycontreras.chartasticlibrary.charts.chart_grid.ChartGridAxisX
import com.eudycontreras.chartasticlibrary.charts.chart_grid.ChartGridAxisY
import com.eudycontreras.chartasticlibrary.charts.chart_grid.ChartGridPlotArea
import com.eudycontreras.chartasticlibrary.charts.chart_legend.LegendArea
import com.eudycontreras.chartasticlibrary.charts.chart_options.AxisYOptions
import com.eudycontreras.chartasticlibrary.charts.interfaces.TouchableElement
import com.eudycontreras.chartasticlibrary.properties.*
import com.eudycontreras.chartasticlibrary.shapes.Rectangle
import com.eudycontreras.chartasticlibrary.utilities.extensions.dp
import com.eudycontreras.chartasticlibrary.utilities.global.HighlightCriteria

/**
 * Created by eudycontreras.
 */
class BarChart(private val context: Context, var data: BarChartData) : Chart, TouchableElement {

    private val rectangle: Rectangle = Rectangle()

    private val mShapes = ArrayList<Shape>()
    private val mElements = ArrayList<ChartElement>()

    private val mYValue = ChartGridAxisY.Type.LEFT

    private var showBoundingBoxes = false

    var showDataLabels: Boolean = false

    var showAxixLabels: Boolean = false

    var showMajorTickMarks: Boolean = false

    var showMinorTickMarks: Boolean = false

    var showMajorGridLines: Boolean = false

    var showMinorGridLines: Boolean = false

    var showGroupDividers: Boolean = false

    var showAxisYLabels: Pair<Boolean, ChartGridAxisY.Type> = Pair(true, ChartGridAxisY.Type.LEFT)

    var showGridLines: Boolean = false

    var showLegend: Boolean = false

    var showInterceptor: Boolean = false

    var showDataBarDecorations: Boolean = false

    var showDataBars: Boolean = false

    var showDataBarBackgrounds: Boolean = false

    var showDataBarTooltips: Boolean = false

    var showGridBorder: ChartGridPlotArea.Border = ChartGridPlotArea.Border.NONE

    var barHighlightCriteria: HighlightCriteria? = null

    var acrossGradient: Gradient? = null

    var barRevealAnimation: ChartAnimation<List<ChartAnimation.Animateable>>? = null

    var render = true

    lateinit var view: ChartView
        private set

    private lateinit var chartLayoutManagerOuter: ChartLayoutManager
    private lateinit var chartLayoutManagerInner: ChartLayoutManager

    lateinit var chartGridPlotArea: ChartGridPlotArea

    lateinit var chartLegendTop: LegendArea
    lateinit var chartLegendBottom: LegendArea

    lateinit var chartAxisXTop: ChartGridAxisX
    lateinit var chartAxisXBottom: ChartGridAxisX

    lateinit var chartAxisYLeft: ChartGridAxisY
    lateinit var chartAxisYRight: ChartGridAxisY

    override fun build(view: ChartView, bounds: Bounds) {
        this.view = view

        val widthMultiplier = 1f
        val heightMultiplier = 0.91f

        val rectBounds = setBackground(
            x = (bounds.dimension.width / 2) - ((bounds.dimension.width / 2) * widthMultiplier),
            y = (bounds.dimension.height / 2) - ((bounds.dimension.height / 2) * heightMultiplier),
            width = (bounds.dimension.width * widthMultiplier),
            height = (bounds.dimension.height * heightMultiplier)
        )

        val drawBoundingBoxes = false

        chartLayoutManagerOuter = ChartLayoutManager(rectBounds)
        chartLayoutManagerOuter.type = ChartLayoutManager.Type.OUTER
        chartLayoutManagerOuter.showBoundingBoxes = drawBoundingBoxes

        chartLayoutManagerInner = ChartLayoutManager(chartLayoutManagerOuter)
        chartLayoutManagerInner.type = ChartLayoutManager.Type.INNER
        chartLayoutManagerInner.showBoundingBoxes = drawBoundingBoxes

        chartGridPlotArea = ChartGridPlotArea(this, chartLayoutManagerInner)

        val optionsLeft = AxisYOptions().apply {
            labelValueAppend = " LOC"
            padding = Padding(4.dp, 4.dp, 0.dp, 0.dp)
            positiveValuePointCount = 6
            negativeValuePointCount = 0
            showLabels = true
            showTickLines = true
            chartData = data
            build()
        }

        chartAxisYLeft = ChartGridAxisY(chartLayoutManagerInner, ChartGridAxisY.Type.LEFT)
        chartAxisYLeft.options = optionsLeft
        chartAxisYLeft.changeListeners.add { chartGridPlotArea.chartGrid.build(it) }
        chartAxisYLeft.build()

        val optionsRight = AxisYOptions().apply {
            labelValueAppend = ""
            valuePointCount = 5
            padding = Padding(4.dp, 6.dp, 0.dp, 0.dp)
            showLabels = false
            showTickLines = true
            chartData = data
            build()
        }

        chartAxisYRight = ChartGridAxisY(chartLayoutManagerInner, ChartGridAxisY.Type.RIGHT)
        chartAxisYRight.options = optionsRight
        chartAxisYRight.changeListeners.add { chartGridPlotArea.chartGrid.build(it) }
        chartAxisYRight.build()

        chartAxisXTop = ChartGridAxisX(this, chartLayoutManagerInner,ChartGridAxisX.Type.TOP)
        chartAxisXTop.bounds.updateDimensions(0f, 40.dp)

        chartAxisXBottom = ChartGridAxisX(this, chartLayoutManagerInner,ChartGridAxisX.Type.BOTTOM)
        chartAxisXBottom.bounds.updateDimensions(0f, 40.dp)

        chartLegendTop = LegendArea(this, chartLayoutManagerOuter, LegendArea.Position.TOP)
        chartLegendTop.bounds.updateDimensions(0f, 50.dp)

        chartLegendBottom =  LegendArea(this, chartLayoutManagerOuter,LegendArea.Position.BOTTOM)
        chartLegendBottom.bounds.updateDimensions(0f, 50.dp)

        chartGridPlotArea.chartGrid.gridAxisY = chartAxisYLeft
        chartGridPlotArea.chartGrid.gridAxisX = chartAxisXBottom

        chartGridPlotArea.setUpBars()

        chartAxisXBottom.computeBounds = false
        chartAxisXTop.computeBounds = false
        chartLegendTop.computeBounds = false
        chartLegendBottom.computeBounds = false
        //chartAxisYLeft.computeBounds = false
        //chartAxisYRight.computeBounds = false
    }

    override fun render(
        path: Path,
        paint: Paint,
        canvas: Canvas,
        renderingProperties: ShapeRenderer.RenderingProperties
    ) {
        if (!render) {
            return
        }

        chartAxisXTop.render(path, paint, canvas, renderingProperties)
        chartAxisXBottom.render(path, paint, canvas, renderingProperties)
        chartGridPlotArea.render(path, paint, canvas, renderingProperties)
        chartAxisYLeft.render(path, paint, canvas, renderingProperties)
        chartAxisYRight.render(path, paint, canvas, renderingProperties)
        chartLegendTop.render(path, paint, canvas, renderingProperties)
        chartLegendBottom.render(path, paint, canvas, renderingProperties)
        chartLayoutManagerInner.render(path, paint, canvas, renderingProperties)
    }

    override fun getBackground(): Shape {
        return rectangle
    }

    override fun getShapes(): List<Shape> {
        return mShapes
    }

    override fun getElements(): List<ChartElement> {
        if (mElements.isEmpty()) {
            mElements.add(chartAxisXTop)
            mElements.add(chartAxisXBottom)
            mElements.add(chartGridPlotArea)
            mElements.add(chartAxisYLeft)
            mElements.add(chartAxisYRight)
            mElements.addAll(chartAxisYLeft.getElements()!!)
            mElements.addAll(chartAxisYRight.getElements()!!)
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

    override fun onTouch(event: MotionEvent, x: Float, y: Float, shapeRenderer: ShapeRenderer) {
        chartGridPlotArea.onTouch(event, x, y, shapeRenderer)
    }

    override fun onLongPressed(event: MotionEvent, x: Float, y: Float) {
        chartGridPlotArea.onLongPressed(event, x, y)
    }
}