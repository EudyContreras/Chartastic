package com.eudycontreras.chartasticlibrary.charts.chart_grid

import android.graphics.*
import com.eudycontreras.chartasticlibrary.ShapeRenderer
import com.eudycontreras.chartasticlibrary.charts.ChartElement
import com.eudycontreras.chartasticlibrary.properties.Bounds
import com.eudycontreras.chartasticlibrary.properties.Dimension
import com.eudycontreras.chartasticlibrary.properties.MutableColor
import com.eudycontreras.chartasticlibrary.utilities.extensions.recycle
import com.eudycontreras.chartasticlibrary.utilities.extensions.sp

/**
 * Created by eudycontreras.
 */

class ChartGridText(
    var value: Any,
    var prefix: String = "",
    var suffix: String = ""
) : ChartElement {

    override var render: Boolean = true

    enum class Alignment {
        LEFT,
        RIGHT
    }

    val text: String
        get() = "$prefix$value$suffix"

    var bounds: Rect = Rect()

    var textSize: Float = 12.sp
    var textColor: MutableColor = MutableColor.rgb(255, 255, 255)
    var typeFace: Typeface = Typeface.DEFAULT

    var alignment: Alignment =
        Alignment.LEFT

    var x: Float = 0f
    var y: Float = 0f

    lateinit var paint: Paint

    lateinit var dimension: Dimension

    fun build(bounds: Bounds = Bounds()) {
        paint.recycle()
        paint.typeface = typeFace
        paint.textSize = textSize
        paint.color = textColor.toColor()
        paint.textAlign = if (alignment == Alignment.LEFT) Paint.Align.LEFT else Paint.Align.RIGHT
        paint.getTextBounds(text, 0, text.length, this.bounds)
        dimension = Dimension(this.bounds.width().toFloat(), this.bounds.height().toFloat())
    }

    override fun render(
        path: Path,
        paint: Paint,
        canvas: Canvas,
        renderingProperties: ShapeRenderer.RenderingProperties
    ) {
        if (render) {
            canvas.drawText(text, x, y, this.paint)
        }
    }

    fun copyStyle(other: ChartGridText) {
        this.textSize = other.textSize
        this.typeFace = other.typeFace
        this.textColor = other.textColor
        this.alignment = other.alignment
    }
}