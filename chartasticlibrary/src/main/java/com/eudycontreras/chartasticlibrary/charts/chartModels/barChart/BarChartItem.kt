package com.eudycontreras.chartasticlibrary.charts.chartModels.barChart

import com.eudycontreras.chartasticlibrary.Shape
import com.eudycontreras.chartasticlibrary.properties.Color
import com.eudycontreras.chartasticlibrary.properties.Gradient
import com.eudycontreras.chartasticlibrary.shapes.Rectangle


/**
 * Created by eudycontreras.
 */
data class BarChartItem<Data>(
    var label: String,
    var value: Any,
    var action: ((Data) -> Unit)? = null
) {
    companion object {
        const val DEFUALT_ROUND_RADIUS = -1f
    }

    private val cornerRadiiMultiplier = 0.75f

    private val shape: Rectangle = Rectangle()

    private var shapes: ArrayList<Shape> = ArrayList()

    var color: Color = Color()
        set(value) {
            field = value
            shape.color = value
        }

    var gradient: Gradient? = null
        set(value) {
            field = value
            field?.let {
                shape.shader = Shape.getShader(it, x, y , x + thickness, y + length)
            }
        }

    var x: Float = 0f
        set(value) {
            field = value
            shape.coordinate.x = field
        }

    var y: Float = 0f
        set(value) {
            field = value
            shape.coordinate.y = field
        }

    var length: Float = 0f
        set(value) {
            field = value
            shape.dimension.height = field
        }

    var thickness: Float = 0f
        set(value) {
            field = value
            shape.dimension.width = field
            if (cornerRadius == DEFUALT_ROUND_RADIUS) {
                shape.corners.apply {
                    this.rx = field * cornerRadiiMultiplier
                    this.ry = field * cornerRadiiMultiplier
                }
            }
        }

    var strokeColor: Color?
        get() = shape.strokeColor
        set(value) {
            shape.strokeColor = value
        }

    var strokeWidth: Float
        get() = shape.strokeWidth
        set(value) {
            shape.showStroke = value  > 0
            shape.strokeWidth = value
        }
    var elevation: Float
        get() = shape.elevation
        set(value) {
            shape.drawShadow = value > 0
            shape.elevation = value
        }

    var roundedTop: Boolean
        get() = shape.corners.topLeft && shape.corners.topRight
        set(value) {
            shape.corners.topLeft = value
            shape.corners.topRight = value
        }

    var roundedBottom: Boolean
        get() = shape.corners.bottomLeft && shape.corners.bottomRight
        set(value) {
            shape.corners.bottomLeft = value
            shape.corners.bottomRight = value
        }

    var cornerRadius: Float = 0f
        set(value) {
            field = value
            if(value >= 0) {
                shape.corners.rx = value
                shape.corners.ry = value
            } else if (value == DEFUALT_ROUND_RADIUS) {
                shape.corners.rx = thickness * cornerRadiiMultiplier
                shape.corners.ry = thickness * cornerRadiiMultiplier
            }
        }

    var backgroundOptions: BackgroundOptions = BackgroundOptions()
        private set(value) {
            field = value
        }

    fun build() {
        shape.color = color
        shape.coordinate.x = x
        shape.coordinate.y = y
        shape.dimension.width = thickness
        shape.dimension.height = length
        shape.shader = gradient?.let {
            Shape.getShader(it, x, y , x + thickness, y + length)
        }

        backgroundOptions.showBackground = backgroundOptions.showBackground
    }

    fun getShapes(): ArrayList<Shape> {
        if (shapes.isEmpty()) {
            shapes = arrayListOf(backgroundOptions.background, shape)
        }
        return shapes
    }

    inner class BackgroundOptions {

        internal val background: Rectangle by lazy {
            Rectangle()
        }

        var padding: Float = 0f
            set(value) {
                field = value
                background.coordinate.x -= value
                background.coordinate.y -= value
                background.dimension.width += value
                background.dimension.height += value
            }

        var color: Color = Color()
            set(value) {
                field = value
                background.color.setColor(field)
            }

        var height: Float = length
            set(value ) {
                field = value
                background.dimension.height = value
                background.dimension.height += (padding * 2)
            }

        var y: Float = 0f
            set(value ) {
                field = value
                background.coordinate.y = value
                background.coordinate.y -= (padding * 2)
            }

        var showBackground: Boolean = false
            set(value) {
                field = value
                background.render = field
                background.coordinate.x = x
                background.coordinate.y = y
                background.dimension.height = height
                background.dimension.width = thickness
                background.corners.copy(shape.corners)
                background.coordinate.x -= padding
                background.coordinate.y -= padding
                background.dimension.width += (padding * 2)
                background.dimension.height += (padding * 2)
            }
    }
}