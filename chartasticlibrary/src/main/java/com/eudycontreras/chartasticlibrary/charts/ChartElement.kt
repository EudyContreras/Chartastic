package com.eudycontreras.chartasticlibrary.charts

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.eudycontreras.chartasticlibrary.ShapeRenderer

/**
 * Created by eudycontreras.
 */
interface ChartElement {
    var render: Boolean

    fun render(
        path: Path,
        paint: Paint,
        canvas: Canvas,
        renderingProperties: ShapeRenderer.RenderingProperties
    )
}