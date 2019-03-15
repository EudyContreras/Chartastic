package com.eudycontreras.chartasticlibrary.charts.chart_grid

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Typeface
import android.util.Log
import com.eudycontreras.chartasticlibrary.Shape
import com.eudycontreras.chartasticlibrary.ShapeRenderer
import com.eudycontreras.chartasticlibrary.charts.ChartBoundsManager
import com.eudycontreras.chartasticlibrary.charts.ChartElement
import com.eudycontreras.chartasticlibrary.charts.chart_data.DataTableValue
import com.eudycontreras.chartasticlibrary.charts.chart_model.bar_chart.BarChart
import com.eudycontreras.chartasticlibrary.charts.chart_options.AxisYOptions
import com.eudycontreras.chartasticlibrary.charts.chart_text.ChartText
import com.eudycontreras.chartasticlibrary.properties.Bounds
import com.eudycontreras.chartasticlibrary.properties.MutableColor
import com.eudycontreras.chartasticlibrary.properties.Padding
import com.eudycontreras.chartasticlibrary.shapes.BoundingBox
import com.eudycontreras.chartasticlibrary.shapes.Line
import com.eudycontreras.chartasticlibrary.utilities.extensions.dp
import com.eudycontreras.chartasticlibrary.utilities.extensions.roundToNearest
import com.eudycontreras.chartasticlibrary.utilities.extensions.sp

/**
 * Created by eudycontreras.
 */

