package com.eudycontreras.chartasticlibrary.charts.chartGrids

import android.graphics.Paint
import android.graphics.Typeface
import com.eudycontreras.chartasticlibrary.charts.chartModels.barChart.BarChartData
import com.eudycontreras.chartasticlibrary.charts.chartText.ChartText
import com.eudycontreras.chartasticlibrary.charts.data.DataTableValue
import com.eudycontreras.chartasticlibrary.extensions.roundToNearest
import com.eudycontreras.chartasticlibrary.extensions.sp
import com.eudycontreras.chartasticlibrary.properties.Bounds
import com.eudycontreras.chartasticlibrary.properties.Color

/**
 * Created by eudycontreras.
 */

class ChartGridValues(
    private val paint: Paint,
    private val data: BarChartData,
    private val grid: ChartGrid
) {

    enum class Label(var value: Int) {
        TOP(0),
        LEFT(1),
        BOTTOM(2),
        RIGHT(3)
    }

    var typeFace: Typeface =  Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)

    var textColor = Color.White

    var textSize: Float = 9.sp

    var prepend: String = "$"
    var append: String = ""

    private fun buildText(
        bounds: Bounds,
        paddingLeft: Float,
        paddingRight: Float,
        paddingY: Float,
        pointCount: Int
    ): Pair<List<ChartText>, Float>{

        val values = data.valueY
        val type = data.valueTypeY

        val valuesY = createValues(values, type, pointCount)

        val chartTexts = mutableListOf<ChartText>()

        var margin = 0f

        valuesY?.let {
            val points = it.second
            val highestValue = it.third

            var text = "$prepend$highestValue$append"

            val reference = ChartText(text, paint)
            reference.textColor = textColor
            reference.textSize = textSize
            reference.typeFace = typeFace
            reference.build()

            reference.x = (bounds.coordinate.x + paddingLeft) + reference.dimension.width
            reference.y = (bounds.coordinate.y + paddingY) + (reference.dimension.height + grid.getBorderThickness())

            margin = reference.x + paddingRight

            val top = reference.y
            val bottom = (bounds.coordinate.y + bounds.dimension.height) - (paddingY + grid.getBorderThickness())

            val height: Float = (bottom - top)

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

        return Pair(chartTexts, margin)
    }

    private fun createValues(valueY: List<DataTableValue>, type: Any, pointCount: Int): Triple<List<Any>,List<String>,Any>? {
        val points = mutableListOf<String>()

        return when(type) {
            is Float.Companion -> {
                val pointValues = mutableListOf<Float>()
                val values = valueY.map { it.value.toFloat()}.sortedByDescending { it }
                val max = values.max()!!.roundToNearest()

                for (count in 0 until pointCount) {
                    val value = max / pointCount.toFloat()
                    val point = value * count.toFloat()
                    pointValues.add(point)
                }

                points.addAll(pointValues.sortedByDescending { it }.map { it.toString() })

                return Triple<List<Any>,List<String>,Any>(values, points, max)
            }
            is Int.Companion -> {
                val pointValues = mutableListOf<Int>()
                val values = valueY.map { it.value.toInt()}.sortedByDescending { it }
                val max = values.max()!!.roundToNearest().toFloat()

                for (count in 0 until pointCount) {
                    val value = max / pointCount.toFloat()
                    val point = value * count.toFloat()
                    pointValues.add(point.toInt())
                }

                points.addAll(pointValues.sortedByDescending { it }.map { it.toString() })

                return Triple<List<Any>,List<String>,Any>(values, points, max.toInt())
            }
            is Double.Companion -> {
                val pointValues = mutableListOf<Double>()
                val values = valueY.map { it.value.toDouble()}.sortedByDescending { it }
                val max = values.max()!!.roundToNearest()

                for (count in 0 until pointCount) {
                    val value = max / pointCount.toFloat()
                    val point = value * count.toDouble()
                    pointValues.add(point)
                }

                points.addAll(pointValues.sortedByDescending { it }.map { it.toString() })

                return Triple<List<Any>,List<String>,Any>(values, points, max)
            }
            else -> null
        }
    }
}