package com.eudycontreras.chartasticlibrary.properties

import com.eudycontreras.chartasticlibrary.Shape

/**
 * Created by eudycontreras.
 */

data class Bounds(
    var coordinate: Coordinate = Coordinate(),
    var dimension: Dimension = Dimension()
) {
    fun intercepts(shape: Shape) : Boolean {
        val startX = shape.coordinate.x
        val startY = shape.coordinate.y

        val endX = startX + shape.dimension.width
        val endY = startY + shape.dimension.height

        val width = shape.dimension.width
        val height = shape.dimension.height

        return intercepts(startX, startY, endX, endY, width, height)
    }

    fun intercepts(
        sourceStartX: Float,
        sourceStartY: Float,
        sourceEndX: Float,
        sourceEndY: Float,
        sourceWidth: Float,
        sourceHeight: Float
    ): Boolean {
        TODO("Implement the intercept")
    }

    fun isInside(): Boolean{
        TODO("Implement the isInside")
    }
}

