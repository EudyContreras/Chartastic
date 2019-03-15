package com.eudycontreras.chartasticlibrary.charts.chart_grid

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.eudycontreras.chartasticlibrary.Shape
import com.eudycontreras.chartasticlibrary.ShapeRenderer
import com.eudycontreras.chartasticlibrary.charts.ChartBoundsManager
import com.eudycontreras.chartasticlibrary.charts.ChartElement
import com.eudycontreras.chartasticlibrary.charts.chart_model.bar_chart.BarChartData
import com.eudycontreras.chartasticlibrary.charts.chart_options.AxisYOptions
import com.eudycontreras.chartasticlibrary.properties.*
import com.eudycontreras.chartasticlibrary.shapes.BoundingBox
import com.eudycontreras.chartasticlibrary.shapes.Line
import com.eudycontreras.chartasticlibrary.utilities.extensions.dp

/**
 * Created by eudycontreras.
 */


class ChartGrid(private val boundsManager: ChartBoundsManager): ChartElement {

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

    enum class GridLineType {
        MINOR,
        MAJOR
    }

    enum class TickLineType {
        MINOR,
        MAJOR
    }

    override var render: Boolean = true

    private val boundsBox: Shape by lazy {
        BoundingBox().apply {
            this.bounds = this@ChartGrid.bounds
        }
    }

    private val borders = arrayListOf(Line(), Line(), Line(), Line())

    private val majorGridLines = ArrayList<Line>()
    private val minorGridLines = ArrayList<Line>()

    private var borderLineThickness = 1.dp

    lateinit var data: BarChartData

    lateinit var drawableZone: Bounds

    var padding: Padding = Padding()

    var bounds: Bounds = Bounds()

    var showMajorGridLines = true
    var showMinorGridLines = true

    var majorGridLineCount: Int = 0
    var minorGridLineCount: Int = 0

    var majorGridLineColor: MutableColor = MutableColor()
    var minorGridLineColor: MutableColor = MutableColor()

    var majorGridLineThickness: Float = 1.dp
    var minorGridLineThickness: Float = 0.5f.dp

    init {

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

        minorGridLines.forEach { it.render(path, paint, canvas, renderingProperties) }
        majorGridLines.forEach { it.render(path, paint, canvas, renderingProperties) }
        borders.forEach { it.render(path, paint, canvas, renderingProperties) }
    }

    fun build(bounds: Bounds = Bounds()) {
        this.bounds.update(bounds)

        borders.forEach { it.shadowPosition = LightSource.Position.BOTTOM_LEFT }

        val left = bounds.left
        val right = bounds.right

        val top = bounds.top
        val bottom = bounds.bottom

        borders[Border.TOP.value].coordinate.x = left
        borders[Border.TOP.value].coordinate.y = top
        borders[Border.TOP.value].dimension.width = Math.abs(left - right)

        borders[Border.LEFT.value].coordinate.x = left
        borders[Border.LEFT.value].coordinate.y = top
        borders[Border.LEFT.value].dimension.height = Math.abs(bottom - top)

        borders[Border.BOTTOM.value].coordinate.x = left
        borders[Border.BOTTOM.value].coordinate.y = bottom - borders[Border.BOTTOM.value].dimension.height
        borders[Border.BOTTOM.value].dimension.width = Math.abs(left - right)

        borders[Border.RIGHT.value].coordinate.x = right - borders[Border.RIGHT.value].dimension.width
        borders[Border.RIGHT.value].coordinate.y = top
        borders[Border.RIGHT.value].dimension.height = Math.abs(bottom - top)

        val coordinates = Coordinate().apply {
            x = borders[Border.LEFT.value].right
            y = borders[Border.TOP.value].bottom
        }

        val dimension = Dimension().apply {
            width = borders[Border.RIGHT.value].left - borders[Border.LEFT.value].right
            height = borders[Border.BOTTOM.value].top -  borders[Border.TOP.value].bottom
        }

        drawableZone = Bounds(coordinates, dimension).addPadding(padding)
    }

