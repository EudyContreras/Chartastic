package com.eudycontreras.chartasticlibrary.shapes

import android.graphics.Canvas
import android.graphics.Paint
import com.eudycontreras.chartasticlibrary.Shape
import com.eudycontreras.chartasticlibrary.ShapeRenderer
import com.eudycontreras.chartasticlibrary.extensions.dp
import com.eudycontreras.chartasticlibrary.extensions.drawRoundRect
import com.eudycontreras.chartasticlibrary.properties.Color

/**
 * Created by eudycontreras.
 */

class BoundingBox : Shape() {

    init {
        color = Color.rgba(200, 20, 20, 0.35f)
        strokeWidth = 1.dp
        strokeColor = Color.rgba(200, 20, 20, 1f)
        showStroke = true
        render = true
    }

    override fun render(paint: Paint, canvas: Canvas?, renderingProperties: ShapeRenderer.RenderingProperties) {
        if (!render) {
            return
        }

        val left = coordinate.x
        val top = coordinate.y
        val bottom = top + dimension.height
        val right = left + dimension.width

        paint.reset()

        paint.style = Paint.Style.FILL
        paint.color = color.toColor()

        canvas?.drawRoundRect(path, left, top, right, bottom, corners, paint)

        strokeColor?.let {
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = strokeWidth
            paint.color = it.toColor()

            canvas?.drawRoundRect(path, left, top, right, bottom, corners, paint)
        }
    }
}