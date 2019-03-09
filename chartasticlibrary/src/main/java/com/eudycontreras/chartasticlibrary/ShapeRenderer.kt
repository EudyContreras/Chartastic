package com.eudycontreras.chartasticlibrary

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent
import com.eudycontreras.chartasticlibrary.charts.interfaces.TouchableShape
import com.eudycontreras.chartasticlibrary.properties.LightSource

/**
 * Created by eudycontreras.
 */

class ShapeRenderer(
    var paint: Paint = Paint(),
    var properties: RenderingProperties = RenderingProperties.Default
) {
    private val path: Path = Path()

    private val mShapes = ArrayList<Shape>()

    private val shapePool = ArrayList<Shape>()

    var renderCapsule: ((Path, Paint, Canvas, RenderingProperties) -> Unit)? = null

    var rendering: Boolean = false

    fun <T : Shape> addShape(shape: T) {
        mShapes.add(shape)
    }

    fun <T : Shape> addShape(shape: Array<T>) {
        mShapes.addAll(shape)
    }

    fun <T : Shape> addShape(shape: List<T>) {
        mShapes.addAll(shape)
    }

    fun <T : Shape> removeShape(shape: T) {
        mShapes.remove(shape)
    }

    fun <T : Shape> removeShape(vararg shape: T) {
        mShapes.removeAll(shape)
    }

    fun renderShape(canvas: Canvas) {
        mShapes.forEach { it.render(path, paint, canvas, properties) }
        renderCapsule?.invoke(path, paint, canvas, properties)
    }

    fun renderShape(canvas: Canvas, vararg shapes: Shape) {
        shapes.forEach { it.render(path, paint, canvas, properties) }
        if (mShapes.isNotEmpty()) {
            renderShape(canvas)
        }
    }

    fun renderShape(canvas: Canvas, shapes: List<Shape>) {
        shapes.forEach { it.render(path, paint, canvas, properties) }
        if (mShapes.isNotEmpty()) {
            renderShape(canvas)
        }
    }

    fun delegateTouchEvent(motionEvent: MotionEvent, x: Float, y: Float) {
        mShapes.forEach {
            if (it is TouchableShape && it.touchProcessor != null) {
                it.onTouch(motionEvent, x, y)
            }
        }
    }

    class RenderingProperties {

        var useSystemShadow: Boolean = false

        var lightSource: LightSource? = null

        companion object {
            val Default = RenderingProperties()
        }
    }
}