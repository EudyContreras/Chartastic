package com.eudycontreras.chartasticlibrary.charts.chartModels.barChart

import com.eudycontreras.chartasticlibrary.Shape
import com.eudycontreras.chartasticlibrary.extensions.dp
import com.eudycontreras.chartasticlibrary.properties.Color
import com.eudycontreras.chartasticlibrary.properties.CornerRadii
import com.eudycontreras.chartasticlibrary.shapes.Rectangle

/**
 * Created by eudycontreras.
 */
data class BarChartItemBackground<Data>(
    var label: String,
    var value: Any,
    var color: Color,
    var action: ((Data) -> Unit)? = null
) {
    var shape: Rectangle = Rectangle()

    var length: Float = 0f
    var thickness: Float = 0f

    var roundedEnd: Boolean = false

    var x: Float = 0f
    var y: Float = 0f

    fun build() {
        shape.color = color
        shape.coordinate.x = x
        shape.coordinate.y = y
        shape.strokeWidth = 2.dp
        shape.strokeColor = color.adjust(1.15f).subtractRed(10).subtractGreen(10)
        shape.dimension.width = thickness
        shape.dimension.height = length
        shape.corners = if (roundedEnd) {
            CornerRadii().apply {
                this.rx = thickness / 2
                this.ry = thickness / 2
                this.topLeft = true
                this.topRight = true
            }
        } else {
            CornerRadii()
        }
        shape.elevation = 10.dp
        shape.drawShadow = false
        shape.showStroke = false
    }

    fun getShape() : Shape {
        return shape
    }
}