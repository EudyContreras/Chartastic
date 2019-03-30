package com.eudycontreras.chartasticlibrary

import android.graphics.*
import android.view.MotionEvent
import com.eudycontreras.chartasticlibrary.properties.*
import com.eudycontreras.chartasticlibrary.utilities.extensions.dp
import com.eudycontreras.chartasticlibrary.utilities.global.ShapeStyle

/**
 * Created by eudycontreras.
 */

abstract class Shape {

    var ownerId: Int = -1

    val corners: CornerRadii by lazy { CornerRadii() }

    var render: Boolean = true

    var showStroke: Boolean = false
        get() = field && strokeWidth > 0

    var shadowType: Shadow.Type = Shadow.Type.OUTER

    var shadowPosition: LightSource.Position = LightSource.Position.BOTTOM_LEFT

    var touchProcessor: ((Shape, MotionEvent, Float, Float) -> Unit)? = null

    var style: ShapeStyle? = null

    var color: MutableColor = MutableColor()

    open var bounds: Bounds = Bounds()

    var left: Float
        get() = coordinate.x
        set(value) {
            coordinate.x = value
        }

    var top: Float
        get() = coordinate.y
        set(value) {
            coordinate.y = value
        }

    var bottom: Float
        get() = top + dimension.height
        set(value) {
            dimension.height = value - top
        }

    var right: Float
        get() = left + dimension.width
        set(value) {
            dimension.width = value - left
        }

    var coordinate: Coordinate
        get() = bounds.coordinate
        set(value) {
            bounds.coordinate = value
        }

    var dimension: Dimension
        get() = bounds.dimension
        set(value) {
            bounds.dimension = value
        }

    var elevation: Float = 0f
        set(value) {
            field = value
            drawShadow = value > 0f
        }

    var drawShadow: Boolean = false
        get() = field && elevation > 0
        set(value) {
            field = value
            if (shadow == null) {
                if (value) {
                    shadow = Shadow()
                }
            }
        }

    var strokeWidth: Float = 0f

    var strokeColor: MutableColor? = null

    var shadow: Shadow? = null

    var shader: Shader? = null

    fun reset() {
        coordinate.x = 0f
        coordinate.y = 0f

        dimension.width = 0f
        dimension.height = 0f

        color.setColor(0, 0, 0, 255)

        elevation = 0f
        corners.reset()

        showStroke = false
        drawShadow = false
        shadowType = Shadow.Type.OUTER
        shadowPosition = LightSource.Position.BOTTOM_LEFT

        strokeWidth = 0f
        strokeColor = null
        style = null

        shadow = null
        shader = null
        render = true

    }

    fun update(delta: Float) {}

    abstract fun render(
        path: Path,
        paint: Paint,
        canvas: Canvas,
        renderingProperties: ShapeRenderer.RenderingProperties
    )

    companion object {
        val MaxElevation = 50.dp
        val MinElevation = 0.dp

        fun getShader(
            gradient: Gradient,
            x: Float,
            y: Float,
            width: Float,
            height: Float
        ): Shader {
            val zero = 0f
            when (gradient.type) {
                Gradient.TOP_TO_BOTTOM -> return LinearGradient(
                    zero,
                    y,
                    zero,
                    y + height,
                    gradient.colors.map { it.toColor() }.toIntArray(),
                    getPositions(gradient.colors.size),
                    Shader.TileMode.MIRROR
                )
                Gradient.BOTTOM_TO_TOP -> return LinearGradient(
                    zero,
                    y + height,
                    zero,
                    y,
                    gradient.colors.map { it.toColor() }.toIntArray(),
                    getPositions(gradient.colors.size),
                    Shader.TileMode.CLAMP
                )
                Gradient.LEFT_TO_RIGHT -> return LinearGradient(
                    x,
                    zero,
                    x + width,
                    zero,
                    gradient.colors.map { it.toColor() }.toIntArray(),
                    getPositions(gradient.colors.size),
                    Shader.TileMode.CLAMP
                )
                Gradient.RIGHT_TO_LEFT -> return LinearGradient(
                    x + width,
                    zero,
                    x,
                    zero,
                    gradient.colors.map { it.toColor() }.toIntArray(),
                    getPositions(gradient.colors.size),
                    Shader.TileMode.CLAMP
                )
                else -> return LinearGradient(
                    x,
                    y,
                    width,
                    height,
                    gradient.colors.map { it.toColor() }.toIntArray(),
                    getPositions(gradient.colors.size),
                    Shader.TileMode.CLAMP
                )
            }
        }

        private fun getPositions(count: Int): FloatArray {
            val value: Float = 1f / (count - 1)
            val array = ArrayList<Float>()
            var increase = value
            array.add(0f)
            for (i in 0 until count - 1) {
                array.add(increase)
                increase += (value)
            }
            return array.toFloatArray()
        }
    }
}