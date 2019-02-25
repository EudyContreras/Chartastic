package com.eudycontreras.chartasticlibrary.shapes

import android.graphics.Canvas
import android.graphics.Paint
import com.eudycontreras.chartasticlibrary.Shape
import com.eudycontreras.chartasticlibrary.ShapeRenderer
import com.eudycontreras.chartasticlibrary.extensions.drawRoundRect

/**
 * Created by eudycontreras.
 */

class Line : Shape() {

    override fun render(paint: Paint, canvas: Canvas?, renderingProperties: ShapeRenderer.RenderingProperties) {
        if (!render) {
            return
        }

        val left = coordinate.x
        val top = coordinate.y
        val bottom = top + dimension.height
        val right = left + dimension.width

        paint.shader = null
        paint.reset()

        if (drawShadow) {
            renderingProperties.lightSource?.computeShadow(this)

            shadow?.draw(this, path, paint, canvas!!)
        }

        paint.style = Paint.Style.FILL
        paint.color = color.toColor()

        if (shader != null) {
            paint.shader = shader
        }

        canvas?.drawRoundRect(path, left, top, right, bottom, corners, paint)

        if (showStroke) {

            strokeColor?.let {
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = strokeWidth
                paint.color = it.toColor()

                canvas?.drawRoundRect(path, left, top, right, bottom, corners, paint)
            }
        }
    }
}