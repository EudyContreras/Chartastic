package com.eudycontreras.chartasticlibrary.charts.chartModels.barChart

import android.content.Context
import androidx.core.content.ContextCompat
import com.eudycontreras.chartasticlibrary.R
import com.eudycontreras.chartasticlibrary.Shape
import com.eudycontreras.chartasticlibrary.charts.Chart
import com.eudycontreras.chartasticlibrary.charts.ChartElement
import com.eudycontreras.chartasticlibrary.charts.chartGrids.ChartGrid
import com.eudycontreras.chartasticlibrary.charts.chartGrids.ChartGridAxisY
import com.eudycontreras.chartasticlibrary.extensions.dp
import com.eudycontreras.chartasticlibrary.global.mapRange
import com.eudycontreras.chartasticlibrary.properties.Bounds
import com.eudycontreras.chartasticlibrary.properties.Color
import com.eudycontreras.chartasticlibrary.properties.Coordinate
import com.eudycontreras.chartasticlibrary.properties.Dimension
import com.eudycontreras.chartasticlibrary.shapes.Rectangle

/**
 * Created by eudycontreras.
 */
class BarChart(private val context: Context, private var data: BarChartData) : Chart() {

    private val chartGrid: ChartGrid = ChartGrid(context)
    private val rectangle: Rectangle = Rectangle()

    private val mShapes = ArrayList<Shape>()
    private val mElements = ArrayList<ChartElement>()

    private val mYValue = ChartGridAxisY.RIGHT

    override fun build(bounds: Bounds) {

        val widthMultiplier = 1f
        val heightMultiplier = 1f

        val rectBounds = setBackground(
            x = (bounds.dimension.width / 2) - ((bounds.dimension.width / 2) * widthMultiplier),
            y = (bounds.dimension.height / 2) - ((bounds.dimension.height / 2) * heightMultiplier),
            width = (bounds.dimension.width * widthMultiplier),
            height = bounds.dimension.height * heightMultiplier
        )

        buildGrid(16.dp, rectBounds.subtract(10.dp))

        if (data.getBarChartItems().isEmpty()) {

        }

        buildBars(mYValue)
    }

    override fun getBackground(): Shape {
        return rectangle
    }

    override fun getShapes(): List<Shape> {
        if (mShapes.isEmpty()) {
            mShapes.addAll(chartGrid.getLines())
            mShapes.addAll(data.getBarChartItems().flatMap { it.getShapes() })
            mShapes.addAll(chartGrid.getBorders())
            //mShapes.add(chartGrid.getBoundingBox(mYValue))
            //mShapes.add(chartGrid.getBoundingBox())
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
        rectangle.drawShadow = true
        rectangle.showStroke = false
        rectangle.elevation = 10.dp
        rectangle.coordinate = Coordinate(x, y)
        rectangle.dimension = Dimension(width, height)
        rectangle.color = Color(ContextCompat.getColor(context, R.color.colorPrimary))
        rectangle.strokeColor = Color.Blue
        rectangle.strokeWidth = 1.dp

        return rectangle.getBounds()
    }

    private fun buildGrid(padding: Float, bounds: Bounds) {
        chartGrid.valueYPointCount = 16
        chartGrid.pointLineColor = Color.rgba(255, 255, 255, 0.35f)
        chartGrid.pointLineThickness = 0.8f.dp
        chartGrid.bounds = bounds
        chartGrid.setDataSource(data)
        chartGrid.setBorderColor(Color.White, ChartGrid.Border.ALL)
        chartGrid.showBorder(false, ChartGrid.Border.TOP)
        chartGrid.showBorder(false, ChartGrid.Border.RIGHT)
        chartGrid.showBorder(false, ChartGrid.Border.LEFT)
        chartGrid.showBorder(true, ChartGrid.Border.BOTTOM)
        chartGrid.setBorderElevation(4.dp, ChartGrid.Border.ALL)
        chartGrid.setBorderThickness(6.dp, ChartGrid.Border.ALL)
        chartGrid.showGridLines(true)
        chartGrid.showYTextValues(true, ChartGridAxisY.LEFT)
        chartGrid.showYValues(bounds, padding, 6.dp, 6.dp, mYValue, " LOC")
    }

    private fun buildBars(value: Int) {
        val spacing = 16.dp
        val bounds = chartGrid.drawableZone

        var lastBarX = bounds.coordinate.x + spacing

        for(bar in data.getBarChartItems()) {
            bar.length = mapRange(
                bar.value.toString().toFloat(),
                0f,
                chartGrid.getGridValueY(value).maxY.toString().toFloat(),
                0f,
                bounds.dimension.height)
            bar.thickness = (bounds.dimension.width / data.getBarChartItems().size) - spacing
            bar.x = lastBarX
            bar.y = (bounds.coordinate.y + bounds.dimension.height) - bar.length
            bar.build()
            lastBarX += (bounds.dimension.width / data.getBarChartItems().size) - (spacing / data.getBarChartItems().size)

            bar.backgroundOptions.height = bounds.dimension.height
            bar.backgroundOptions.y = bounds.coordinate.y
        }
    }
}