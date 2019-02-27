package com.eudycontreras.chartasticlibrary.extensions

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

    var rx = if (radiusX >= 0) radiusX else 0f
    var ry = if (radiusY >= 0) radiusY else 0f

    val width = right - left
    val height = bottom - top

    if (rx > width / 2) rx = width / 2
    if (ry > height / 2) ry = height / 2

    val widthMinusCorners = width - 2 * rx
    val heightMinusCorners = height - 2 * ry

    path.moveTo(right, top + ry)

    if (topRight)
        path.rQuadTo(0f, -ry, -rx, -ry)//top-right corner
    else {
        path.rLineTo(0f, -ry)
        path.rLineTo(-rx, 0f)
    }
    path.rLineTo(-widthMinusCorners, 0f)

    if (topLeft)
        path.rQuadTo(-rx, 0f, -rx, ry) //top-left corner
    else {
        path.rLineTo(-rx, 0f)
        path.rLineTo(0f, ry)
    }

    path.rLineTo(0f, heightMinusCorners)

    if (bottomRight)
        path.rQuadTo(0f, ry, rx, ry)//bottom-left corner
    else {
        path.rLineTo(0f, ry)
        path.rLineTo(rx, 0f)
    }

    path.rLineTo(widthMinusCorners, 0f)

    if (bottomLeft)
        path.rQuadTo(rx, 0f, rx, -ry) //bottom-right corner
    else {
        path.rLineTo(rx, 0f)
        path.rLineTo(0f, -ry)
    }

    path.rLineTo(0f, -heightMinusCorners)

    path.close()

    this.drawPath(path, paint)
}
