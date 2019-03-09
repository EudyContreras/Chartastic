package com.eudycontreras.chartasticlibrary.charts.chart_text

import android.graphics.*
import com.eudycontreras.chartasticlibrary.ShapeRenderer
import com.eudycontreras.chartasticlibrary.charts.ChartElement
import com.eudycontreras.chartasticlibrary.properties.Bounds
import com.eudycontreras.chartasticlibrary.properties.Dimension
import com.eudycontreras.chartasticlibrary.properties.MutableColor
import com.eudycontreras.chartasticlibrary.utilities.extensions.sp

/**
 * Created by eudycontreras.
 */

class ChartText(
    var text: String,
    var paint: Paint
) : ChartElement {

    private var mRender: Boolean = true

    override var render: Boolean
        get() = mRender
        set(value) {
            mRender = value
        }

    enum class Alignment {
        LEFT,
        RIGHT
    }

    var bounds: Rect = Rect()

    var textSize: Float = 12.sp
    var textColor: MutableColor = MutableColor.rgb(255, 255, 255)
    var typeFace: Typeface = Typeface.DEFAULT

    var alignment: Alignment = Alignment.LEFT

    var x: Float = 0f
    var y: Float = 0f

    lateinit var dimension: Dimension

    override fun build(bounds: Bounds) {
        paint.reset()
        paint.typeface = typeFace
        paint.textSize = textSize
        paint.color = textColor.toColor()
        paint.textAlign = if (alignment == Alignment.LEFT) Paint.Align.LEFT else Paint.Align.RIGHT
        paint.getTextBounds(text, 0, text.length, this.bounds)
        dimension = Dimension(this.bounds.width().toFloat(), this.bounds.height().toFloat())
    }

    override fun render(
        path: Path,
        p: Paint,
        canvas: Canvas,
        renderingProperties: ShapeRenderer.RenderingProperties
    ) {
        if (mRender) {
            canvas.drawText(text, x, y, paint)
        }
    }

    fun copyStyle(other: ChartText) {
        this.textSize = other.textSize
        this.typeFace = other.typeFace
        this.textColor = other.textColor
        this.alignment = other.alignment
    }
}