class ChartGridAxisY(
    private val barChart: BarChart,
    private val boundsManager: ChartBoundsManager,
    _type: Type = Type.LEFT
) : ChartBoundsManager.ChartBoundsOwner, ChartElement{

    enum class Type {
        LEFT,
        RIGHT
    }

    companion object {
        val DEFAULT_PADDING = Padding(2.dp, 6.dp, 0.dp, 0.dp)
        val DEFAULT_LABEL_TEXT_SIZE = 9.sp
        val DEFAULT_LABEL_TYPEFACE: Typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
        val DEFAULT_LABEL_COLOR = MutableColor.rgba(255, 255, 255, 1f)
    }

    override var render: Boolean = true

    override var computeBounds: Boolean = true
        set(value) {
            field = value
            if (!value) {
                boundsManager.removeBoundsOwner(this)
            } else {
                boundsManager.addBoundsOwner(this)
            }
        }

    override var drawBounds: Boolean = false

    override val bounds: Bounds = Bounds(this)

    override val anchor: ChartBoundsManager.BoundsAnchor
        get() {
            return when (type) {
                Type.LEFT -> ChartBoundsManager.BoundsAnchor.LEFT
                Type.RIGHT -> ChartBoundsManager.BoundsAnchor.RIGHT
            }
        }

    var type: Type = _type
        set(value) {
            boundsManager.removeBoundsOwner(this)
            field = value
            boundsManager.addBoundsOwner(this)
        }

    private var tickLines = ArrayList<Line>()

    private var values = Triple<List<ChartText>, Float, Float>(ArrayList(), 0f, 0f)

    private val boundsBox: Shape by lazy {
        BoundingBox().apply {
            this.bounds.update(this@ChartGridAxisY.bounds.drawableArea)
        }
    }
    private var axisLabelBounds: Bounds = Bounds()

    var options: AxisYOptions = AxisYOptions()

    lateinit var maxY: Any
    lateinit var minY: Any

    init {
        boundsManager.addBoundsOwner(this)
    }

    fun build(bounds: Bounds = Bounds()) {
        options.addPropertyChangeListener { oldValue, newValue, name ->
            when (type) {
                Type.LEFT -> {
                    buildLeft(this.bounds.drawableArea, options.valuePointCount)
                    buildLeftTicks(values.first)
                }
                Type.RIGHT -> {
                    buildRight(this.bounds.drawableArea, options.valuePointCount)
                    buildRightTicks(values.first)
                }
            }
            this.bounds.update(axisLabelBounds)
            Log.d("Options", "old value: $oldValue new value: $newValue name: $name")
        }
        when (type) {
            Type.LEFT -> {
                buildLeft(this.bounds.drawableArea, options.valuePointCount)
                buildLeftTicks(values.first)
            }
            Type.RIGHT -> {
                buildRight(this.bounds.drawableArea, options.valuePointCount)
                buildRightTicks(values.first)
            }
        }
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
        when (type) {
            Type.LEFT -> {
                buildLeft(this.bounds.drawableArea, options.valuePointCount)
                buildLeftTicks(values.first)
            }
            Type.RIGHT -> {
                buildRight(this.bounds.drawableArea, options.valuePointCount)
                buildRightTicks(values.first)
            }
        }
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

        values.first.forEach { it.render(path, paint, canvas, renderingProperties) }

        if (options.showTickLines) {
            tickLines.forEach { it.render(path, paint, canvas, renderingProperties) }
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

            var text = "${options.labelValuePrepend}${points[0]}${options.labelValueAppend}"

            val reference = ChartText(text, paint)
            reference.alignment = ChartText.Alignment.RIGHT
            reference.textColor = options.labelTextColor
            reference.textSize = options.labelTextSize
            reference.typeFace = options.labelTypeFace
            reference.build()

            reference.x = (bounds.coordinate.x + options.padding.start) + reference.dimension.width
            reference.y = (bounds.coordinate.y + grid.getBorderThickness(ChartGrid.Border.TOP)) + (reference.dimension.height + options.padding.top)

            margin = reference.x + (options.padding.end + TickOptions.tickLength)

            val top = reference.y
            val bottom = (bounds.bottom - (grid.getBorderThickness(ChartGrid.Border.BOTTOM))) - options.padding.bottom

            val height: Float = (bottom - top)

            axisLabelBounds.coordinate.x = bounds.coordinate.x
            axisLabelBounds.coordinate.y = (bounds.coordinate.y + grid.getBorderThickness(ChartGrid.Border.TOP))
            axisLabelBounds.dimension.height = (bottom) - axisLabelBounds.coordinate.y
            axisLabelBounds.dimension.width = reference.dimension.width + (options.padding.end + TickOptions.tickLength) + options.padding.start

            points = points.drop(1)

            val increase = height / points.count().toFloat()

            chartTexts.add(reference)

            var offset = increase

            for (value in points) {

                text = "${options.labelValuePrepend}$value${options.labelValueAppend}"

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

    private fun buildLeftTicks(chartTexts: List<ChartText>) {

        tickLines.clear()

        val x = bounds.right - TickOptions.tickLength

        for (text in chartTexts) {

            val y = (text.y - (text.dimension.height / 2f)) - TickOptions.tickWidth / 2f

            val tick = Line()
            tick.color.setColor(TickOptions.tickColor)
            tick.elevation = 0f
            tick.drawShadow = false
            tick.render = options.showTickLines
            tick.coordinate.x = x
            tick.coordinate.y = y
            tick.dimension.width = TickOptions.tickLength
            tick.dimension.height = TickOptions.tickWidth

            tickLines.add(tick)
        }

        val line = Line()

        val first = tickLines.first()
        val last = tickLines.last()

        line.color.setColor(TickOptions.tickColor)
        line.elevation = TickOptions.elevation
        line.render = options.showTickLineBar
        line.coordinate.x = x + TickOptions.tickLength
        line.coordinate.y = first.top
        line.dimension.width = TickOptions.tickWidth
        line.dimension.height = last.bottom - first.top

        tickLines.add(line)
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

            var text = "${options.labelValuePrepend}${points[0]}${options.labelValueAppend}"

            val reference = ChartText(text, paint)
            reference.alignment = ChartText.Alignment.LEFT
            reference.textColor = options.labelTextColor
            reference.textSize = options.labelTextSize
            reference.typeFace = options.labelTypeFace
            reference.build()

            reference.x = (bounds.coordinate.x + bounds.dimension.width) - (reference.dimension.width + options.padding.end)
            reference.y = (bounds.coordinate.y + grid.getBorderThickness(ChartGrid.Border.TOP)) + (reference.dimension.height + options.padding.top)

            margin = (reference.x - (options.padding.start + TickOptions.tickLength)) - grid.getBorderThickness(ChartGrid.Border.RIGHT)

            val top = reference.y
            val bottom = (bounds.coordinate.y + bounds.dimension.height) - (grid.getBorderThickness(ChartGrid.Border.BOTTOM))

            val height: Float = (bottom - top)

            axisLabelBounds.coordinate.x = reference.x - (options.padding.start + TickOptions.tickLength)
            axisLabelBounds.coordinate.y = (bounds.coordinate.y + grid.getBorderThickness(ChartGrid.Border.TOP))
            axisLabelBounds.dimension.height = (bottom) - axisLabelBounds.coordinate.y
            axisLabelBounds.dimension.width = reference.dimension.width + (options.padding.start + TickOptions.tickLength) + options.padding.end

            points = points.drop(1)

            val increase = height / points.count().toFloat()

            chartTexts.add(reference)

            var offset = increase

            for (value in points) {

                text = "${options.labelValuePrepend}$value${options.labelValueAppend}"

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

    private fun buildRightTicks(chartTexts: List<ChartText>) {

        tickLines.clear()

        val x = bounds.left

        for (text in chartTexts) {
            val y = (text.y - (text.dimension.height / 2f)) - TickOptions.tickWidth / 2f

            val tick = Line()
            tick.color.setColor(TickOptions.tickColor)
            tick.elevation = TickOptions.elevation
            tick.render = options.showTickLines
            tick.coordinate.x = x
            tick.coordinate.y = y
            tick.dimension.width = TickOptions.tickLength
            tick.dimension.height = TickOptions.tickWidth

            tickLines.add(tick)
        }

        val line = Line()

        val first = tickLines.first()
        val last = tickLines.last()

        line.color.setColor(TickOptions.tickColor)
        line.elevation = 0f
        line.drawShadow = false
        line.render = options.showTickLineBar
        line.coordinate.x = x - TickOptions.tickWidth
        line.coordinate.y = first.top
        line.dimension.width = TickOptions.tickWidth
        line.dimension.height = last.bottom - first.top

        tickLines.add(line)
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

    object TickOptions {
        var tickWidth = 1.dp
        var tickLength = 8.dp
        var elevation = 0.dp
        var tickColor= MutableColor.rgb(255)
    }
}