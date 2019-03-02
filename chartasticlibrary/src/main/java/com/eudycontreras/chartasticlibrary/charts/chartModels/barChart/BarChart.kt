package com.eudycontreras.chartasticlibrary.charts.chartModels.barChart

import android.content.Context
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import com.eudycontreras.chartasticlibrary.R
import com.eudycontreras.chartasticlibrary.Shape
import com.eudycontreras.chartasticlibrary.ShapeRenderer
import com.eudycontreras.chartasticlibrary.charts.Chart
import com.eudycontreras.chartasticlibrary.charts.ChartAnimation
import com.eudycontreras.chartasticlibrary.charts.ChartElement
import com.eudycontreras.chartasticlibrary.charts.ChartView
import com.eudycontreras.chartasticlibrary.charts.chartGrids.ChartGrid
import com.eudycontreras.chartasticlibrary.charts.chartGrids.ChartGridAxisY
import com.eudycontreras.chartasticlibrary.charts.chartInterceptor.ValueInterceptor
import com.eudycontreras.chartasticlibrary.charts.interfaces.TouchableElement
import com.eudycontreras.chartasticlibrary.extensions.dp
import com.eudycontreras.chartasticlibrary.global.mapRange
import com.eudycontreras.chartasticlibrary.properties.Bounds
import com.eudycontreras.chartasticlibrary.properties.Coordinate
import com.eudycontreras.chartasticlibrary.properties.Dimension
import com.eudycontreras.chartasticlibrary.properties.MutableColor
import com.eudycontreras.chartasticlibrary.shapes.Rectangle

/**
 * Created by eudycontreras.
 */
class BarChart(private val context: Context, private var data: BarChartData) : Chart(), TouchableElement {

    private val chartGrid: ChartGrid = ChartGrid()
    private val interceptor: ValueInterceptor = ValueInterceptor()
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

    var barRevealAnimation: ChartAnimation? = null

    private var barsRevealed: Boolean = false

    private lateinit var view: ChartView

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

        buildGrid(16.dp, rectBounds.subtract(10.dp))

        if (data.getBarChartItems().isEmpty()) {

        }

        val spacing = 12.dp

        buildBars(mYValue, spacing)

        buildInterceptor()
    }

    override fun getBackground(): Shape {
        return rectangle
    }

    override fun getShapes(): List<Shape> {
        if (mShapes.isEmpty()) {
            mShapes.addAll(chartGrid.getLines())
            mShapes.addAll(data.getBarChartItems().flatMap { it.getShapes() })
            mShapes.addAll(interceptor.getElements())
            mShapes.addAll(chartGrid.getBorders())

            if (showBoundingBoxes) {
                mShapes.add(chartGrid.getBoundingBox(mYValue))
                mShapes.add(chartGrid.getBoundingBox())
            }
        }
        return mShapes
    }

    override fun getElements(): List<ChartElement> {
        if (mElements.isEmpty()) {
            mElements.addAll(chartGrid.getElements(mYValue))
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

    private fun buildGrid(padding: Float, bounds: Bounds) {
        chartGrid.valueYPointCount = 10
        chartGrid.pointLineColor = MutableColor.rgba(130, 130, 130, 0.35f)
        chartGrid.pointLineThickness = 0.8f.dp
        chartGrid.bounds = bounds
        chartGrid.setDataSource(data)
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
        chartGrid.showYValues(bounds, padding, 6.dp, 6.dp, mYValue, " LOC")
    }

    private fun buildInterceptor() {
        interceptor.visible = false
        interceptor.lineColor = MutableColor.rgb(255, 255, 255)
        interceptor.markerFillColor = MutableColor.rgb(0, 150, 235)
        interceptor.markerStrokeColor = MutableColor.rgb(255, 255, 255)
        interceptor.lineThickness = 0.75f.dp
        interceptor.markerRadius = 20.dp
        interceptor.showShadows = true
        interceptor.showHorizontalLine = true
        interceptor.showVerticalLine = true
        interceptor.build(chartGrid.drawableZone)
    }

    private fun buildBars(value: Int, spacing: Float) {
        val bounds = chartGrid.drawableZone.copyProps()

        var lastBarX = bounds.coordinate.x + spacing

        for(bar in data.getBarChartItems()) {
            bar.length = mapRange(
                bar.value.toString().toFloat(),
                0f,
                chartGrid.getGridValueY(value).maxY.toString().toFloat(),
                0f,
                bounds.dimension.height)
            bar.thickness = (bounds.dimension.width / data.getBarChartItems().size - 1) - (spacing)
            bar.x = lastBarX
            bar.y = (bounds.coordinate.y + bounds.dimension.height) - bar.length
            bar.build()
            lastBarX += ((bounds.dimension.width) / (data.getBarChartItems().size)) - ((spacing) / data.getBarChartItems().size)
            bar.backgroundOptions.height = bounds.dimension.height
            bar.backgroundOptions.y = bounds.coordinate.y
        }

        view.onFullyVisible = {
            if (!barsRevealed) {
                barsRevealed = true
                barRevealAnimation?.animate(view, data.getBarChartItems())
            }
        }
    }

    override fun onTouch(event: MotionEvent, x: Float, y: Float, shapeRenderer: ShapeRenderer) {
        interceptor.onTouch(event, x, y, shapeRenderer)
    }

    override fun onLongPressed(event: MotionEvent, x: Float, y: Float) {

    }
}