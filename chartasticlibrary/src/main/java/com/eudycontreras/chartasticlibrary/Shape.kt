package com.eudycontreras.chartasticlibrary

import android.graphics.*
import com.eudycontreras.chartasticlibrary.extensions.dp
import com.eudycontreras.chartasticlibrary.properties.*
import com.eudycontreras.chartasticlibrary.properties.Color

/**
 * Created by eudycontreras.
 */

abstract class Shape(
    var coordinate: Coordinate = Coordinate(),
    var dimension: Dimension = Dimension(),
    var color: Color = Color(),
    var elevation: Float = 0f
) {
    var corners: CornerRadii = CornerRadii()

    var render: Boolean = true

    var showStroke: Boolean = false
    var drawShadow: Boolean = false

    var strokeWidth: Float = 0f

    var strokeColor: Color? = null

    var path: Path = Path()

    var shadow: Shadow? = null

    var shader: Shader? = null

    fun recycle() {
        coordinate.x = 0f
        coordinate.y = 0f

        dimension.width = 0f
        dimension.height = 0f

        color.setColor(0, 0, 0, 255)

        elevation = 0f
        corners.reset()

        showStroke = false
        drawShadow = false

        strokeWidth = 0f
        strokeColor = null

        path.reset()

        shadow = null
        shader = null

    }

    fun getBounds() : Bounds {
        return Bounds(coordinate, dimension)
    }

    fun update(delta: Float) {

    }

    abstract fun render(paint: Paint, canvas: Canvas?, renderingProperties: ShapeRenderer.RenderingProperties)

    companion object {
        val MaxElevation = 50.dp
        val MinElevation = 0.dp

        fun getShader(
            gradient: Gradient,
            left: Float,
            top: Float,
            right: Float,
            bottom: Float
        ): Shader {
            when (gradient.type) {
                Gradient.TOP_TO_BOTTOM -> return LinearGradient(
                    left,
                    top,
                    right,
                    bottom,
                    gradient.colorOne.toColor(),
                    gradient.colorTwo.toColor(),
                    Shader.TileMode.CLAMP
                )
                Gradient.BOTTOM_TO_TOP -> return LinearGradient(
                    left,
                    top,
                    right,
                    bottom,
                    gradient.colorOne.toColor(),
                    gradient.colorTwo.toColor(),
                    Shader.TileMode.CLAMP
                )
                Gradient.LEFT_TO_RIGHT -> return LinearGradient(
                    left,
                    top,
                    right,
                    bottom,
                    gradient.colorOne.toColor(),
                    gradient.colorTwo.toColor(),
                    Shader.TileMode.CLAMP
                )
                Gradient.RIGHT_TO_LEFT -> return LinearGradient(
                    left,
                    top,
                    right,
                    bottom,
                    gradient.colorOne.toColor(),
                    gradient.colorTwo.toColor(),
                    Shader.TileMode.CLAMP
                )
                else -> return LinearGradient(
                    left,
                    top,
                    right,
                    bottom,
                    gradient.colorOne.toColor(),
                    gradient.colorTwo.toColor(),
                    Shader.TileMode.CLAMP
                )
            }
        }
    }
}