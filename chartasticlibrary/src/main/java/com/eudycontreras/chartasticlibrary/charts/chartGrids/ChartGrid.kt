package com.eudycontreras.chartasticlibrary.charts.grid

import com.eudycontreras.chartasticlibrary.extensions.dp
import com.eudycontreras.chartasticlibrary.properties.Bounds
import com.eudycontreras.chartasticlibrary.properties.Color
import com.eudycontreras.chartasticlibrary.shapes.Line

/**
 * Created by eudycontreras.
 */


class ChartGrid (
    var padding: Float,
    private var bounds: Bounds
){
    enum class Border(var value: Int) {
        TOP(0),
        LEFT(1),
        RIGHT(2),
        BOTTOM(3)
    }

    private var border: Border = Border.TOP

    private val borders = arrayOf(Line(), Line(), Line(), Line())

    private val horizontalGridLines = ArrayList<Line>()

    var borderThickness: Float = 12.dp

    var borderColor: Color = Color()

    var innerLinesColor: Color = Color()

    fun build() {

        borders[Border.TOP.value].color.setColor(borderColor)
        borders[Border.TOP.value].elevation = borderThickness
        borders[Border.TOP.value].drawShadow = true
        borders[Border.TOP.value].coordinate.x = bounds.coordinate.x + padding
        borders[Border.TOP.value].coordinate.y = bounds.coordinate.y + padding
        borders[Border.TOP.value].dimension.height = borderThickness
        borders[Border.TOP.value].dimension.width = bounds.dimension.width - (padding * 2)

        borders[Border.LEFT.value].color.setColor(borderColor)
        borders[Border.LEFT.value].elevation = borderThickness
        borders[Border.LEFT.value].drawShadow = true
        borders[Border.LEFT.value].coordinate.x = bounds.coordinate.x + padding
        borders[Border.LEFT.value].coordinate.y = bounds.coordinate.y + padding
        borders[Border.LEFT.value].dimension.height = bounds.dimension.height - (padding * 2)
        borders[Border.LEFT.value].dimension.width = borderThickness

        borders[Border.RIGHT.value].color.setColor(borderColor)
        borders[Border.RIGHT.value].elevation = borderThickness
        borders[Border.RIGHT.value].drawShadow = true
        borders[Border.RIGHT.value].coordinate.x = borders[Border.TOP.value].coordinate.x + (borders[Border.TOP.value].dimension.width- (padding / 2))
        borders[Border.RIGHT.value].coordinate.y = bounds.coordinate.y + padding
        borders[Border.RIGHT.value].dimension.height = bounds.dimension.height - (padding * 2)
        borders[Border.RIGHT.value].dimension.width = borderThickness

        borders[Border.BOTTOM.value].color.setColor(borderColor)
        borders[Border.BOTTOM.value].elevation = borderThickness
        borders[Border.BOTTOM.value].drawShadow = true
        borders[Border.BOTTOM.value].coordinate.x = bounds.coordinate.x + padding
        borders[Border.BOTTOM.value].coordinate.y = borders[Border.LEFT.value].coordinate.y + (borders[Border.LEFT.value].dimension.height - (borderThickness))
        borders[Border.BOTTOM.value].dimension.height = borderThickness
        borders[Border.BOTTOM.value].dimension.width = bounds.dimension.width - (padding * 2)

        val thickness = 1.dp

        val top = (borders[Border.TOP.value].coordinate.y) + borderThickness
        val bottom = (borders[Border.BOTTOM.value].coordinate.y)

        val height: Float = (bottom - top)

        val increase = height / (horizontalGridLines.count())

        var offset = 0f

        for(line in horizontalGridLines){
            line.color.setColor(innerLinesColor)
            line.elevation = thickness/2
            line.drawShadow = true
            line.coordinate.x = bounds.coordinate.x + (padding)
            line.coordinate.y = top + offset
            line.dimension.height = thickness
            line.dimension.width = bounds.dimension.width - (padding * 2)
            offset += increase
        }
    }

    fun setHorizontalPointCount(count: Int) {
        horizontalGridLines.clear()
        for (i : Int in 0 until count) {
            horizontalGridLines.add(Line())
        }
    }

    fun showBorder(border: Border, show: Boolean) {
        borders[border.value].render = show
    }

    fun getShapes(): Array<Line> {
        val shapes = ArrayList<Line>()
        shapes.addAll(horizontalGridLines)
        shapes.addAll(borders)
        return shapes.toTypedArray()
    }
}