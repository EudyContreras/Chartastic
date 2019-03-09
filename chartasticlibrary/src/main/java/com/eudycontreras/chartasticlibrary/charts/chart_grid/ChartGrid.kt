package com.eudycontreras.chartasticlibrary.charts.chart_grid

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Typeface
import com.eudycontreras.chartasticlibrary.Shape
import com.eudycontreras.chartasticlibrary.ShapeRenderer
import com.eudycontreras.chartasticlibrary.charts.ChartBoundsManager
import com.eudycontreras.chartasticlibrary.charts.ChartElement
import com.eudycontreras.chartasticlibrary.charts.chart_model.bar_chart.BarChartData
import com.eudycontreras.chartasticlibrary.charts.chart_text.ChartText
import com.eudycontreras.chartasticlibrary.properties.*
import com.eudycontreras.chartasticlibrary.shapes.BoundingBox
import com.eudycontreras.chartasticlibrary.shapes.Line
import com.eudycontreras.chartasticlibrary.utilities.extensions.dp
import com.eudycontreras.chartasticlibrary.utilities.extensions.sp

/**
 * Created by eudycontreras.
 */


class ChartGrid(private val boundsManager: ChartBoundsManager): ChartElement {

    override var render: Boolean = true

    private val boundsBox: Shape by lazy {
        BoundingBox().apply {
            this.bounds = this@ChartGrid.bounds
        }
    }

    enum class Border(var value: Int) {
        TOP(0),
        LEFT(1),
        RIGHT(2),
        BOTTOM(3),
        NONE(4),
        ALL(-1),
    }

    enum class LinePlacement {
        ALIGNED,
        BETWEEN
    }

    lateinit var data: BarChartData

    var bounds: Bounds = Bounds()

    lateinit var drawableZone: Bounds

    private val valuesX = HashMap<Int, ChartGridAxisX>()
    private val valuesY = HashMap<Int, ChartGridAxisY>()

    private val borders = arrayListOf(Line(), Line(), Line(), Line())

    private val horizontalGridLines = ArrayList<Line>()

    private var borderLineThickness = 1.dp

    private var mValueYPointCount = 0

    private var showLines = true
    private var showYText = true

    var valueYPointCount: Int
        get() {
            return mValueYPointCount
        }
        set(value) {
            mValueYPointCount = value
        }

    private var mPointLineColor = MutableColor()

    var pointLineColor: MutableColor
        get() = mPointLineColor
        set(value) {
            mPointLineColor = value
        }

    private var mPointLineThickness = 1.dp

    var pointLineThickness: Float
        get() = mPointLineThickness
        set(value) {
            mPointLineThickness = value
        }

    init {
        valuesY[ChartGridAxisY.LEFT] = ChartGridAxisY(Paint(), ChartGridAxisY.LEFT, this)
        valuesY[ChartGridAxisY.RIGHT] = ChartGridAxisY(Paint(), ChartGridAxisY.RIGHT, this)
    }

    override fun render(
        path: Path,
        paint: Paint,
        canvas: Canvas,
        renderingProperties: ShapeRenderer.RenderingProperties
    ) {
        horizontalGridLines.forEach { it.render(path, paint, canvas, renderingProperties) }
        borders.forEach { it.render(path, paint, canvas, renderingProperties) }
        valuesY.getValue(ChartGridAxisY.LEFT).getElements().forEach { it.render(path, paint, canvas, renderingProperties) }
    }

    override fun build(bounds: Bounds) {
        this.bounds.update(bounds)

        borders.forEach { it.shadowPosition = LightSource.Position.BOTTOM_LEFT }

        val left = valuesY[ChartGridAxisY.LEFT]!!.getValues().second + valuesY[ChartGridAxisY.RIGHT]!!.getValues().second
        val right = valuesY[ChartGridAxisY.RIGHT]!!.getValues().third + valuesY[ChartGridAxisY.LEFT]!!.getValues().third

        val top = bounds.coordinate.y
        val bottom = bounds.dimension.height

        borders[Border.TOP.value].coordinate.x = left
        borders[Border.TOP.value].coordinate.y = top
        borders[Border.TOP.value].dimension.width = Math.abs(left - right)

        borders[Border.LEFT.value].coordinate.x = left
        borders[Border.LEFT.value].coordinate.y = top
        borders[Border.LEFT.value].dimension.height = Math.abs(bottom - top) + top

        borders[Border.BOTTOM.value].coordinate.x = left
        borders[Border.BOTTOM.value].coordinate.y = (top + bottom) - borders[Border.BOTTOM.value].dimension.height
        borders[Border.BOTTOM.value].dimension.width =
            Math.abs(left - right) + borders[Border.LEFT.value].dimension.width

        borders[Border.RIGHT.value].coordinate.x = left + Math.abs(left - right)
        borders[Border.RIGHT.value].coordinate.y = top
        borders[Border.RIGHT.value].dimension.height = Math.abs(bottom - top) + top

        val coordinates = Coordinate().apply {
            x = borders[Border.LEFT.value].coordinate.x + borders[Border.LEFT.value].dimension.width
            y = borders[Border.TOP.value].coordinate.y + borders[Border.TOP.value].dimension.height
        }

        val dimension = Dimension().apply {
            width =
                (borders[Border.RIGHT.value].coordinate.x - coordinates.x) - borders[Border.RIGHT.value].dimension.width
            height = (borders[Border.BOTTOM.value].coordinate.y - coordinates.y)
        }

        drawableZone = Bounds(coordinates, dimension)
    }

