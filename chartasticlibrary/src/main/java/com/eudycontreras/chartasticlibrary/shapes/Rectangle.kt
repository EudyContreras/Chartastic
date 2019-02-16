package com.eudycontreras.chartasticlibrary.shapes

import android.graphics.Canvas
import android.graphics.Paint
import com.eudycontreras.chartasticlibrary.Shape
import com.eudycontreras.chartasticlibrary.ShapeRenderer

/**
 * Created by eudycontreras.
 */

class Rectangle: Shape() {

    override fun update() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun render(paint: Paint, canvas: Canvas?, renderingProperties: ShapeRenderer.RenderingProperties) {
        val left = coordinate.x
        val top = coordinate.y
        val bottom = top + dimension.height
        val right = left + dimension.width

        paint.reset()

        if (renderingProperties.drawShadow) {
            renderingProperties.lightSource?.computeShadow(this)

            shadow?.draw(this, paint, canvas!!)
        }

        paint.style = Paint.Style.FILL
        paint.color = color.toColor()

        canvas?.drawRoundRect(left, top, right, bottom, cornerRadii, cornerRadii, paint)

        if (renderingProperties.showStroke) {

            strokeColor?.let {
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = strokeWidth
                paint.color = it.toColor()

                canvas?.drawRoundRect(left, top, right, bottom, cornerRadii, cornerRadii, paint)
            }
        }
    }
}