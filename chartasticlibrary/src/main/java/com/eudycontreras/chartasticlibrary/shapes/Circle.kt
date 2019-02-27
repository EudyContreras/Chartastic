package com.eudycontreras.chartasticlibrary.shapes

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.eudycontreras.chartasticlibrary.Shape
import com.eudycontreras.chartasticlibrary.ShapeRenderer

/**
 * Created by eudycontreras.
 */

class Circle: Shape() {

    var radius: Float = Math.min(dimension.width, dimension.height)
        set(value) {
            field = value
            dimension.width = value
            dimension.height = value
        }

    var centerX: Float = 0f
        set(value) {
            field = value
            coordinate.x = value - (radius/2)
        }

    var centerY: Float = 0f
        set(value) {
            field = value
            coordinate.y = value - (radius/2)
        }

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

        canvas?.drawOval(left, top, right, bottom, paint)

        if (showStroke) {

            strokeColor?.let {
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = strokeWidth
                paint.color = it.toColor()

                canvas?.drawOval(left, top, right, bottom, paint)
            }
        }
    }
}