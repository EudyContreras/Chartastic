package com.eudycontreras.chartasticlibrary.shapes

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.eudycontreras.chartasticlibrary.Shape
import com.eudycontreras.chartasticlibrary.ShapeRenderer
import com.eudycontreras.chartasticlibrary.properties.MutableColor
import com.eudycontreras.chartasticlibrary.utilities.extensions.dp
import com.eudycontreras.chartasticlibrary.utilities.extensions.recycle

/**
 * Created by eudycontreras.
 */

class BoundingBox : Shape() {

    init {
        color = MutableColor.rgba(200, 20, 20, 100)
        strokeWidth = 1.dp
        strokeColor = MutableColor.rgba(200, 20, 20, 1f)
        showStroke = true
        render = true
    }

    var renderFill: Boolean = false

    override fun render(
        path: Path,
        paint: Paint,
        canvas: Canvas,
        renderingProperties: ShapeRenderer.RenderingProperties
    ) {
        if (!render) {
            return
        }

        paint.recycle()

        if(renderFill) {
            paint.style = Paint.Style.FILL
            paint.color = color.toColor()

            canvas.drawRect(left, top, right, bottom, paint)
        }

        strokeColor?.let {
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = strokeWidth
            paint.color = it.toColor()

            canvas.drawRect(left, top, right, bottom, paint)
        }
    }
}