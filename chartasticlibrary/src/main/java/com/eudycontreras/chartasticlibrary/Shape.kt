package com.eudycontreras.chartasticlibrary

import android.graphics.Canvas
import android.graphics.Paint
import com.eudycontreras.chartasticlibrary.extensions.dp
import com.eudycontreras.chartasticlibrary.properties.*

/**
 * Created by eudycontreras.
 */

abstract class Shape(
    var coordinate: Coordinate = Coordinate(),
    var dimension: Dimension = Dimension(),
    var color: Color = Color(),
    var elevation: Float = 0f
    ) {

    var render: Boolean = true

    var showStroke: Boolean = false
    var drawShadow: Boolean = false

    var strokeWidth: Float = 0f
    var cornerRadii: Float = 0f

    var strokeColor: Color? = null

    var shadow: Shadow? = null

    fun getBounds() : Bounds {
        return Bounds(coordinate, dimension)
    }

    abstract fun update()

    abstract fun render(paint: Paint, canvas: Canvas?, renderingProperties: ShapeRenderer.RenderingProperties)

    companion object {
        val MaxElevation = 50.dp
        val MinElevation = 0.dp
    }
}