    fun buildMinorLines(majorGridLines: ArrayList<Line>) {
        minorGridLines.clear()

        if (majorGridLines.isEmpty()) {
            return
        }

        val count = (minorGridLineCount).toFloat() + 1f

        for(index in 0 until majorGridLines.size - 1) {
            val current = majorGridLines[index]
            val next = majorGridLines[index + 1]

            val distance = (next.top - current.bottom)

            val increase = (distance / count) + (minorGridLineThickness/count)

            val start = current.bottom  - minorGridLineThickness

            var offset = increase

            for(counter in 0 until minorGridLineCount) {
                val line = Line()

                line.color.setColor(minorGridLineColor)
                line.elevation = 0f
                line.drawShadow = false
                line.render = showMinorGridLines
                line.coordinate.x = current.coordinate.x
                line.coordinate.y = start + offset
                line.dimension.width = current.dimension.width
                line.dimension.height = minorGridLineThickness

                offset += increase

                minorGridLines.add(line)
            }
        }
    }

    fun buildMajorLines(chartGridAxisY: ChartGridAxisY) {

        fun createGridLineLabels() {
            val options = AxisYOptions()
            options.labelValueAppend = " LOC"
            options.padding = Padding(0.dp, 8.dp, 0.dp, 0.dp)
            options.valuePointCount = majorGridLineCount
            options.showLabels = true
            options.showTickLines = true
            chartGridAxisY.options = options
            chartGridAxisY.build()
            options.labelValueAppend = ""
        }

        createGridLineLabels()

        majorGridLines.clear()

        val placement = LinePlacement.ALIGNED
        val values = chartGridAxisY.getValues().first

        if (values.isEmpty()) {
            return
        }

        val topX = borders[Border.TOP.value].coordinate.x
        val topWidth = borders[Border.TOP.value].dimension.width

        for (index in 0 until values.size) {
            val text = values[index]
            val line = Line()

            line.color.setColor(majorGridLineColor)
            line.elevation = 0f
            line.drawShadow = false
            line.render = showMajorGridLines

            if (placement == LinePlacement.ALIGNED) {
                line.coordinate.x = topX
                line.coordinate.y = (text.y - (text.dimension.height / 2)) - majorGridLineThickness / 2f
                line.dimension.height = majorGridLineThickness
                line.dimension.width = topWidth
            } else {

                if (index < values.size - 1) {
                    val shift = index + 1
                    val next = values[shift].y

                    if (chartGridAxisY.type == ChartGridAxisY.Type.LEFT) {
                        line.coordinate.x = bounds.coordinate.x
                        line.dimension.width = bounds.dimension.width
                    } else {
                        line.coordinate.x = topX
                        line.dimension.width = bounds.dimension.width
                    }
                    line.coordinate.y = text.y + ((next - text.y) / 2) - (text.dimension.height / 2)
                    line.dimension.height = majorGridLineThickness
                } else {
                    continue
                }
            }
            majorGridLines.add(line)
        }

        buildMinorLines(majorGridLines)

        val top = majorGridLines[0]
        val bottom = majorGridLines[majorGridLines.size - 1]


        val coordinates = Coordinate().apply {
            x = top.left + if(isBorderVisible(Border.LEFT)) borders[Border.LEFT.value].bounds.width else 0f
            y = top.top
        }

        val dimension = Dimension().apply {
            width = (top.right - top.left) - if(isBorderVisible(Border.RIGHT)) {
                borders[Border.RIGHT.value].bounds.width + if(isBorderVisible(Border.LEFT)) borders[Border.LEFT.value].bounds.width else 0f
            } else {
                0f
            }
            height = bottom.bottom - top.top
        }

        drawableZone = Bounds(coordinates, dimension).addPadding(padding)
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

    fun showBorder(show: Boolean, border: Border = Border.ALL) {
        if (border == Border.ALL) {
            borders.forEach { it.render = show }
            return
        }
        borders[border.value].render = show
    }

    fun isBorderVisible(border: Border = Border.ALL): Boolean{
        return if (border == Border.ALL) {
            borders.all { it.render }
        } else {
            borders[border.value].render
        }
    }

    fun showGridLines(value: Boolean, lineType: GridLineType) {
        if (lineType == GridLineType.MAJOR) {
            this.showMajorGridLines = value
            majorGridLines.forEach { it.render = value }
        } else {
            this.showMinorGridLines = value
            minorGridLines.forEach { it.render = value }
        }
    }

    fun getBorderColor(border: Border): MutableColor {
        return borders[border.value].color
    }

    fun getBorderElevation(border: Border): Float {
        return borders[border.value].elevation
    }

    fun getBorders(): Collection<Line> {
        return borders
    }

    fun getMajorLines(): Collection<Line> {
        return majorGridLines
    }

    fun getMinorLines(): Collection<Line> {
        return minorGridLines
    }
}