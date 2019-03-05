package com.eudycontreras.chartasticlibrary.utilities.extensions

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.eudycontreras.chartasticlibrary.properties.CornerRadii

/**
 * Created by eudycontreras.
 */

fun Canvas.drawRoundRect(
    path: Path,

    left: Float,
    top: Float,
    right: Float,
    bottom: Float,

    corners: CornerRadii,
    paint: Paint
) {
    this.drawRoundRect(path, left, top, right, bottom, corners.rx, corners.ry, corners.topLeft, corners.topRight, corners.bottomLeft, corners.bottomRight, paint)
}

fun Canvas.drawRoundRect(
    path: Path,

    left: Float,
    top: Float,
    right: Float,
    bottom: Float,

    radiusX: Float,
    radiusY: Float,

    corners: CornerRadii,
    paint: Paint
) {
    this.drawRoundRect(path, left, top, right, bottom, radiusX, radiusY, corners.topLeft, corners.topRight, corners.bottomLeft, corners.bottomRight, paint)
}

fun Canvas.drawRoundRect(
    path: Path,

    left: Float,
    top: Float,
    right: Float,
    bottom: Float,

    radiusX: Float,
    radiusY: Float,

    topLeft: Boolean,
    topRight: Boolean,
    bottomLeft: Boolean,
    bottomRight: Boolean,

    paint: Paint
){
    path.reset()

    val tl = if (topLeft) Pair(radiusX, radiusY) else Pair(0f,0f)
    val tr = if (topRight) Pair(radiusX, radiusY) else Pair(0f,0f)
    val bl = if (bottomLeft) Pair(radiusX, radiusY) else Pair(0f,0f)
    val br = if (bottomRight) Pair(radiusX, radiusY) else Pair(0f,0f)

    val corners = arrayOf(
        tl.first,tl.second,
        tr.first,tr.second,
        bl.first,bl.second,
        br.first,br.second
    )

    path.addRoundRect(left, top, right, bottom, corners.toFloatArray(), Path.Direction.CCW)

    this.drawPath(path, paint)
}
