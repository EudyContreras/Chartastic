package com.eudycontreras.chartasticlibrary.shapes

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.eudycontreras.chartasticlibrary.Shape
import com.eudycontreras.chartasticlibrary.ShapeRenderer
import com.eudycontreras.chartasticlibrary.utilities.extensions.recycle

/**
 * Created by eudycontreras.
 */

class Line : Shape() {

    override fun render(path: Path, paint: Paint, canvas: Canvas, renderingProperties: ShapeRenderer.RenderingProperties) {
        if (!render) {
            return
        }

        paint.recycle()

        if (drawShadow) {
            if (renderingProperties.useSystemShadow) {
                paint.setShadowLayer(elevation, 0f, 0f, shadow!!.shadowColor.updateAlpha(1f).toColor())
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

        canvas.drawRoundRect(left, top, right, bottom, corners.rx, corners.ry, paint)

        if (showStroke) {

            strokeColor?.let {
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = strokeWidth
                paint.color = it.toColor()

                canvas.drawRoundRect(left, top, right, bottom, corners.rx, corners.ry, paint)
            }
        }
    }
}