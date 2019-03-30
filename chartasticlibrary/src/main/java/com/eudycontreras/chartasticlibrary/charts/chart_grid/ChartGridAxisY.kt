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
import com.eudycontreras.chartasticlibrary.charts.chart_data.DataType
import com.eudycontreras.chartasticlibrary.charts.chart_model.bar_chart.BarChartData
import com.eudycontreras.chartasticlibrary.charts.chart_options.AxisYOptions
import com.eudycontreras.chartasticlibrary.charts.interfaces.ChartBoundsOwner
import com.eudycontreras.chartasticlibrary.properties.Bounds
import com.eudycontreras.chartasticlibrary.properties.Coordinate
import com.eudycontreras.chartasticlibrary.properties.MutableColor
import com.eudycontreras.chartasticlibrary.properties.Padding
import com.eudycontreras.chartasticlibrary.shapes.BoundingBox
import com.eudycontreras.chartasticlibrary.shapes.Line
import com.eudycontreras.chartasticlibrary.utilities.extensions.*
import com.eudycontreras.chartasticlibrary.utilities.global.BoundsChangeListener
import com.eudycontreras.chartasticlibrary.utilities.global.mapRange

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

    override val changeListeners: ArrayList<BoundsChangeListener> by lazy {
        ArrayList<BoundsChangeListener>()
    }

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
                    buildLeft(bounds.drawableArea)
                    values?.valuesBuildData?.let {
                        buildLeftTicks(it.gridTextElements)
                    }
                }
                Type.RIGHT -> {
                    buildRight(bounds.drawableArea)
                    values?.valuesBuildData?.let {
                        buildRightTicks(it.gridTextElements)
                    }
                }
            }
            this.bounds.update(axisLabelBounds)
        }
        when (type) {
            Type.LEFT -> {
                buildLeft(bounds.drawableArea)
                values?.valuesBuildData?.let {
                    buildLeftTicks(it.gridTextElements)
                }
            }
            Type.RIGHT -> {
                buildRight(bounds.drawableArea)
                values?.valuesBuildData?.let {
                    buildRightTicks(it.gridTextElements)
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
                buildLeft(bounds.drawableArea)
                values?.valuesBuildData?.let {
                    buildLeftTicks(it.gridTextElements)
                }
            }
            Type.RIGHT -> {
                buildRight(bounds.drawableArea)
                values?.valuesBuildData?.let {
                    buildRightTicks(it.gridTextElements)
                }
            }
        }

        changeListeners.forEach { it.invoke(this.bounds) }

        if(layoutManager.showBoundingBoxes){
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

        if(layoutManager.showBoundingBoxes){
            boundsBox.render(path, paint, canvas, renderingProperties)
        }

        values?.valuesBuildData?.let { data ->
            data.gridTextElements.forEach { it.render(path, paint, canvas, renderingProperties) }
        }

        if (options.showTickLines) {
            tickLines.forEach { it.render(path, paint, canvas, renderingProperties) }
        }
    }

    private fun buildLeft(bounds: Bounds) {
        if(options.chartData == null){
            return
        }
        val data = options.chartData!!

        val valuesY = computeValues(data.valueY, data.valueTypeY, options.positiveValuePointCount, options.negativeValuePointCount)

        val chartTexts = mutableListOf<ChartGridText>()

        val zeroPoint = Coordinate()

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            isAntiAlias = true
        }

        var margin = 0f

        valuesY?.let {
            val referenceUpper = ChartGridText(
                it.maxValueRounded,
                options.labelValuePrepend,
                options.labelValueAppend
            )
            referenceUpper.alignment = ChartGridText.Alignment.RIGHT
            referenceUpper.textColor = options.labelTextColor
            referenceUpper.textSize = options.labelTextSize
            referenceUpper.typeFace = options.labelTypeFace
            referenceUpper.render = options.showLabels
            referenceUpper.paint = paint
            referenceUpper.build()

            referenceUpper.y = (bounds.top + (referenceUpper.dimension.height))

            val referenceLower = ChartGridText(
                it.minValueRounded,
                options.labelValuePrepend,
                options.labelValueAppend
            )
            referenceLower.alignment = ChartGridText.Alignment.RIGHT
            referenceLower.textColor = options.labelTextColor
            referenceLower.textSize = options.labelTextSize
            referenceLower.typeFace = options.labelTypeFace
            referenceLower.render = options.showLabels
            referenceLower.paint = paint
            referenceLower.build()

            referenceLower.y = (bounds.top + (referenceLower.dimension.height))

            val top = referenceUpper.y
            val bottom = bounds.bottom

            val height: Float = (bottom - top)

            val referenceUpperWidth = (if (options.showLabels) referenceUpper.dimension.width else 0f) + (options.padding.end + options.tickLength) + options.padding.start
            val referenceLowerWidth = (if (options.showLabels) referenceLower.dimension.width else 0f) + (options.padding.end + options.tickLength) + options.padding.start

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

                val chartText = ChartGridText(
                    value,
                    options.labelValuePrepend,
                    options.labelValueAppend
                )
                chartText.copyStyle(reference)
                chartText.paint = paint
                chartText.render = options.showLabels
                chartText.build()

                chartText.x = reference.x
                chartText.y = top + offset

                offset += increase

                chartTexts.add(chartText)
            }

            zeroPoint.y = chartTexts[chartTexts.size - 1].y  - (chartTexts[chartTexts.size - 1].dimension.height / 2)

            data.zeroPoint?.let { point ->
                findZeroPointPosition(point, data, chartTexts)?.let {
                    zeroPoint.y = it
                }
            }

            for (value in it.lowerPoints) {

                paint.reset()

                val chartText = ChartGridText(
                    value,
                    options.labelValuePrepend,
                    options.labelValueAppend
                )
                chartText.copyStyle(reference)
                chartText.paint = paint
                chartText.render = options.showLabels
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

    private fun findZeroPointPosition(
        point: Any,
        data: BarChartData,
        chartGridTexts: MutableList<ChartGridText>
    ): Float? {
        if (point.isNumeric(data.valueTypeY)) {
            if (!point.isZero(data.valueTypeY)) {
                if (point.isPositive(data.valueTypeY)) {
                    if (data.valueTypeY == DataType.NUMERIC_WHOLE) {
                        if (point is Long) {

                            val highest: Int = chartGridTexts[0].value.restored()

                            if (point >= highest) {
                                return null
                            }

                            val match = chartGridTexts.find { it.value.restored<Long>() == point }

                            if (match == null) {

                                val upperElement = chartGridTexts
                                    .sortedBy { it.value.restored<Long>() }
                                    .first { it.value.restored<Long>() > point }
                                val lowerElement = chartGridTexts.first { it.value.restored<Long>() < point }

                                val upperYCoordinate = (upperElement.y - upperElement.dimension.height / 2)
                                val lowerYCoordinate = (lowerElement.y - lowerElement.dimension.height / 2)

                                val upperYValue: Long = upperElement.value.restored()
                                val lowerYValue: Long = upperElement.value.restored()

                                return mapRange(
                                    point.toFloat(),
                                    lowerYValue.toFloat(),
                                    upperYValue.toFloat(),
                                    lowerYCoordinate,
                                    upperYCoordinate
                                )

                            } else {
                               return match.y - (match.dimension.height / 2)
                            }

                        } else if (point is Int) {

                            val highest: Int = chartGridTexts[0].value.restored()

                            if (point >= highest) {
                                return null
                            }

                            val match = chartGridTexts.find { it.value.restored<Int>() == point }

                            if (match == null) {

                                val upperElement = chartGridTexts
                                    .sortedBy { it.value.restored<Int>() }
                                    .first { it.value.restored<Int>() > point }
                                val lowerElement = chartGridTexts.first { it.value.restored<Int>() < point }

                                val upperYCoordinate = (upperElement.y - upperElement.dimension.height / 2)
                                val lowerYCoordinate = (lowerElement.y - lowerElement.dimension.height / 2)

                                val upperYValue: Int = upperElement.value.restored()
                                val lowerYValue: Int = lowerElement.value.restored()

                                return mapRange(
                                    point.toFloat(),
                                    lowerYValue.toFloat(),
                                    upperYValue.toFloat(),
                                    lowerYCoordinate,
                                    upperYCoordinate
                                )

                            } else {
                                return match.y - (match.dimension.height / 2)
                            }
                        }
                    } else {
                        if (point is Double) {

                        } else if (point is Float) {

                        }
                    }
                }
            } else {
                return null
            }
        } else {
            return null
        }

        return null
    }

    private fun buildLeftTicks(chartGridTexts: List<ChartGridText>) {

        if(chartGridTexts.isEmpty()) {
            return
        }
        tickLines.clear()

        val x = bounds.right - options.tickLength

        for (text in chartGridTexts) {

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

    private fun buildRight(bounds: Bounds) {
        if(options.chartData == null){
            return
        }
        val data = options.chartData!!

        val valuesY = computeValues(data.valueY, data.valueTypeY, options.positiveValuePointCount, options.negativeValuePointCount)

        val chartTexts = mutableListOf<ChartGridText>()

        var zeroPoint = Coordinate()

        var margin = 0f

        val paint = Paint()

        valuesY?.let {
            val points = it.upperPoints

            val reference = ChartGridText(
                it.maxValueRounded,
                options.labelValuePrepend,
                options.labelValueAppend
            )
            reference.alignment = ChartGridText.Alignment.LEFT
            reference.textColor = options.labelTextColor
            reference.textSize = options.labelTextSize
            reference.typeFace = options.labelTypeFace
            reference.paint = paint
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

                paint.reset()

                val chartText = ChartGridText(
                    value,
                    options.labelValuePrepend,
                    options.labelValueAppend
                )
                chartText.copyStyle(reference)
                chartText.paint = paint
                chartText.build()

                chartText.x = reference.x
                chartText.y = top + offset

                offset += increase

                chartTexts.add(chartText)
            }

            zeroPoint = Coordinate(chartTexts[chartTexts.size - 1].x, chartTexts[chartTexts.size - 1].y)
        }

        valuesY?.valuesBuildData = AxisBuildData(chartTexts, margin, axisLabelBounds.dimension.width).apply { this.zeroPoint = zeroPoint }
        values = valuesY
    }

    private fun buildRightTicks(chartGridTexts: List<ChartGridText>) {

        if(chartGridTexts.isEmpty()) {
            return
        }
        tickLines.clear()

        val x = bounds.left

        for (text in chartGridTexts) {
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
        dataType: DataType,
        positivePointCount: Int,
        negativePointCount: Int
    ): ValueCalculation? {
        return when (dataType) {
            DataType.ALPHABETIC -> {
                val values = valueY.map { it.value.restored<String>() }.sortedByDescending { it }
                handleStringType(values, positivePointCount, negativePointCount)
            }
            DataType.NUMERIC_DECIMAL -> {
                if(valueY[0].value is Double) {
                    val values = valueY.map { it.value.restored<Double>() }.sortedByDescending { it }
                    handleDoubleType(values, positivePointCount, negativePointCount)
                } else {
                    val values = valueY.map { it.value.restored<Float>() }.sortedByDescending { it }
                    handleFloatType(values, positivePointCount, negativePointCount)
                }
            }
            DataType.NUMERIC_WHOLE -> {
                if(valueY[0].value is Long) {
                    val values = valueY.map { it.value.restored<Long>() }.sortedByDescending { it }
                    handleLongType(values, positivePointCount, negativePointCount)
                } else {
                    val values = valueY.map { it.value.restored<Int>() }.sortedByDescending { it }
                    handleIntegerType(values, positivePointCount, negativePointCount)
                }
            }
            DataType.BINARY -> {
                val values = valueY.map { it.value.restored<Boolean>() }.sortedByDescending { it }
                handleBooleanType(values, positivePointCount, negativePointCount)
            }
        }
    }

    private fun handleDoubleType(
        values: List<Double>,
        positivePointCount: Int,
        negativePointCount: Int
    ): ValueCalculation {
        val valuesUpper = mutableListOf<Double>()
        val valuesLower = mutableListOf<Double>()

        val max = values.max()!!
        val min = values.min()!!

        val maxRounded = max.roundToNearest()
        val minRounded = min.roundToNearest()

        if (min < 0) {

            for (count in positivePointCount-1 downTo  0) {
                val value = max / positivePointCount.toDouble()
                val point = value * count.toDouble()
                valuesUpper.add(point.roundToNearest())
            }

            for (count in 1 until negativePointCount) {
                val value = min / negativePointCount.toDouble()
                val point = value * count.toDouble()
                valuesLower.add(point.roundToNearest())
            }

            valuesLower.add(minRounded)
        } else {
            for (count in positivePointCount-1 downTo  0) {
                val value = max / positivePointCount.toDouble()
                val point = value * count.toDouble()
                valuesUpper.add(point.roundToNearest())
            }
        }

        return ValueCalculation(values, valuesUpper, valuesLower, max, maxRounded, min, minRounded)
    }

    private fun handleLongType(
        values: List<Long>,
        positivePointCount: Int,
        negativePointCount: Int
    ): ValueCalculation {
        val valuesUpper = mutableListOf<Long>()
        val valuesLower = mutableListOf<Long>()

        val max = values.max()!!
        val min = values.min()!!

        val maxRounded = max.roundToNearest()
        val minRounded = min.roundToNearest()

        if (min < 0) {

            for (count in positivePointCount-1 downTo  0) {
                val value = max / positivePointCount.toLong()
                val point = value * count.toLong()
                valuesUpper.add(point.roundToNearest())
            }

            for (count in 1 until negativePointCount) {
                val value = min / negativePointCount.toLong()
                val point = value * count.toLong()
                valuesLower.add(point.roundToNearest())
            }

            valuesLower.add(minRounded)
        } else {
            for (count in positivePointCount-1 downTo  0) {
                val value = max / positivePointCount.toLong()
                val point = value * count.toLong()
                valuesUpper.add(point.roundToNearest())
            }
        }

        return ValueCalculation(values, valuesUpper, valuesLower, max, maxRounded, min, minRounded)
    }

    private fun handleIntegerType(
        values: List<Int>,
        positivePointCount: Int,
        negativePointCount: Int
    ): ValueCalculation {
        val valuesUpper = ArrayList<Int>()
        val valuesLower = ArrayList<Int>()

        val max = values.max()!!
        val min = values.min()!!

        val maxRounded = max.roundToNearest(shift = 1, ratio = 0.5f)
        val minRounded = min.roundToNearest(shift = 1, ratio = 0.5f)

        if (max > 0 && positivePointCount > 0) {
            val increase = (max / positivePointCount).roundToNearest(shift = 1, ratio = 0.5f, method = RoundMethod.DOWN)

            for (count in positivePointCount-1 downTo 0) {
                val point = increase * count
                valuesUpper.add(point.roundToNearest(shift = 1, ratio = 0.5f))
            }

            valuesUpper.removeAll { it == maxRounded }
        }

        if (min < 0 && negativePointCount > 0) {
            val decrease = (min / negativePointCount).roundToNearest(shift = 1, ratio = 0.5f, method = RoundMethod.DOWN)

            for (count in 1..negativePointCount) {
                val point = decrease * count
                valuesLower.add(point.roundToNearest(shift = 1, ratio = 0.5f))
            }

            valuesLower.removeAll { it == minRounded}
        }

        return ValueCalculation(values, valuesUpper.distinct(), valuesLower.distinct() , max, maxRounded, min, minRounded)
    }

    private fun handleFloatType(
        values: List<Float>,
        positivePointCount: Int,
        negativePointCount: Int
    ): ValueCalculation {
        val valuesUpper = mutableListOf<Float>()
        val valuesLower = mutableListOf<Float>()

        val max = values.max()!!
        val min = values.min()!!

        val maxRounded = max.roundToNearest()
        val minRounded = min.roundToNearest()

        if (min < 0) {

            for (count in positivePointCount-1 downTo  0) {
                val value = max / positivePointCount.toFloat()
                val point = value * count.toFloat()
                valuesUpper.add(point.roundToNearest())
            }

            for (count in 1 until negativePointCount) {
                val value = min / negativePointCount.toFloat()
                val point = value * count.toFloat()
                valuesLower.add(point.roundToNearest())
            }

            valuesLower.add(minRounded)
        } else {
            for (count in positivePointCount-1 downTo  0) {
                val value = max / positivePointCount.toFloat()
                val point = value * count.toFloat()
                valuesUpper.add(point.roundToNearest())
            }
        }

        return ValueCalculation(values, valuesUpper, valuesLower, max, maxRounded, min, minRounded)
    }

    private fun handleStringType(
        values: List<String>,
        positivePointCount: Int,
        negativePointCount: Int
    ): ValueCalculation {

        val valuesUpper = mutableListOf<String>()
        val valuesLower = mutableListOf<String>()

        return ValueCalculation(values, valuesUpper, valuesLower, 0, 0, 0, 0)
    }

    private fun handleBooleanType(
        values: List<Boolean>,
        positivePointCount: Int,
        negativePointCount: Int
    ): ValueCalculation {

        val valuesUpper = mutableListOf<Boolean>()
        val valuesLower = mutableListOf<Boolean>()

        return ValueCalculation(values, valuesUpper, valuesLower, 0, 0, 0, 0)
    }

    fun getElements() = values?.valuesBuildData?.gridTextElements

    data class ValueCalculation(
        val values: List<Any>,
        val upperPoints: List<Any> = ArrayList(),
        val lowerPoints: List<Any> = ArrayList(),
        val maxValue: Any,
        val maxValueRounded: Any,
        val minValue: Any,
        val minValueRounded: Any,
        var valuesBuildData: AxisBuildData = AxisBuildData()
    )

    data class AxisBuildData(
        val gridTextElements: List<ChartGridText> = ArrayList(),
        val start: Float = 0f,
        val margin: Float = 0f
    ) {
        lateinit var zeroPoint: Coordinate
    }
}