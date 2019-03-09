package com.eudycontreras.chartasticlibrary.shapes.polygons

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Point
import com.eudycontreras.chartasticlibrary.Shape
import com.eudycontreras.chartasticlibrary.ShapeRenderer

/**
 * Created by eudycontreras.
 */
class ToolTip: Shape(){

    var points: ArrayList<Point> = ArrayList()

    override fun render(
        path: Path,
        paint: Paint,
        canvas: Canvas,
        renderingProperties: ShapeRenderer.RenderingProperties
    ) {

    }

}