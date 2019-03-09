package com.eudycontreras.chartasticlibrary.shapes

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent
import com.eudycontreras.chartasticlibrary.Shape
import com.eudycontreras.chartasticlibrary.ShapeRenderer
import com.eudycontreras.chartasticlibrary.charts.interfaces.TouchableShape
import com.eudycontreras.chartasticlibrary.properties.Shadow
import com.eudycontreras.chartasticlibrary.utilities.extensions.drawRoundRect

/**
 * Created by eudycontreras.
 */

class Rectangle: Shape(), TouchableShape {

    override fun onTouch(event: MotionEvent, x: Float, y: Float) {
        touchProcessor?.invoke(this, event, x, y)
    }

    override fun onLongPressed(event: MotionEvent, x: Float, y: Float) {

    }

    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        isAntiAlias = true
    }


    override fun render(path: Path, p: Paint, canvas: Canvas, renderingProperties: ShapeRenderer.RenderingProperties) {
        if (!render) {
            return
        }

        if (style != null) {
            style?.invoke(canvas, paint, path, coordinate.y, coordinate.y, dimension.width, dimension.height)
            return
        }

        if (drawShadow && shadow?.shadowType == Shadow.Type.OUTER) {
            paint.reset()
            if (renderingProperties.useSystemShadow) {
                paint.setShadowLayer(elevation, 0f, 0f, shadow!!.shadowColor.updateAlpha(255).toColor())
            } else {
                renderingProperties.lightSource?.computeShadow(this, shadowPosition)
                shadow?.draw(this, path, paint, canvas)
            }
        }

        paint.style = Paint.Style.FILL
        paint.color = color.toColor()

        if (shader != null) {
            paint.shader = shader
        }

        canvas.drawRoundRect(path, left, top, right, bottom, corners, paint)

        if (showStroke) {

            strokeColor?.let {
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = strokeWidth
                paint.color = it.toColor()

                canvas.drawRoundRect(path, left, top, right, bottom, corners, paint)
            }
        }

        if (drawShadow && shadow?.shadowType == Shadow.Type.INNER) {
            paint.reset()
            if (renderingProperties.useSystemShadow) {
                paint.setShadowLayer(elevation, 0f, 0f, shadow!!.shadowColor.updateAlpha(255).toColor())
            } else {
                renderingProperties.lightSource?.computeShadow(this, shadowPosition)
                shadow?.draw(this, path, paint, canvas)
            }
        }
    }
}