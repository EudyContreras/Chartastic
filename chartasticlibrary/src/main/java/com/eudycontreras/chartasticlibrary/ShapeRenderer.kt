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

    fun <T : Shape> addShape(shape: T) {
        mShapes.add(shape)
    }

    fun <T : Shape> addShape(shape: Array<T>) {
        mShapes.addAll(shape)
    }

    fun <T : Shape> removeShape(shape: T) {
        mShapes.remove(shape)
    }

    fun <T : Shape> removeShape(vararg shape: T) {
        mShapes.removeAll(shape)
    }

    fun renderShape(canvas: Canvas?) {
        for (shape in mShapes) {
            shape.render(paint, canvas, properties)
        }
    }

    class RenderingProperties{

        var lightSource: LightSource? = null

        companion object {
            val Default = RenderingProperties()
        }
    }
}