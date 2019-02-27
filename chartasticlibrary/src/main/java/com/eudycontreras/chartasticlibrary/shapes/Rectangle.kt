package com.eudycontreras.chartasticlibrary.shapes

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.eudycontreras.chartasticlibrary.Shape
import com.eudycontreras.chartasticlibrary.ShapeRenderer
import com.eudycontreras.chartasticlibrary.extensions.drawRoundRect

/**
 * Created by eudycontreras.
 */

class Rectangle: Shape() {

    override fun render(path: Path, paint: Paint, canvas: Canvas?, renderingProperties: ShapeRenderer.RenderingProperties) {
        if (!render) {
            return
        }

        paint.reset()

        if (drawShadow) {
            if (renderingProperties.useSystemShadow) {
                paint.setShadowLayer(elevation, 0f, -elevation / 2, shadow!!.shadowColorStart.toColor())
            } else {
                renderingProperties.lightSource?.computeShadow(this, shadowPosition)
                shadow?.draw(this, path, paint, canvas!!)
            }
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