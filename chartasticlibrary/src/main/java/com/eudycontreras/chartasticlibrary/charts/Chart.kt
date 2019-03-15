package com.eudycontreras.chartasticlibrary.charts

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.eudycontreras.chartasticlibrary.Shape
import com.eudycontreras.chartasticlibrary.ShapeRenderer
import com.eudycontreras.chartasticlibrary.properties.Bounds

/**
 * Created by eudycontreras.
 */
interface Chart {
    fun build(view: ChartView, bounds: Bounds)

    fun render(
        path: Path,
        paint: Paint,
        canvas: Canvas,
        renderingProperties: ShapeRenderer.RenderingProperties
    )

    fun getBackground(): Shape
    fun getShapes(): List<Shape>
    fun getElements(): List<ChartElement>
}