    private fun buildLines(values: Triple<List<ChartText>, Float, Float>, placement: LinePlacement, value: Int) {
        horizontalGridLines.clear()

        for (index in 0 until values.first.size) {
            val text = values.first[index]
            val line = Line()

            line.color.setColor(mPointLineColor)
            line.elevation = 0f
            line.drawShadow = false
            line.render = showLines

            if (placement == LinePlacement.ALIGNED) {
                line.coordinate.x = borders[Border.TOP.value].coordinate.x
                line.coordinate.y = text.y - (text.dimension.height / 2)
                line.dimension.height = mPointLineThickness
                line.dimension.width = borders[Border.TOP.value].dimension.width
            } else {

                if (index < values.first.size - 1) {
                    val shift = index + 1
                    val next = values.first[shift].y

                    if (value == ChartGridAxisY.LEFT) {
                        line.coordinate.x = bounds.coordinate.x
                        line.dimension.width = bounds.dimension.width
                    } else {
                        line.coordinate.x = borders[Border.TOP.value].coordinate.x
                        line.dimension.width = bounds.dimension.width
                    }
                    line.coordinate.y = text.y + ((next - text.y) / 2) - (text.dimension.height / 2)
                    line.dimension.height = mPointLineThickness
                } else {
                    continue
                }
            }
            horizontalGridLines.add(line)
        }
    }

    fun setBorderColor(color: MutableColor, border: Border = Border.ALL) {
        if (border == Border.ALL) {
            borders.forEach { it.color.setColor(color) }
            return
        }
        borders[border.value].color.setColor(color)
    }

    fun setBorderElevation(elevation: Float, border: Border = Border.ALL) {
        if (border == Border.ALL) {
            borders.forEach {
                if (it.render) {
                    it.drawShadow = elevation > 0
                    it.elevation = elevation
                }
            }
            return
        }

        if (borders[border.value].render) {
            borders[border.value].drawShadow = elevation > 0
            borders[border.value].elevation = elevation
        }
    }

    fun setBorderThickness(thickness: Float, border: Border = Border.ALL) {
        if (border == Border.ALL) {
            this.borderLineThickness = thickness
            borders[Border.TOP.value].dimension.height = thickness
            borders[Border.LEFT.value].dimension.width = thickness
            borders[Border.BOTTOM.value].dimension.height = thickness
            borders[Border.RIGHT.value].dimension.width = thickness
            return
        }
        when (border) {
            Border.RIGHT -> borders[Border.RIGHT.value].dimension.width = thickness
            Border.TOP -> borders[Border.TOP.value].dimension.height = thickness
            Border.LEFT -> borders[Border.LEFT.value].dimension.width = thickness
            Border.BOTTOM -> borders[Border.BOTTOM.value].dimension.height = thickness
            else -> this.borderLineThickness = thickness
        }
    }

    fun setDataSource(data: BarChartData) {
        this.data = data
    }

    fun getBorderThickness(border: Border = Border.ALL): Float {
        if (border == Border.ALL) {
            return borderLineThickness
        }
        return when (border) {
            Border.RIGHT -> borders[Border.RIGHT.value].dimension.width
            Border.TOP -> borders[Border.TOP.value].dimension.height
            Border.LEFT -> borders[Border.LEFT.value].dimension.width
            Border.BOTTOM -> borders[Border.BOTTOM.value].dimension.height
            else -> borderLineThickness
        }
    }

    fun getBorderColor(border: Border): MutableColor {
        return borders[border.value].color
    }

    fun getBorderElevation(border: Border): Float {
        return borders[border.value].elevation
    }

    fun showBorder(show: Boolean, border: ChartGrid.Border = Border.ALL) {
        if (border == Border.ALL) {
            borders.forEach { it.render = show }
            return
        }
        borders[border.value].render = show
    }

    fun showYValues(
        bounds: Bounds,
        data: BarChartData,
        paddingLeft: Float,
        paddingRight: Float,
        value: Int,
        append: String = "",
        prepend: String = ""
    ) {
        valuesY[value]?.let {
            it.paddingLeft = paddingLeft
            it.paddingRight = paddingRight
            it.paddingTop = bounds.paddings?.top?:0f
            it.prepend = prepend
            it.append = append
            it.textSize = 9.sp
            it.typeFace = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            it.textColor = MutableColor.rgba(255, 255, 255, 0.75f)
            it.build(data, bounds, mValueYPointCount)
            it.showText(showYText)

            build(bounds)
            buildLines(it.getValues(), LinePlacement.ALIGNED, value)
        }
    }

    fun showXValues(value: Int, padding: Float, bounds: Bounds) {

    }

    fun getGridValueY(value: Int): ChartGridAxisY {
        return valuesY[value]!!
    }

    fun getElements(value: Int): List<ChartElement> {
        return valuesY[value]!!.getElements()
    }

    fun getBoundingBox(value: Int): Shape {
        return valuesY[value]!!.getBoundingBox()
    }

    fun getBorders(): List<Line> {
        return borders
    }

    fun getLines(): List<Line> {
        return horizontalGridLines
    }

    fun showGridLines(value: Boolean) {
        this.showLines = value
        horizontalGridLines.forEach { it.render = value }
    }

    fun showYTextValues(value: Boolean, type: Int) {
        this.showYText = value
        valuesY[type]?.showText(value)
    }

    fun getBoundingBox(): Shape {
        return BoundingBox().apply {
            dimension = drawableZone.dimension
            coordinate = drawableZone.coordinate
        }
    }
}