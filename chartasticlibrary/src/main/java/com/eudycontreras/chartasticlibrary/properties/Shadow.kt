package com.eudycontreras.chartasticlibrary.properties

import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.eudycontreras.chartasticlibrary.Shape
import com.eudycontreras.chartasticlibrary.utilities.extensions.drawRoundRect
import com.eudycontreras.chartasticlibrary.utilities.global.mapRange


/**
 * Created by eudycontreras.
 */

class Shadow {

    private var shadowColorStart = MutableColor.fromColor(DefaultColor)
    private var shadowColorEnd = MutableColor.fromColor(DefaultColor)

    enum class Type {
        INNER,
        OUTER
    }

    companion object {

        private val shadowPaint = Paint(0).apply {
            maskFilter = BlurMaskFilter(8f, BlurMaskFilter.Blur.NORMAL)
        }

        const val DEFAULT_MAX_STEP_COUNT = 20f
        const val DEFAULT_MIN_STEP_COUNT = 1f

        val DefaultColor: Color = MutableColor.rgb(35, 35, 35)
    }

    var shadowType: Type = Type.OUTER

    var shadowColor: MutableColor
        get() = shadowColorStart
        set(value) {
            shadowColorStart.setColor(value).updateAlpha(55)
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

    private var color: MutableColor = MutableColor.fromColor(shadowColorStart)

    private var shiftLeft: Float = 0f
    private var shiftRight: Float = 0f
    private var shiftTop: Float = 0f
    private var shiftBottom: Float = 0f

    private var shift = -1f

    private var steps = 0

    private var position: LightSource.Position = LightSource.Position.CENTER

    init {
        shadowColor = MutableColor.fromColor(DefaultColor)
    }

    fun computeShift(shape: Shape, position: LightSource.Position) {
        if (shift == shape.elevation && this.position == position) {
            return
        }

        this.shift = shape.elevation
        this.position = position

        when (position) {
            LightSource.Position.TOP_LEFT -> {
                shiftLeft = -(shift / 2)
                shiftRight = 0f
                shiftTop = -(shift / 2)
                shiftBottom = 0f
            }
            LightSource.Position.TOP_RIGHT -> {
                shiftLeft = 0f
                shiftRight = -(shift / 2)
                shiftTop = -(shift / 2)
                shiftBottom = 0f
            }
            LightSource.Position.BOTTOM_LEFT -> {
                shiftRight = 0f
                shiftLeft = -(shift / 2)
                shiftBottom = -(shift / 2)
                shiftTop = 0f
            }
            LightSource.Position.BOTTOM_RIGHT -> {
                shiftRight = -(shift / 2)
                shiftLeft = 0f
                shiftBottom = -(shift / 2)
                shiftTop = 0f
            }
            LightSource.Position.TOP_LEFT_RIGHT -> {
                shiftTop = -(shift / 2)
                shiftRight = 0f
                shiftLeft = 0f
                shiftBottom = 0f
            }
            LightSource.Position.BOTTOM_LEFT_RIGHT -> {
                shiftTop = 0f
                shiftRight = 0f
                shiftLeft = 0f
                shiftBottom = -(shift / 2)
            }
            LightSource.Position.TOP_LEFT_BOTTOM -> {
                shiftTop = 0f
                shiftRight = 0f
                shiftLeft = -(shift / 2)
                shiftBottom = 0f
            }
            LightSource.Position.TOP_RIGHT_BOTTOM -> {
                shiftTop = 0f
                shiftRight = -(shift / 2)
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

        color = MutableColor.fromColor(shadowColorStart)

        steps = Math.round(
            mapRange(
                shape.elevation,
                Shape.MinElevation,
                Shape.MaxElevation,
                minStepCount,
                maxStepCount
            )
        )

        if (steps <= 0) steps = 1
    }

    fun draw(shape: Shape, path: Path, paint: Paint, canvas: Canvas, polygon: Boolean = false) {
        if (shadowType == Type.INNER) {
            drawInnerShadow(shape, path, paint, canvas, polygon)
        }
        else {
            drawOuterShadow(shape, path, paint, canvas, polygon)
        }
    }

    private fun drawInnerShadow(shape: Shape, path: Path, paint: Paint, canvas: Canvas, polygon: Boolean) {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f + steps

        val left = shape.coordinate.x
        val right = shape.coordinate.x + shape.dimension.width
        val top = shape.coordinate.y
        val bottom = shape.coordinate.y + shape.dimension.height

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
                left + if (shiftBottom != 0f) i else 0,
                top + if (shiftBottom != 0f) i else 0,
                right - if (shiftLeft != 0f) i else 0,
                bottom - if (shiftTop != 0f) i else 0,
                shape.corners.rx,
                shape.corners.ry,
                shape.corners,
                paint
            )
        }
    }

   private fun drawOuterShadow(shape: Shape, path: Path, paint: Paint, canvas: Canvas, polygon: Boolean) {
       val shiftedLeft = shape.coordinate.x - shiftLeft
       val shiftedTop = shape.coordinate.y - shiftTop
       val shiftedRight = (shape.coordinate.x + shape.dimension.width) + shiftRight
       val shiftedBottom = (shape.coordinate.y + shape.dimension.height) + shiftBottom

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
               shiftedTop - i,
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

    fun drawShadow(shape: Shape, paint: Paint, canvas: Canvas) {
        draw(shape.left, shape.top, shape.right, shape.bottom, shape.elevation, paint, canvas)
    }

    fun draw(left: Float, top: Float, right: Float, bottom: Float, elevation: Float, paint: Paint, canvas: Canvas) {
        if (shadowType == Type.INNER) {
            paint.style = Paint.Style.STROKE
        }

        val shiftedLeft = left - shiftLeft
        val shiftedTop = top - shiftTop
        val shiftedRight = right + shiftRight
        val shiftedBottom = bottom + shiftBottom

        val color: MutableColor = MutableColor.fromColor(shadowColorStart)

        var steps = mapRange(
            elevation,
            Shape.MinElevation,
            Shape.MaxElevation,
            minStepCount,
            maxStepCount
        ).toInt()

        if (steps <= 0) steps = 1

        paint.strokeWidth = steps.toFloat()

        for (i: Int in 0..elevation.toInt() step steps) {

            val amount = mapRange(
                i.toFloat(),
                0f,
                elevation,
                shadowColorStart.getOpacity(),
                shadowColorEnd.getOpacity()
            )

            color.updateAlpha(amount)
            paint.color = color.toColor()

            when (shadowType) {
                Type.INNER -> {
                    canvas.drawOval(
                        shiftedLeft + i,
                        shiftedTop + i,
                        shiftedRight - i,
                        shiftedBottom - i,
                        paint
                    )
                }
                Type.OUTER -> {
                    canvas.drawOval(
                        shiftedLeft - i,
                        shiftedTop - i,
                        shiftedRight + i,
                        shiftedBottom + i,
                        paint
                    )
                }
            }
        }
    }

    private fun getDistance(fromX: Float, fromY: Float, toX: Float, toY: Float): Float {
        val distanceX = (toX - fromX) * (toX - fromX).toDouble()
        val distanceY = (toY - fromY) * (toY - fromY).toDouble()

        return Math.sqrt(distanceY + distanceX).toFloat()
    }
}