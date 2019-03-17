package com.eudycontreras.chartasticlibrary.charts.chart_grid

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.eudycontreras.chartasticlibrary.ShapeRenderer
import com.eudycontreras.chartasticlibrary.charts.ChartElement
import com.eudycontreras.chartasticlibrary.charts.ChartLayoutManager
import com.eudycontreras.chartasticlibrary.charts.chart_model.bar_chart.BarChartData
import com.eudycontreras.chartasticlibrary.properties.Bounds
import com.eudycontreras.chartasticlibrary.properties.Coordinate
import com.eudycontreras.chartasticlibrary.properties.Dimension
import com.eudycontreras.chartasticlibrary.properties.MutableColor
import com.eudycontreras.chartasticlibrary.shapes.Line
import com.eudycontreras.chartasticlibrary.utilities.extensions.dp

/**
 * Created by eudycontreras.
 */

class ChartGrid(private val layoutManager: ChartLayoutManager): ChartElement {

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

    private val majorGridLines = ArrayList<Line>()
    private val minorGridLines = ArrayList<Line>()

    lateinit var data: BarChartData

    lateinit var gridAxisY: ChartGridAxisY

    lateinit var gridAxisX: ChartGridAxisX

    val maxY: Any?
        get() {
            return if(::gridAxisY.isInitialized) {
                gridAxisY.values?.maxValue
            } else {
                null
            }
        }

    val minY: Any?
        get() {
            return if(::gridAxisY.isInitialized) {
                gridAxisY.values?.minValue
            } else {
                null
            }
        }
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
    }

    fun build(bounds: Bounds = Bounds()) {
        this.bounds.update(bounds)
    }

    fun buildMajorLines() {

        if (!::gridAxisY.isInitialized) {
            return
        }

        majorGridLines.clear()

        val placement = LinePlacement.ALIGNED
        val values = gridAxisY.values?.valuesBuildData?.textElements!!

        if (values.isEmpty()) {
            return
        }

        val topX = gridAxisY.axisLabelBounds.right
        val topWidth = bounds.drawableArea.width

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

                    if (gridAxisY.type == ChartGridAxisY.Type.LEFT) {
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

        recreateDrawableZone(majorGridLines)
    }

    private fun recreateDrawableZone(majorGridLines: ArrayList<Line>){
        val top = majorGridLines[0]
        val bottom = majorGridLines[majorGridLines.size - 1]

        val coordinates = Coordinate().apply {
            x = top.left
            y = top.top
        }

        val dimension = Dimension().apply {
            width = (top.right - top.left)
            height = (bottom.bottom - top.top)
        }

        this.bounds.update(Bounds(coordinates, dimension))
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

    fun showGridLines(value: Boolean, lineType: GridLineType) {
        if (lineType == GridLineType.MAJOR) {
            this.showMajorGridLines = value
            majorGridLines.forEach { it.render = value }
        } else {
            this.showMinorGridLines = value
            minorGridLines.forEach { it.render = value }
        }
    }

    fun getMajorLines(): Collection<Line> {
        return majorGridLines
    }

    fun getMinorLines(): Collection<Line> {
        return minorGridLines
    }
}