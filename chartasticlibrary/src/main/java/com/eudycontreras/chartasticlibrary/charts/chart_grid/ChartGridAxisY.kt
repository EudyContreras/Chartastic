package com.eudycontreras.chartasticlibrary.charts.chart_grid

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Typeface
import com.eudycontreras.chartasticlibrary.Shape
import com.eudycontreras.chartasticlibrary.ShapeRenderer
import com.eudycontreras.chartasticlibrary.charts.ChartElement
import com.eudycontreras.chartasticlibrary.charts.ChartLayoutManager
import com.eudycontreras.chartasticlibrary.charts.chart_data.DataTableValue
import com.eudycontreras.chartasticlibrary.charts.chart_options.AxisYOptions
import com.eudycontreras.chartasticlibrary.charts.chart_text.ChartText
import com.eudycontreras.chartasticlibrary.charts.interfaces.ChartBoundsOwner
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
    private val layoutManager: ChartLayoutManager,
    _type: Type = Type.LEFT
) : ChartBoundsOwner, ChartElement{

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
                layoutManager.removeBoundsOwner(this)
            } else {
                layoutManager.addBoundsOwner(this)
            }
        }

    override var drawBounds: Boolean = false

    override val bounds: Bounds = Bounds(this)

    override val anchor: ChartLayoutManager.BoundsAnchor
        get() {
            return when (type) {
                Type.LEFT -> ChartLayoutManager.BoundsAnchor.LEFT
                Type.RIGHT -> ChartLayoutManager.BoundsAnchor.RIGHT
            }
        }

    var type: Type = _type
        set(value) {
            layoutManager.removeBoundsOwner(this)
            field = value
            layoutManager.addBoundsOwner(this)
        }

    private val boundsBox: Shape by lazy {
        BoundingBox().apply {
            this.bounds.update(this@ChartGridAxisY.bounds.drawableArea)
        }
    }

    private var tickLines = ArrayList<Line>()

    var values: ValueCalculation? = null

    var axisLabelBounds: Bounds = Bounds()

    var options: AxisYOptions = AxisYOptions()

    init {
        layoutManager.addBoundsOwner(this)
    }

    fun build(bounds: Bounds = Bounds()) {
        options.addPropertyChangeListener { _, _, _ ->
            when (type) {
                Type.LEFT -> {
                    buildLeft(bounds.drawableArea, options.valuePointCount)
                    values?.valuesBuildData?.let {
                        buildLeftTicks(it.textElements)
                    }
                }
                Type.RIGHT -> {
                    buildRight(bounds.drawableArea, options.valuePointCount)
                    values?.valuesBuildData?.let {
                        buildRightTicks(it.textElements)
                    }
                }
            }
            this.bounds.update(axisLabelBounds)
        }
        when (type) {
            Type.LEFT -> {
                buildLeft(bounds.drawableArea, options.valuePointCount)
                values?.valuesBuildData?.let {
                    buildLeftTicks(it.textElements)
                }
            }
            Type.RIGHT -> {
                buildRight(bounds.drawableArea, options.valuePointCount)
                values?.valuesBuildData?.let {
                    buildRightTicks(it.textElements)
                }
            }
        }
        this.bounds.update(axisLabelBounds)
    }

    override fun notifyBoundsChange(bounds: Bounds) {
        if(bounds != this.bounds) {
            this.bounds.update(bounds, false)
        }
        layoutManager.notifyBoundsChange(this)
    }

    override fun propagateNewBounds(bounds: Bounds) {
        this.bounds.update(bounds, false)
        when (type) {
            Type.LEFT -> {
                buildLeft(bounds.drawableArea, options.valuePointCount)
                values?.valuesBuildData?.let {
                    buildLeftTicks(it.textElements)
                }
            }
            Type.RIGHT -> {
                buildRight(bounds.drawableArea, options.valuePointCount)
                values?.valuesBuildData?.let {
                    buildRightTicks(it.textElements)
                }
            }
        }
        if (drawBounds) {
            boundsBox.bounds.update(axisLabelBounds.drawableArea, false)
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

        values?.valuesBuildData?.let { data ->
            data.textElements.forEach { it.render(path, paint, canvas, renderingProperties) }
        }

        if (options.showTickLines) {
            tickLines.forEach { it.render(path, paint, canvas, renderingProperties) }
        }
    }

    private fun buildLeft(bounds: Bounds, pointCount: Int) {
        if(options.chartData == null){
            return
        }
        val data = options.chartData!!

        val valuesY = computeValues(data.valueY, data.valueTypeY, pointCount)

        val chartTexts = mutableListOf<ChartText>()

        var zeroPoint = ChartText()

        val paint = Paint()

        var margin = 0f

        valuesY?.let {
            val referenceTextUpper = "${options.labelValuePrepend}${ it.maxValueRounded }${options.labelValueAppend}"
            val referenceTextLower = "${options.labelValuePrepend}${ it.minValueRounded }${options.labelValueAppend}"

            val referenceUpper = ChartText(referenceTextUpper, paint)
            referenceUpper.alignment = ChartText.Alignment.RIGHT
            referenceUpper.textColor = options.labelTextColor
            referenceUpper.textSize = options.labelTextSize
            referenceUpper.typeFace = options.labelTypeFace
            referenceUpper.build()

            referenceUpper.y = (bounds.top + (referenceUpper.dimension.height))

            val referenceLower = ChartText(referenceTextLower, paint)
            referenceLower.alignment = ChartText.Alignment.RIGHT
            referenceLower.textColor = options.labelTextColor
            referenceLower.textSize = options.labelTextSize
            referenceLower.typeFace = options.labelTypeFace
            referenceLower.build()

            referenceLower.y = (bounds.top + (referenceLower.dimension.height))

            val top = referenceUpper.y
            val bottom = bounds.bottom

            val height: Float = (bottom - top)

            val referenceUpperWidth = referenceUpper.dimension.width + (options.padding.end + options.tickLength) + options.padding.start
            val referenceLowerWidth = referenceLower.dimension.width + (options.padding.end + options.tickLength) + options.padding.start

            val reference = if (referenceUpperWidth > referenceLowerWidth) referenceUpper else referenceLower

            referenceUpper.x = (bounds.left + options.padding.start) + reference.dimension.width
            referenceLower.x = (bounds.left + options.padding.start) + reference.dimension.width

            margin = reference.x + (options.padding.end + options.tickLength)

            axisLabelBounds.coordinate.x = bounds.left
            axisLabelBounds.coordinate.y = bounds.top
            axisLabelBounds.dimension.height = (bottom) - axisLabelBounds.coordinate.y
            axisLabelBounds.dimension.width = if (referenceUpperWidth > referenceLowerWidth) referenceUpperWidth else referenceLowerWidth

            val increase = height / (it.upperPoints.size + it.lowerPoints.size).toFloat()

            chartTexts.add(referenceUpper)

            var offset = increase

            for (value in it.upperPoints) {

                paint.reset()

                val chartText = ChartText("${options.labelValuePrepend}$value${options.labelValueAppend}", paint)
                chartText.copyStyle(reference)
                chartText.build()

                chartText.x = reference.x
                chartText.y = top + offset

                offset += increase

                chartTexts.add(chartText)
            }

           zeroPoint = chartTexts[chartTexts.size - 1]

            for (value in it.lowerPoints) {

                paint.reset()

                val chartText = ChartText("${options.labelValuePrepend}$value${options.labelValueAppend}", paint)
                chartText.copyStyle(reference)
                chartText.build()

                chartText.x = reference.x
                chartText.y = top + offset

                offset += increase

                chartTexts.add(chartText)
            }
        }

        valuesY?.valuesBuildData = AxisBuildData(chartTexts, margin, axisLabelBounds.dimension.width).apply { this.zeroPoint = zeroPoint }
        values = valuesY
    }

    private fun buildLeftTicks(chartTexts: List<ChartText>) {

        if(chartTexts.isEmpty()) {
            return
        }
        tickLines.clear()

        val x = bounds.right - options.tickLength

        for (text in chartTexts) {

            val y = (text.y - (text.dimension.height / 2f)) - options.tickWidth / 2f

            val tick = Line()
            tick.color.setColor(options.tickColor)
            tick.elevation = 0f
            tick.drawShadow = false
            tick.render = options.showTickLines
            tick.coordinate.x = x
            tick.coordinate.y = y
            tick.dimension.width = options.tickLength
            tick.dimension.height = options.tickWidth

            tickLines.add(tick)
        }

        val line = Line()

        val first = tickLines.first()
        val last = tickLines.last()

        line.color.setColor(options.tickColor)
        line.elevation = options.tickElevation
        line.render = options.showTickLineBar
        line.coordinate.x = (x + options.tickLength) - options.tickWidth
        line.coordinate.y = first.top
        line.dimension.width = options.tickWidth
        line.dimension.height = last.bottom - first.top

        tickLines.add(line)
    }

    private fun buildRight(bounds: Bounds, pointCount: Int) {
        if(options.chartData == null){
            return
        }
        val data = options.chartData!!

        val valuesY = computeValues(data.valueY, data.valueTypeY, pointCount)

        val chartTexts = mutableListOf<ChartText>()

        var zeroPoint = ChartText()

        var margin = 0f

        val paint = Paint()

        valuesY?.let {
            val points = it.upperPoints

            var text = "${options.labelValuePrepend}${it.maxValueRounded}${options.labelValueAppend}"

            val reference = ChartText(text, paint)
            reference.alignment = ChartText.Alignment.LEFT
            reference.textColor = options.labelTextColor
            reference.textSize = options.labelTextSize
            reference.typeFace = options.labelTypeFace
            reference.build()

            reference.x = (bounds.coordinate.x + bounds.dimension.width) - (reference.dimension.width + options.padding.end)
            reference.y = (bounds.top + reference.dimension.height)

            margin = (reference.x - (options.padding.start + options.tickLength))

            val top = reference.y
            val bottom = bounds.bottom

            val height: Float = (bottom - top)

            axisLabelBounds.coordinate.x = reference.x - (options.padding.start + options.tickLength)
            axisLabelBounds.coordinate.y = bounds.top
            axisLabelBounds.dimension.height = (bottom) - axisLabelBounds.coordinate.y
            axisLabelBounds.dimension.width = reference.dimension.width + (options.padding.start + options.tickLength) + options.padding.end

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

            zeroPoint = chartTexts[chartTexts.size - 1]
        }

        valuesY?.valuesBuildData = AxisBuildData(chartTexts, margin, axisLabelBounds.dimension.width).apply { this.zeroPoint = zeroPoint }
        values = valuesY
    }

    private fun buildRightTicks(chartTexts: List<ChartText>) {

        if(chartTexts.isEmpty()) {
            return
        }
        tickLines.clear()

        val x = bounds.left

        for (text in chartTexts) {
            val y = (text.y - (text.dimension.height / 2f)) - options.tickWidth / 2f

            val tick = Line()
            tick.color.setColor(options.tickColor)
            tick.elevation = options.tickElevation
            tick.render = options.showTickLines
            tick.coordinate.x = x
            tick.coordinate.y = y
            tick.dimension.width = options.tickLength
            tick.dimension.height = options.tickWidth

            tickLines.add(tick)
        }

        val line = Line()

        val first = tickLines.first()
        val last = tickLines.last()

        line.color.setColor(options.tickColor)
        line.elevation = 0f
        line.drawShadow = false
        line.render = options.showTickLineBar
        line.coordinate.x = x
        line.coordinate.y = first.top
        line.dimension.width = options.tickWidth
        line.dimension.height = last.bottom - first.top

        tickLines.add(line)
    }

    private fun computeValues(
        valueY: List<DataTableValue>,
        type: Any,
        pointCount: Int
    ): ValueCalculation? {
        val pointsUpper = mutableListOf<String>()
        val pointsLower = mutableListOf<String>()

        return when (type) {
            is Float.Companion -> {
                val values = valueY.map { it.value.toFloat() }.sortedByDescending { it }
                return handleFloatType(values, pointCount, pointsUpper, pointsLower)
            }
            is Int.Companion -> {
                val values = valueY.map { it.value.toInt() }.sortedByDescending { it }
                return handleIntegerType(values, pointCount, pointsUpper, pointsLower)
            }
            is Double.Companion -> {
                val values = valueY.map { it.value.toDouble() }.sortedByDescending { it }
                return handleDoubleType(values, pointCount, pointsUpper, pointsLower)
            }
            is Long.Companion -> {
                val values = valueY.map { it.value.toLong() }.sortedByDescending { it }
                return handleLongType(values, pointCount, pointsUpper, pointsLower)
            }
            else -> null
        }
    }

    private fun handleDoubleType(
        values: List<Double>,
        pointCount: Int,
        pointsUpper: MutableList<String>,
        pointsLower: MutableList<String>
    ): ValueCalculation {
        val valuesUpper = mutableListOf<Double>()
        val valuesLower = mutableListOf<Double>()

        val max = values.max()!!
        val min = values.min()!!

        val maxRounded = max.roundToNearest()
        val minRounded = min.roundToNearest()

        if (min < 0) {

            val upperCount = if (pointCount % 2 == 0) (pointCount / 2) else (pointCount / 2) + 1
            val lowerCount = (pointCount / 2)

            for (count in upperCount-1 downTo  0) {
                val value = max / upperCount.toDouble()
                val point = value * count.toDouble()
                valuesUpper.add(point.roundToNearest())
            }

            for (count in 1 until lowerCount) {
                val value = min / lowerCount.toDouble()
                val point = value * count.toDouble()
                valuesLower.add(point.roundToNearest())
            }

            valuesLower.add(minRounded)
        } else {
            for (count in pointCount-1 downTo  0) {
                val value = max / pointCount.toDouble()
                val point = value * count.toDouble()
                valuesUpper.add(point.roundToNearest())
            }
        }

        pointsUpper.addAll(valuesUpper.map { it.toString() })
        pointsLower.addAll(valuesLower.map { it.toString() })

        return ValueCalculation(values, pointsUpper, pointsLower, max, maxRounded, min, minRounded)
    }

    private fun handleIntegerType(
        values: List<Int>,
        pointCount: Int,
        pointsUpper: MutableList<String>,
        pointsLower: MutableList<String>
    ): ValueCalculation {
        val valuesUpper = mutableListOf<Int>()
        val valuesLower = mutableListOf<Int>()

        val max = values.max()!!
        val min = values.min()!!

        val maxRounded = max.roundToNearest()
        val minRounded = min.roundToNearest()

        if (min < 0) {

            val upperCount = if (pointCount % 2 == 0) (pointCount / 2) else (pointCount / 2) + 1
            val lowerCount = (pointCount / 2)

            for (count in upperCount-1 downTo  0) {
                val value = max / upperCount
                val point = value * count
                valuesUpper.add(point.roundToNearest())
            }

            for (count in 1 until lowerCount) {
                val value = min / lowerCount
                val point = value * count
                valuesLower.add(point.roundToNearest())
            }

            valuesLower.add(minRounded)
        } else {
            for (count in pointCount-1 downTo  0) {
                val value = max / pointCount
                val point = value * count
                valuesUpper.add(point.roundToNearest())
            }
        }

        pointsUpper.addAll(valuesUpper.map { it.toString() })
        pointsLower.addAll(valuesLower.map { it.toString() })

        return ValueCalculation(values, pointsUpper, pointsLower, max, maxRounded, min, minRounded)
    }

    private fun handleFloatType(
        values: List<Float>,
        pointCount: Int,
        pointsUpper: MutableList<String>,
        pointsLower: MutableList<String>
    ): ValueCalculation {
        val valuesUpper = mutableListOf<Float>()
        val valuesLower = mutableListOf<Float>()

        val max = values.max()!!
        val min = values.min()!!

        val maxRounded = max.roundToNearest()
        val minRounded = min.roundToNearest()

        if (min < 0) {

            val upperCount = if (pointCount % 2 == 0) (pointCount / 2) else (pointCount / 2) + 1
            val lowerCount = (pointCount / 2)

            for (count in upperCount-1 downTo  0) {
                val value = max / upperCount.toFloat()
                val point = value * count.toFloat()
                valuesUpper.add(point.roundToNearest())
            }

            for (count in 1 until lowerCount) {
                val value = min / lowerCount.toFloat()
                val point = value * count.toFloat()
                valuesLower.add(point.roundToNearest())
            }

            valuesLower.add(minRounded)
        } else {
            for (count in pointCount-1 downTo  0) {
                val value = max / pointCount.toFloat()
                val point = value * count.toFloat()
                valuesUpper.add(point.roundToNearest())
            }
        }

        pointsUpper.addAll(valuesUpper.map { it.toString() })
        pointsLower.addAll(valuesLower.map { it.toString() })

        return ValueCalculation(values, pointsUpper, pointsLower, max, maxRounded, min, minRounded)
    }

    private fun handleLongType(
        values: List<Long>,
        pointCount: Int,
        pointsUpper: MutableList<String>,
        pointsLower: MutableList<String>
    ): ValueCalculation {
        val valuesUpper = mutableListOf<Long>()
        val valuesLower = mutableListOf<Long>()

        val max = values.max()!!
        val min = values.min()!!

        val maxRounded = max.roundToNearest()
        val minRounded = min.roundToNearest()

        if (min < 0) {

            val upperCount = if (pointCount % 2 == 0) (pointCount / 2) else (pointCount / 2) + 1
            val lowerCount = (pointCount / 2)

            for (count in upperCount-1 downTo  0) {
                val value = max / upperCount.toLong()
                val point = value * count.toLong()
                valuesUpper.add(point.roundToNearest())
            }

            for (count in 1 until lowerCount) {
                val value = min / lowerCount.toLong()
                val point = value * count.toLong()
                valuesLower.add(point.roundToNearest())
            }

            valuesLower.add(minRounded)
        } else {
            for (count in pointCount-1 downTo  0) {
                val value = max / pointCount.toLong()
                val point = value * count.toLong()
                valuesUpper.add(point.roundToNearest())
            }
        }

        pointsUpper.addAll(valuesUpper.map { it.toString() })
        pointsLower.addAll(valuesLower.map { it.toString() })

        return ValueCalculation(values, pointsUpper, pointsLower, max, maxRounded, min, minRounded)
    }

    fun getElements() = values?.valuesBuildData?.textElements

    data class ValueCalculation(
        val values: List<Any>,
        val upperPoints: List<String> = ArrayList(),
        val lowerPoints: List<String> = ArrayList(),
        val maxValue: Any,
        val maxValueRounded: Any,
        val minValue: Any,
        val minValueRounded: Any,
        var valuesBuildData: AxisBuildData = AxisBuildData()
    )

    data class AxisBuildData(
        val textElements: List<ChartText> = ArrayList(),
        val start: Float = 0f,
        val margin: Float = 0f
    ) {
        lateinit var zeroPoint: ChartText
    }
}