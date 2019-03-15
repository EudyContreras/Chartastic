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
import com.eudycontreras.chartasticlibrary.charts.chart_grid.ChartGrid
import com.eudycontreras.chartasticlibrary.charts.chart_grid.ChartGridAxisY
import com.eudycontreras.chartasticlibrary.charts.chart_grid.ChartGridPlotArea
import com.eudycontreras.chartasticlibrary.charts.chart_legend.LegendArea
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

    var showGridBorder: ChartGrid.Border = ChartGrid.Border.NONE

    var barHighlightCriteria: HighlightCriteria? = null

    var acrossGradient: Gradient? = null

    var barRevealAnimation: ChartAnimation<List<ChartAnimation.Animateable>>? = null

    var render = true

    lateinit var view: ChartView
        private set

    private lateinit var chartBoundsManager: ChartBoundsManager

    lateinit var chartLegendArea: LegendArea

    lateinit var chartGridPlotArea: ChartGridPlotArea

    lateinit var chartAxisYLeft: ChartGridAxisY
    lateinit var chartAxisYRight: ChartGridAxisY

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

        chartBoundsManager = ChartBoundsManager(rectBounds.subtract((12.dp)))

        chartLegendArea =  LegendArea(this, chartBoundsManager)
        chartGridPlotArea = ChartGridPlotArea(this, chartBoundsManager)

        chartAxisYLeft = ChartGridAxisY(this, chartBoundsManager, ChartGridAxisY.Type.LEFT)

      /*chartAxisYRight = ChartGridAxisY(this, chartBoundsManager, ChartGridAxisY.Type.RIGHT)
        chartAxisYRight.prepend = ""
        chartAxisYRight.append = ""
        chartAxisYRight.pointCount = 8
        chartAxisYRight.padding = Padding(6.dp, 6.dp, 10.dp, 0.dp)
        chartAxisYRight.textSize = 9.sp
        chartAxisYRight.typeFace = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        chartAxisYRight.textColor = MutableColor.rgba(255, 255, 255, 0.95f)
        chartAxisYRight.showLabels = true
        chartAxisYRight.build()*/

        chartLegendArea.computeBounds = false
        chartAxisYLeft.computeBounds = true
        chartGridPlotArea.chartGrid.buildMajorLines(chartAxisYLeft)
        chartGridPlotArea.setUpBars(chartGridPlotArea.chartGrid)
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

        chartLegendArea.render(path, paint, canvas, renderingProperties)
        chartGridPlotArea.render(path, paint, canvas, renderingProperties)
        chartAxisYLeft.render(path, paint, canvas, renderingProperties)
        //chartAxisYRight.render(path, paint, canvas, renderingProperties)
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
            mElements.add(chartAxisYLeft)
            mElements.add(chartAxisYRight)
            mElements.addAll(chartAxisYLeft.getElements())
            mElements.addAll(chartAxisYRight.getElements())
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