package com.eudycontreras.chartasticlibrary.properties

import com.eudycontreras.chartasticlibrary.Shape

/**
 * Created by eudycontreras.
 */


data class LightSource(
    var x: Float = 0f,
    var y: Float = 0f,
    var radius: Float = 0f,
    var intensity: Float = 0f,
    var color: Color = Color.Default
) {

    enum class Position {
        TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, CENTER
    }

    val centerPoint: Pair<Float,Float>
        get() {
            return (x + (radius/2)) to (y + (radius/2))
        }

    fun computeShadow(shapes: List<Shape>) {
        for (shape in shapes) {
            shape.shadow = Shadow()
            shape.shadow?.computeShift(shape, computePosition(shape))
        }
    }

    fun computeShadow(vararg shapes: Shape) {
        for (shape in shapes) {
            shape.shadow = Shadow()
            shape.shadow?.computeShift(shape, computePosition(shape))
        }
    }

    fun computeShadow(shape: Shape) {
        shape.shadow = Shadow()
        shape.shadow?.computeShift(shape, computePosition(shape))
    }

    private fun computePosition(shape: Shape): Position {
        val centerX = shape.coordinate.x + (shape.dimension.width / 2)
        val centerY = shape.coordinate.y + (shape.dimension.height / 2)

        return when {
                centerPoint.first > centerX && centerPoint.second > centerY -> Position.TOP_LEFT
                centerPoint.first > centerX && centerPoint.second < centerY -> Position.BOTTOM_LEFT
                centerPoint.first < centerX && centerPoint.second > centerY -> Position.TOP_RIGHT
                centerPoint.first < centerX && centerPoint.second < centerY -> Position.BOTTOM_RIGHT
                else -> Position.CENTER
            }
    }
}