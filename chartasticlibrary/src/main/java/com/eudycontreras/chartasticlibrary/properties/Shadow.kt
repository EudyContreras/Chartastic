package com.eudycontreras.chartasticlibrary.properties

import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.eudycontreras.chartasticlibrary.Shape
import com.eudycontreras.chartasticlibrary.utilities.extensions.drawRoundRect
import com.eudycontreras.chartasticlibrary.utilities.global.mapRange
import com.eudycontreras.chartasticlibrary.shapes.Circle


/**
 * Created by eudycontreras.
 */

class Shadow {

    private var shadowColorStart = MutableColor.fromColor(DefaultColor)
    private var shadowColorEnd = MutableColor.fromColor(DefaultColor)

    var shadowColor: MutableColor
        get() = shadowColorStart
        set(value) {
            shadowColorStart.setColor(value).updateAlpha(85)
            shadowColorEnd.setColor(value).updateAlpha(0)
        }

    var minStepCount = DEFAULT_MIN_STEP_COUNT
        set(value) {
            field = when {
                value < DEFAULT_MIN_STEP_COUNT -> DEFAULT_MIN_STEP_COUNT
                value > maxStepCount -> maxStepCount
                else -> value
            }
        }

    var maxStepCount = DEFAULT_MAX_STEP_COUNT - 2
        set(value) {
            field = when {
                value < DEFAULT_MIN_STEP_COUNT -> DEFAULT_MIN_STEP_COUNT
                value > DEFAULT_MAX_STEP_COUNT -> DEFAULT_MAX_STEP_COUNT
                else -> value
            }
        }

    init {
        shadowColor = MutableColor.fromColor(DefaultColor)
    }

    companion object {

        private val shadowPaint = Paint(0).apply {
            maskFilter = BlurMaskFilter(8f, BlurMaskFilter.Blur.NORMAL)
        }

        const val DEFAULT_MAX_STEP_COUNT = 20f
        const val DEFAULT_MIN_STEP_COUNT = 0f

        val DefaultColor: Color = MutableColor.rgb(35,35,35)
    }

    private var shiftLeft: Float = 0f
    private var shiftRight: Float = 0f
    private var shiftTop: Float = 0f
    private var shiftBottom: Float = 0f

    private var elevation = -1f

    private var position: LightSource.Position = LightSource.Position.CENTER

    fun computeShift(shape: Shape, position: LightSource.Position) {
        if (elevation == shape.elevation && this.position == position) {
            return
        }
        this.elevation = shape.elevation
        this.position = position

        when (position) {
            LightSource.Position.TOP_LEFT -> {
                shiftLeft = -(elevation/2)
                shiftRight = 0f
                shiftTop = -(elevation/2)
                shiftBottom = 0f
            }
            LightSource.Position.TOP_RIGHT -> {
                shiftLeft = 0f
                shiftRight = -(elevation/2)
                shiftTop = -(elevation/2)
                shiftBottom = 0f
            }
            LightSource.Position.BOTTOM_LEFT -> {
                shiftRight = 0f
                shiftLeft = -(elevation/2)
                shiftBottom = -(elevation/2)
                shiftTop = 0f
            }
            LightSource.Position.BOTTOM_RIGHT -> {
                shiftRight = -(elevation/2)
                shiftLeft = 0f
                shiftBottom = -(elevation/2)
                shiftTop = 0f
            }
            LightSource.Position.TOP_LEFT_RIGHT -> {
                shiftTop = -(elevation/2)
                shiftRight = 0f
                shiftLeft = 0f
                shiftBottom = 0f
            }
            LightSource.Position.BOTTOM_LEFT_RIGHT -> {
                shiftTop = 0f
                shiftRight = 0f
                shiftLeft = 0f
                shiftBottom = -(elevation/2)
            }
            LightSource.Position.TOP_LEFT_BOTTOM -> {
                shiftTop = 0f
                shiftRight = 0f
                shiftLeft = -(elevation/2)
                shiftBottom = 0f
            }
            LightSource.Position.TOP_RIGHT_BOTTOM -> {
                shiftTop = 0f
                shiftRight = -(elevation/2)
                shiftLeft = 0f
                shiftBottom = 0f
            }
            LightSource.Position.CENTER -> {
                shiftTop = 0f
                shiftRight = 0f
                shiftLeft = 0f
                shiftBottom = 0f
            }
        }
    }

    fun draw(shape: Shape, path: Path, paint: Paint, canvas: Canvas) {
        path.reset()

        val left = shape.coordinate.x
        val right = shape.coordinate.x + shape.dimension.width
        val top = shape.coordinate.y
        val bottom = shape.coordinate.y + shape.dimension.height

        val shiftedLeft = left - shiftLeft
        val shiftedTop = top - shiftTop
        val shiftedRight = right + shiftRight
        val shiftedBottom = bottom + shiftBottom

        val color: MutableColor = MutableColor.fromColor(shadowColorStart)

        var steps = Math.round(mapRange(
            shape.elevation,
            Shape.MinElevation,
            Shape.MaxElevation,
            minStepCount,
            maxStepCount
        ))

        if (steps <= 0) steps = 1

        for (i in 0..shape.elevation.toInt() step steps) {
            val amount = mapRange(
                i.toFloat(),
                0f,
                shape.elevation,
                shadowColorStart.getOpacity(),
                shadowColorEnd.getOpacity()
            )

            color.updateAlpha(amount)
            paint.color = color.toColor()

            canvas.drawRoundRect(
                path,
                shiftedLeft - i,
                shiftedTop - i ,
                shiftedRight + i,
                shiftedBottom + i,
                shape.corners.rx + i,
                shape.corners.ry + i,
                shape.corners,
                paint
            )
        }
    }

    fun draw(shape: Shape, paint: Paint, canvas: Canvas) {
        paint.setShadowLayer(shape.elevation, -shape.elevation, -shape.elevation, shadowColorStart.toColor())
    }

    fun drawOval(shape: Circle, paint: Paint, canvas: Canvas) {
        val left = shape.coordinate.x
        val right = shape.coordinate.x + shape.dimension.width
        val top = shape.coordinate.y
        val bottom = shape.coordinate.y + shape.dimension.height

        val shiftedLeft = left - shiftLeft
        val shiftedTop = top - shiftTop
        val shiftedRight = right + shiftRight
        val shiftedBottom = bottom + shiftBottom

        val color: MutableColor = MutableColor.fromColor(shadowColorStart)

        val steps = mapRange(
            shape.elevation,
            Shape.MinElevation,
            Shape.MaxElevation,
            minStepCount,
            maxStepCount
        ).toInt()

        for(i: Int in 0..shape.elevation.toInt() step steps) {

            val amount = mapRange(
                i.toFloat(),
                0f,
                shape.elevation,
                shadowColorStart.getOpacity(),
                shadowColorEnd.getOpacity()
            )

            color.updateAlpha(amount)
            paint.color = color.toColor()

            canvas.drawOval(
                shiftedLeft - i,
                shiftedTop - i ,
                shiftedRight + i,
                shiftedBottom + i,
                paint
            )
        }
    }

    private fun getDistance(fromX: Float, fromY: Float, toX: Float, toY: Float): Float{
        val distanceX = (toX - fromX) * (toX - fromX).toDouble()
        val distanceY = (toY - fromY) * (toY - fromY).toDouble()

        return Math.sqrt( distanceY + distanceX).toFloat()
    }
}