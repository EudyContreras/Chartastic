package com.eudycontreras.chartasticlibrary

import android.graphics.Canvas
import android.graphics.Paint
import com.eudycontreras.chartasticlibrary.properties.Bounds
import com.eudycontreras.chartasticlibrary.properties.LightSource

/**
 * Created by eudycontreras.
 */

class ShapeRenderer(
    var paint: Paint = Paint(),
    var bounds: Bounds = Bounds(),
    var properties: RenderingProperties = RenderingProperties.Default
) {
    private var mRendering = false

    private val mShapes =  ArrayList<Shape>()

    var rendering: Boolean
        get() = mRendering
        set(value) {mRendering = value}

    fun addShapes(vararg shape: Shape) {
        mShapes.addAll(shape)
    }

    fun removeShapes(vararg shape: Shape) {
        mShapes.removeAll(shape)
    }

    fun renderShape(canvas: Canvas?) {
        for (shape in mShapes) {
            shape.render(paint, canvas, properties)
        }
    }

    class RenderingProperties{
        var showStroke: Boolean = false

        var drawShadow: Boolean = false

        var lightSource: LightSource? = null

        companion object {
            val Default = RenderingProperties()
        }
    }
}