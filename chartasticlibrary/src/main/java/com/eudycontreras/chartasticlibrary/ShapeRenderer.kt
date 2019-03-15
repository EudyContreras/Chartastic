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

    private val shapes = ArrayList<Shape>()

    private val shapePool = ArrayList<Shape>()

    var renderCapsule: ((Path, Paint, Canvas, RenderingProperties) -> Unit)? = null

    var renderShapes: Boolean = true

    fun <T : Shape> addShape(shape: T) {
        shapes.add(shape)
    }

    fun <T : Shape> addShape(shape: Array<T>) {
        shapes.addAll(shape)
    }

    fun <T : Shape> addShape(shape: List<T>) {
        shapes.addAll(shape)
    }

    fun <T : Shape> removeShape(shape: T) {
        shapes.remove(shape)
    }

    fun <T : Shape> removeShape(vararg shape: T) {
        shapes.removeAll(shape)
    }

    fun renderShape(canvas: Canvas) {
        if(!renderShapes)
            return
        shapes.forEach { it.render(path, paint, canvas, properties) }
        renderCapsule?.invoke(path, paint, canvas, properties)
    }

    fun renderShape(canvas: Canvas, vararg shapes: Shape) {
        if(!renderShapes)
            return
        shapes.forEach { it.render(path, paint, canvas, properties) }
        if (this.shapes.isNotEmpty()) {
            renderShape(canvas)
        }
    }

    fun renderShape(canvas: Canvas, shapes: List<Shape>) {
        if(!renderShapes)
            return
        shapes.forEach { it.render(path, paint, canvas, properties) }
        if (this.shapes.isNotEmpty()) {
            renderShape(canvas)
        }
    }

    fun delegateTouchEvent(motionEvent: MotionEvent, x: Float, y: Float) {
        shapes.forEach {
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