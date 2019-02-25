package com.eudycontreras.chartasticlibrary.charts.chartGrids

import android.graphics.Paint
import android.graphics.Typeface
import com.eudycontreras.chartasticlibrary.charts.chartModels.barChart.BarChartData
import com.eudycontreras.chartasticlibrary.charts.chartText.ChartText
import com.eudycontreras.chartasticlibrary.charts.data.DataTableValue
import com.eudycontreras.chartasticlibrary.extensions.dp
import com.eudycontreras.chartasticlibrary.extensions.roundToNearest
import com.eudycontreras.chartasticlibrary.extensions.sp
import com.eudycontreras.chartasticlibrary.properties.Bounds
import com.eudycontreras.chartasticlibrary.properties.Color
import com.eudycontreras.chartasticlibrary.shapes.BoundingBox

/**
 * Created by eudycontreras.
 */

class ChartGridAxisY(
    private val paint: Paint,
    private val type: Int,
    private val grid: ChartGrid
) {

    companion object {
        const val LEFT = 0
        const val RIGHT = 1
    }

    private var values = Triple<List<ChartText>, Float, Float>(ArrayList(), 0f, 0f)

    var typeFace: Typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)

    var textColor = Color.White

    var valuesYBounds: Bounds = Bounds()

    var textSize: Float = 9.sp

    var prepend: String = ""
    var append: String = ""

    var paddingLeft = 0.dp
    var paddingRight = 0.dp
    var paddingTop = 0.dp

    lateinit var maxY: Any
    lateinit var minY: Any

    fun build(
        data: BarChartData,
        bounds: Bounds,
        pointCount: Int
    ){
       when (type) {
           LEFT -> buildLeft(data, bounds, pointCount)
           RIGHT -> buildRight(data, bounds, pointCount)
       }
    }

    private fun buildLeft(data:BarChartData, bounds: Bounds, pointCount: Int) {
        val values = data.valueY
        val type = data.valueTypeY

        val valuesY = createValues(values, type, pointCount)

        val chartTexts = mutableListOf<ChartText>()

        var margin = 0f

        valuesY?.let {
            var points = it.second
            val range = it.third
            maxY = range.first
            minY = range.second

            var text = "$prepend${points[0]}$append"

            val reference = ChartText(text, paint)
            reference.alignment = ChartText.Alignment.RIGHT
            reference.textColor = textColor
            reference.textSize = textSize
            reference.typeFace = typeFace
            reference.build()

            reference.x = (bounds.coordinate.x + paddingLeft) + reference.dimension.width
            reference.y = (bounds.coordinate.y + grid.getBorderThickness(ChartGrid.Border.TOP)) + paddingTop

            margin = reference.x + paddingRight

            val top = reference.y
            val bottom = (bounds.coordinate.y + bounds.dimension.height) - (grid.getBorderThickness(ChartGrid.Border.BOTTOM))

            val height: Float = (bottom - top)

            valuesYBounds.coordinate.x = bounds.coordinate.x
            valuesYBounds.coordinate.y = (bounds.coordinate.y + grid.getBorderThickness(ChartGrid.Border.TOP))
            valuesYBounds.dimension.height = (bottom) - valuesYBounds.coordinate.y
            valuesYBounds.dimension.width = reference.dimension.width + paddingRight + paddingLeft

            points = points.drop(1)

            val increase = height / points.count().toFloat()

            chartTexts.add(reference)

            var offset = increase

            for (value in points) {

                text = "$prepend$value$append"

                paint.reset()

                val chartText = ChartText(text, paint)
                chartText.copyStyle(reference)
                chartText.build()

                chartText.x = reference.x
                chartText.y = top + offset

                offset += increase

                chartTexts.add(chartText)
            }
        }
        this.values = Triple(chartTexts, margin, bounds.dimension.width)
    }

    private fun buildRight(data: BarChartData, bounds: Bounds, pointCount: Int) {
        val values = data.valueY
        val type = data.valueTypeY

        val valuesY = createValues(values, type, pointCount)

        val chartTexts = mutableListOf<ChartText>()

        var margin = 0f

        valuesY?.let {
            var points = it.second
            val range = it.third
            maxY = range.first
            minY = range.second
            var text = "$prepend${points[0]}$append"

            val reference = ChartText(text, paint)
            reference.alignment = ChartText.Alignment.LEFT
            reference.textColor = textColor
            reference.textSize = textSize
            reference.typeFace = typeFace
            reference.build()

            reference.x = (bounds.coordinate.x + bounds.dimension.width) - (reference.dimension.width + paddingRight)
            reference.y = (bounds.coordinate.y + grid.getBorderThickness(ChartGrid.Border.TOP)) + paddingTop

            margin = (reference.x - paddingLeft) - grid.getBorderThickness(ChartGrid.Border.RIGHT)

            val top = reference.y
            val bottom = (bounds.coordinate.y + bounds.dimension.height) - (grid.getBorderThickness(ChartGrid.Border.BOTTOM))

            val height: Float = (bottom - top)

            valuesYBounds.coordinate.x = reference.x - paddingLeft
            valuesYBounds.coordinate.y = (bounds.coordinate.y + grid.getBorderThickness(ChartGrid.Border.TOP))
            valuesYBounds.dimension.height = (bottom) - valuesYBounds.coordinate.y
            valuesYBounds.dimension.width = reference.dimension.width + paddingLeft + paddingRight

            points = points.drop(1)

            val increase = height / points.count().toFloat()

            chartTexts.add(reference)

            var offset = increase

            for (value in points) {

                text = "$prepend$value$append"

                paint.reset()

                val chartText = ChartText(text, paint)
                chartText.copyStyle(reference)
                chartText.build()

                chartText.x = reference.x
                chartText.y = top + offset

                offset += increase

                chartTexts.add(chartText)
            }
        }
        this.values = Triple(chartTexts, bounds.coordinate.x, margin)
    }

    private fun createValues(valueY: List<DataTableValue>, type: Any, pointCount: Int): Triple<List<Any>,List<String>,Pair<Any,Any>>? {
        val points = mutableListOf<String>()

        return when(type) {
            is Float.Companion -> {
                val pointValues = mutableListOf<Float>()
                val values = valueY.map { it.value.toFloat()}.sortedByDescending { it }
                val max = values.max()!!
                val min = values.min()!!

                val rMax = max.roundToNearest()

                for (count in 0 until pointCount) {
                    val value = rMax / pointCount.toFloat()
                    val point = value * count.toFloat()
                    pointValues.add(point.roundToNearest())
                }
                pointValues.add(rMax)

                points.addAll(pointValues.sortedByDescending { it }.map { it.toString() })

                return Triple<List<Any>,List<String>,Pair<Any,Any>>(values, points, Pair(max,min))
            }
            is Int.Companion -> {
                val pointValues = mutableListOf<Int>()
                val values = valueY.map { it.value.toInt()}.sortedByDescending { it }
                val max = values.max()!!
                val min = values.min()!!

                val rMax = max.roundToNearest()

                for (count in 0 until pointCount) {
                    val value = rMax / pointCount.toFloat()
                    val point = value * count.toFloat()
                    pointValues.add(point.toInt().roundToNearest())
                }
                pointValues.add(rMax)

                points.addAll(pointValues.sortedByDescending { it }.map { it.toString() })

                return Triple<List<Any>,List<String>,Pair<Any,Any>>(values, points, Pair(max,min))
            }
            is Double.Companion -> {
                val pointValues = mutableListOf<Double>()
                val values = valueY.map { it.value.toDouble()}.sortedByDescending { it }
                val max = values.max()!!
                val min = values.min()!!

                val rMax = max.roundToNearest()

                for (count in 0 until pointCount) {
                    val value = rMax / pointCount.toFloat()
                    val point = value * count.toDouble()
                    pointValues.add(point.roundToNearest())
                }
                pointValues.add(rMax)

                points.addAll(pointValues.sortedByDescending { it }.map { it.toString() })

                return Triple<List<Any>,List<String>,Pair<Any,Any>>(values, points, Pair(max,min))
            }
            else -> null
        }
    }

    fun getValues() = values

    fun getElements() = values.first

    fun getBoundingBox() = BoundingBox().apply {
        coordinate = valuesYBounds.coordinate
        dimension = valuesYBounds.dimension
    }

    fun showText(value: Boolean) {
        values.first.forEach { it.render = value }
    }
}