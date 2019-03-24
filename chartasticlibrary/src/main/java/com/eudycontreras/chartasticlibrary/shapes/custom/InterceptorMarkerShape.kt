package com.eudycontreras.chartasticlibrary.shapes.custom

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.eudycontreras.chartasticlibrary.Shape
import com.eudycontreras.chartasticlibrary.ShapeRenderer
import com.eudycontreras.chartasticlibrary.properties.MutableColor

/**
 * Created by eudycontreras.
 */

class InterceptorMarkerShape: Shape() {

    var paddingMultiplier: Float = 0f
        get() = radius * field

    var outerColor: MutableColor? = null

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

    val paint = Paint()

    override fun render(path: Path, p: Paint, canvas: Canvas, renderingProperties: ShapeRenderer.RenderingProperties) {
        if (!render) {
            return
        }

        paint.reset()

        if (drawShadow) {
            if (renderingProperties.useSystemShadow) {
                paint.setShadowLayer(elevation, 0f, -elevation / 2, shadow!!.shadowColor.toColor())
            } else {
                renderingProperties.lightSource?.computeShadow(this, shadowPosition)
                shadow?.draw(left - paddingMultiplier, top - paddingMultiplier, right + paddingMultiplier, bottom + paddingMultiplier, elevation, paint, canvas)
            }
        }

        paint.style = Paint.Style.FILL
        paint.color = outerColor?.toColor()?:color.toColor()

        canvas.drawOval(left - paddingMultiplier, top - paddingMultiplier, right + paddingMultiplier, bottom + paddingMultiplier, paint)

        paint.color = color.toColor()

        canvas.drawOval(left, top, right, bottom, paint)

        if (showStroke) {

            strokeColor?.let {
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = strokeWidth
                paint.color = it.toColor()

                canvas.drawOval(left - paddingMultiplier, top - paddingMultiplier, right + paddingMultiplier, bottom + paddingMultiplier, paint)
            }
        }
    }
}