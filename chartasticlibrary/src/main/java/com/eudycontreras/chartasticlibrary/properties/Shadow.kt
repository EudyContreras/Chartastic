package com.eudycontreras.chartasticlibrary.properties

import android.graphics.Canvas
import android.graphics.Paint
import com.eudycontreras.chartasticlibrary.Shape
import com.eudycontreras.chartasticlibrary.mapRange


/**
 * Created by eudycontreras.
 */

class Shadow {

    private var shadowColorStart = Color(45,35,35,35)
    private var shadowColorEnd= Color(0,35,35,35)

    var minStepCount = 1
    var maxStepCount = 10

    var shiftLeft: Float = 0f
    var shiftRight: Float = 0f
    var shiftTop: Float = 0f
    var shiftBottom: Float = 0f

    var position: LightSource.Position = LightSource.Position.CENTER

    fun computeShift(shape: Shape, lightSource: LightSource) {
        TODO()
    }

    fun computeShift(shape: Shape, position: LightSource.Position) {
        val elevation = shape.elevation
        this.position = position

        when (position) {
            LightSource.Position.TOP_LEFT -> {
                shiftLeft = elevation
                shiftRight = -(elevation * 1.5f)
                shiftTop = elevation
                shiftBottom = -(elevation * 1.5f)
            }
            LightSource.Position.TOP_RIGHT -> {
                shiftRight = elevation
                shiftLeft = -(elevation * 1.5f)
                shiftTop = elevation
                shiftBottom = -(elevation * 1.5f)
            }
            LightSource.Position.BOTTOM_LEFT -> {
                shiftLeft = elevation
                shiftRight = -(elevation * 1.5f)
                shiftBottom = elevation
                shiftTop = -(elevation * 1.5f)
            }
            LightSource.Position.BOTTOM_RIGHT -> {
                shiftRight = elevation
                shiftLeft = -(elevation * 1.5f)
                shiftBottom = elevation
                shiftTop = -(elevation * 1.5f)
            }
            else -> {
                shiftLeft = 0f
                shiftRight = 0f
                shiftTop = 0f
                shiftBottom = 0f
            }
        }
    }

    fun draw(shape: Shape, paint: Paint, canvas: Canvas) {

        val left = shape.coordinate.x
        val right = shape.coordinate.x + shape.dimension.width
        val top = shape.coordinate.y
        val bottom = shape.coordinate.y + shape.dimension.height

        var shiftedLeft = left - shiftLeft
        var shiftedTop = top - shiftTop
        var shiftedRight = right + shiftRight
        var shiftedBottom = bottom + shiftBottom

        when (position) {

            LightSource.Position.TOP_LEFT -> {
                shiftedLeft = left
                shiftedTop = top
            }
            LightSource.Position.TOP_RIGHT -> {
                shiftedRight = right
                shiftedTop = top
            }
            LightSource.Position.BOTTOM_LEFT -> {
                shiftedLeft = left
                shiftedBottom = bottom
            }
            LightSource.Position.BOTTOM_RIGHT -> {
                shiftedRight = right
                shiftedBottom = bottom
            }
            LightSource.Position.CENTER -> {}
        }

        val color: Color = Color.fromColor(shadowColorStart)

        val steps = mapRange(shape.elevation, Shape.MinElevation, Shape.MaxElevation, minStepCount.toFloat(), maxStepCount.toFloat()).toInt()

        for( i: Int in 0..shape.elevation.toInt() step steps) {

            val amount = mapRange(
                i.toFloat(),
                0f,
                shape.elevation,
                shadowColorStart.getAlpha().toFloat(),
                shadowColorEnd.getAlpha().toFloat()).toInt()


            color.updateAlpha(amount)
            paint.color = color.toColor()

            canvas.drawRoundRect(
                shiftedLeft - i,
                shiftedTop - i ,
                shiftedRight + i,
                shiftedBottom + i,
                shape.cornerRadii,
                shape.cornerRadii,
                paint)
        }
    }


    private fun getDistance(fromX: Float, fromY: Float, toX: Float, toY: Float): Float{
        val distanceX = (toX - fromX) * (toX - fromX).toDouble()
        val distanceY = (toY - fromY) * (toY - fromY).toDouble()

        return Math.sqrt( distanceY + distanceX).toFloat()
    }
}