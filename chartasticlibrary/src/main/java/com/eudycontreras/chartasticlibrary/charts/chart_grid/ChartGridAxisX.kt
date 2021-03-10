package com.eudycontreras.chartasticlibrary.charts.chart_grid

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Typeface
import com.eudycontreras.chartasticlibrary.Shape
import com.eudycontreras.chartasticlibrary.ShapeRenderer
import com.eudycontreras.chartasticlibrary.charts.ChartElement
import com.eudycontreras.chartasticlibrary.charts.ChartLayoutManager
import com.eudycontreras.chartasticlibrary.charts.chart_model.bar_chart.BarChart
import com.eudycontreras.chartasticlibrary.charts.chart_model.bar_chart.BarChartItem
import com.eudycontreras.chartasticlibrary.charts.chart_options.AxisXOptions
import com.eudycontreras.chartasticlibrary.charts.interfaces.ChartBoundsOwner
import com.eudycontreras.chartasticlibrary.properties.Bounds
import com.eudycontreras.chartasticlibrary.properties.Coordinate
import com.eudycontreras.chartasticlibrary.properties.MutableColor
import com.eudycontreras.chartasticlibrary.properties.Padding
import com.eudycontreras.chartasticlibrary.shapes.BoundingBox
import com.eudycontreras.chartasticlibrary.shapes.Line
import com.eudycontreras.chartasticlibrary.utilities.extensions.dp
import com.eudycontreras.chartasticlibrary.utilities.extensions.sp
import com.eudycontreras.chartasticlibrary.utilities.global.BoundsChangeListener

/**
 * Created by eudycontreras.
 */

class ChartGridAxisX(
    private val barChart: BarChart,
    private val layoutManager: ChartLayoutManager,
    _type: Type = Type.BOTTOM
) : ChartBoundsOwner, ChartElement {


    enum class Type {
        TOP,
        BOTTOM
    }

    enum class ValueType {
        ALPHABETIC,
        NUMERIC
    }

    companion object {
        val DEFAULT_PADDING = Padding(2.dp, 6.dp, 0.dp, 0.dp)
        val DEFAULT_LABEL_TEXT_SIZE = 9.sp
        val DEFAULT_LABEL_TYPEFACE: Typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
        val DEFAULT_LABEL_COLOR = MutableColor.rgba(255, 255, 255, 1f)
    }

    var type: Type = _type
        set(value) {
            layoutManager.removeBoundsOwner(this)
            field = value
            layoutManager.addBoundsOwner(this)
        }

    var valueType: ValueType = ValueType.NUMERIC

    private val boundsBox: Shape by lazy {
        BoundingBox().apply {
            this.bounds.update(this@ChartGridAxisX.bounds.drawableArea)
        }
    }

    override val changeListeners: ArrayList<BoundsChangeListener> by lazy {
        ArrayList<BoundsChangeListener>()
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

    override val bounds: Bounds = Bounds(this)

    override val anchor: ChartLayoutManager.BoundsAnchor
        get() {
            return when (type) {
                Type.TOP -> ChartLayoutManager.BoundsAnchor.TOP
                Type.BOTTOM -> ChartLayoutManager.BoundsAnchor.BOTTOM
            }
        }

    private var tickLines = ArrayList<Line>()

    var values: ChartGridAxisY.ValueCalculation? = null

    var axisLabelBounds: Bounds = Bounds()

    var options: AxisXOptions = AxisXOptions()

    init {
        layoutManager.addBoundsOwner(this)
        build(this.bounds)
    }

    fun build(bounds: Bounds = Bounds()) {
        this.bounds.update(bounds)
    }

    override fun notifyBoundsChange(bounds: Bounds) {
        if(bounds != this.bounds) {
            this.bounds.update(bounds, false)
        }
        layoutManager.notifyBoundsChange(this)
    }

    override fun propagateNewBounds(bounds: Bounds) {
        this.bounds.update(bounds, false)

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
    ){
        if (!render || !computeBounds) {
            return
        }
        if(layoutManager.showBoundingBoxes){
            boundsBox.render(path, paint, canvas, renderingProperties)
        }

        if (options.showTickLines) {
            tickLines.forEach { it.render(path, paint, canvas, renderingProperties) }
        }
    }

    fun buildBarTicks(majorLines: Collection<Line>) {
        val chartItems = barChart.data.chartItems

        when (type) {
            Type.BOTTOM -> {
                buildBottomTicks(majorLines, chartItems)
            }
            Type.TOP -> {
                buildTopTicks(majorLines, chartItems)
            }
        }
    }

    fun buildTopTicks(majorLines: Collection<Line>, chartItems: ArrayList<BarChartItem<out Any>>){
        if(chartItems.isEmpty()) {
            return
        }

        tickLines.clear()

        val first = majorLines.first()

        val y = (first.top + (first.dimension.height / 2)) - (options.tickWidth / 2)

        for (item in chartItems) {
            val x = item.x + (item.thickness / 2)

            val tick = Line()
            tick.color.setColor(options.tickColor)
            tick.elevation = options.tickElevation
            tick.render = options.showTickLines
            tick.coordinate.x = x -(options.tickWidth / 2)
            tick.coordinate.y = y -options.tickLength
            tick.dimension.width = options.tickWidth
            tick.dimension.height = options.tickLength

            tickLines.add(tick)
        }

        if (options.showTickLineBar) {
            val line = Line()

            line.color.setColor(options.tickColor)
            line.elevation = 0f
            line.drawShadow = false
            line.render = options.showTickLineBar
            line.coordinate.x = bounds.drawableArea.left
            line.coordinate.y = y
            line.dimension.width = bounds.drawableArea.width
            line.dimension.height = options.tickWidth

            tickLines.add(line)
        }
    }

    fun buildBottomTicks(majorLines: Collection<Line>, chartItems: ArrayList<BarChartItem<out Any>>) {
        if(chartItems.isEmpty()) {
            return
        }
        tickLines.clear()

        val last = majorLines.last()

        val y = (last.top + (last.dimension.height / 2)) - (options.tickWidth / 2)

        for (item in chartItems) {
            val x = item.x + (item.thickness / 2)

            val tick = Line()
            tick.color.setColor(options.tickColor)
            tick.elevation = options.tickElevation
            tick.render = options.showTickLines
            tick.coordinate.x = x -(options.tickWidth / 2)
            tick.coordinate.y = y + options.tickWidth
            tick.dimension.width = options.tickWidth
            tick.dimension.height = options.tickLength

            tickLines.add(tick)
        }

        if (options.showTickLineBar) {
            val line = Line()

            line.color.setColor(options.tickColor)
            line.elevation = 0f
            line.drawShadow = false
            line.render = options.showTickLineBar
            line.coordinate.x = bounds.drawableArea.left
            line.coordinate.y = y
            line.dimension.width = bounds.drawableArea.width
            line.dimension.height = options.tickWidth

            tickLines.add(line)
        }
    }

    fun buildSepartorLines(){

    }

    fun buildGroupSeparatorLines() {

    }

    data class ValueCalculation(
        val values: List<Any>,
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
