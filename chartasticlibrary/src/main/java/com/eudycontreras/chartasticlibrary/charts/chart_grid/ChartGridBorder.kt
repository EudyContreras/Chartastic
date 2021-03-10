package com.eudycontreras.chartasticlibrary.charts.chart_grid

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.eudycontreras.chartasticlibrary.ShapeRenderer
import com.eudycontreras.chartasticlibrary.charts.ChartElement
import com.eudycontreras.chartasticlibrary.properties.Bounds
import com.eudycontreras.chartasticlibrary.properties.LightSource
import com.eudycontreras.chartasticlibrary.properties.MutableColor
import com.eudycontreras.chartasticlibrary.shapes.Line
import com.eudycontreras.chartasticlibrary.utilities.extensions.dp

/**
 * Created by eudycontreras.
 */
class ChartGridBorder: ChartElement {
    override var render: Boolean = true

    override fun render(
        path: Path,
        paint: Paint,
        canvas: Canvas,
        renderingProperties: ShapeRenderer.RenderingProperties
    ) {
        if (!render)
            return

        borders.forEach { it.render(path, paint, canvas, renderingProperties) }
    }

    enum class Border(var value: Int) {
        TOP(0),
        LEFT(1),
        RIGHT(2),
        BOTTOM(3),
        NONE(4),
        ALL(-1),
    }

    private var bounds: Bounds? = null

    private var borderLineThickness = 1.dp

    private val borders = arrayListOf(Line(), Line(), Line(), Line())

    fun build(bounds: Bounds) {
        this.bounds = bounds

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

        bounds?.let {build(it)}
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

    fun getBorderColor(border: Border): MutableColor {
        return borders[border.value].color
    }

    fun getBorderElevation(border: Border): Float {
        return borders[border.value].elevation
    }

    fun getBorders(): Collection<Line> {
        return borders
    }
}