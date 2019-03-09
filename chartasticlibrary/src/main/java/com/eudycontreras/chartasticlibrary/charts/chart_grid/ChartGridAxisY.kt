package com.eudycontreras.chartasticlibrary.charts.chart_grid

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Typeface
import com.eudycontreras.chartasticlibrary.Shape
import com.eudycontreras.chartasticlibrary.ShapeRenderer
import com.eudycontreras.chartasticlibrary.charts.ChartBoundsManager
import com.eudycontreras.chartasticlibrary.charts.ChartElement
import com.eudycontreras.chartasticlibrary.charts.chart_data.DataTableValue
import com.eudycontreras.chartasticlibrary.charts.chart_model.bar_chart.BarChart
import com.eudycontreras.chartasticlibrary.charts.chart_model.bar_chart.BarChartData
import com.eudycontreras.chartasticlibrary.charts.chart_text.ChartText
import com.eudycontreras.chartasticlibrary.properties.Bounds
import com.eudycontreras.chartasticlibrary.properties.MutableColor
import com.eudycontreras.chartasticlibrary.shapes.BoundingBox
import com.eudycontreras.chartasticlibrary.utilities.extensions.dp
import com.eudycontreras.chartasticlibrary.utilities.extensions.roundToNearest
import com.eudycontreras.chartasticlibrary.utilities.extensions.sp

/**
 * Created by eudycontreras.
 */

class ChartGridAxisY(
    private val barChart: BarChart,
    private val boundsManager: ChartBoundsManager
) : ChartBoundsManager.ChartBoundsOwner, ChartElement{

    companion object {
        const val LEFT = 0
        const val RIGHT = 1
    }

    override var render: Boolean = true

    override var computeBounds: Boolean = true

    override var drawBounds: Boolean = true

    override val anchor: ChartBoundsManager.BoundsAnchor
        get() {
            return when (type) {
                LEFT -> ChartBoundsManager.BoundsAnchor.LEFT
                RIGHT -> ChartBoundsManager.BoundsAnchor.RIGHT
                else ->  ChartBoundsManager.BoundsAnchor.LEFT
            }
        }

    override val bounds: Bounds = Bounds(this)

    private var type: Int = LEFT
        set(value) {
            boundsManager.removeBoundsOwner(this)
            field = value
            boundsManager.addBoundsOwner(this)
        }

    private val boundsBox: Shape by lazy {
        BoundingBox().apply {
            this.bounds.update(this@ChartGridAxisY.bounds.drawableArea)
        }
    }

    private var values = Triple<List<ChartText>, Float, Float>(ArrayList(), 0f, 0f)

    var typeFace: Typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)

    var textColor = MutableColor.rgb(255, 255, 255)

    var valuesYBounds: Bounds = Bounds()

    var textSize: Float = 9.sp

    var prepend: String = ""
    var append: String = ""

    var paddingLeft = 0.dp
    var paddingRight = 0.dp
    var paddingTop = 0.dp

    lateinit var maxY: Any
    lateinit var minY: Any

    init {
        boundsManager.addBoundsOwner(this)
        build()
    }

    override fun build(bounds: Bounds) {
        this.bounds.update(bounds)
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
    }

    override fun render(
        path: Path,
        paint: Paint,
        canvas: Canvas,
        renderingProperties: ShapeRenderer.RenderingProperties
    ) {
        if (drawBounds) {
            boundsBox.render(path, paint, canvas, renderingProperties)
        }
    }

    fun build(
        data: BarChartData,
        bounds: Bounds,
        pointCount: Int
    ) {
        when (type) {
            LEFT -> buildLeft(bounds, pointCount)
            RIGHT -> buildRight(bounds, pointCount)
        }
    }

    private fun buildLeft(bounds: Bounds, pointCount: Int) {
        val grid = barChart.chartGridPlotArea.chartGrid
        val data = barChart.data

        val values = data.valueY
        val type = data.valueTypeY

        val valuesY = createValues(values, type, pointCount)

        val chartTexts = mutableListOf<ChartText>()

        var margin = 0f

        val paint = Paint()

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
            reference.y = (bounds.coordinate.y + grid.getBorderThickness(ChartGrid.Border.TOP)) + reference.dimension.height

            margin = reference.x + paddingRight

            val top = reference.y
            val bottom =
                (bounds.coordinate.y + bounds.dimension.height) - (grid.getBorderThickness(ChartGrid.Border.BOTTOM))

            val height: Float = (bottom - top)

            valuesYBounds.coordinate.x = bounds.coordinate.x
            valuesYBounds.coordinate.y = (bounds.coordinate.y + grid.getBorderThickness(ChartGrid.Border.TOP))
            valuesYBounds.dimension.height = (bottom) - valuesYBounds.coordinate.y
            valuesYBounds.dimension.width = reference.dimension.width + paddingRight + paddingLeft

            points = points.drop(1)

            val increase = height / points.count().toFloat()

            chartTexts.clear()
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

    private fun buildRight(bounds: Bounds, pointCount: Int) {
        val grid = barChart.chartGridPlotArea.chartGrid
        val data = barChart.data

        val values = data.valueY
        val type = data.valueTypeY

        val valuesY = createValues(values, type, pointCount)

        val chartTexts = mutableListOf<ChartText>()

        var margin = 0f

        val paint = Paint()

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

    private fun createValues(
        valueY: List<DataTableValue>,
        type: Any,
        pointCount: Int
    ): Triple<List<Any>, List<String>, Pair<Any, Any>>? {
        val points = mutableListOf<String>()

        return when (type) {
            is Float.Companion -> {
                val pointValues = mutableListOf<Float>()
                val values = valueY.map { it.value.toFloat() }.sortedByDescending { it }
                val max = values.max()!!.roundToNearest()
                val min = values.min()!!

                for (count in 0 until pointCount) {
                    val value = max / pointCount.toFloat()
                    val point = value * count.toFloat()
                    pointValues.add(point.roundToNearest())
                }
                pointValues.add(max)

                points.addAll(pointValues.sortedByDescending { it }.map { it.toString() })

                return Triple<List<Any>, List<String>, Pair<Any, Any>>(values, points, Pair(max, min))
            }
            is Int.Companion -> {
                val pointValues = mutableListOf<Int>()
                val values = valueY.map { it.value.toInt() }.sortedByDescending { it }
                val max = values.max()!!.roundToNearest()
                val min = values.min()!!

                for (count in 0 until pointCount) {
                    val value = max / pointCount.toFloat()
                    val point = value * count.toFloat()
                    pointValues.add(point.toInt().roundToNearest())
                }
                pointValues.add(max)

                points.addAll(pointValues.sortedByDescending { it }.map { it.toString() })

                return Triple<List<Any>, List<String>, Pair<Any, Any>>(values, points, Pair(max, min))
            }
            is Double.Companion -> {
                val pointValues = mutableListOf<Double>()
                val values = valueY.map { it.value.toDouble() }.sortedByDescending { it }
                val max = values.max()!!.roundToNearest()
                val min = values.min()!!

                for (count in 0 until pointCount) {
                    val value = max / pointCount.toFloat()
                    val point = value * count.toDouble()
                    pointValues.add(point.roundToNearest())
                }
                pointValues.add(max)

                points.addAll(pointValues.sortedByDescending { it }.map { it.toString() })

                return Triple<List<Any>, List<String>, Pair<Any, Any>>(values, points, Pair(max, min))
            }
            else -> null
        }
    }

    fun getValues() = values

    fun getElements() = values.first

    fun showText(value: Boolean) {
        values.first.forEach { it.render = value }
    }
}