package com.eudycontreras.chartasticlibrary.shapes.custom

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent
import com.eudycontreras.chartasticlibrary.Shape
import com.eudycontreras.chartasticlibrary.ShapeRenderer
import com.eudycontreras.chartasticlibrary.charts.interfaces.TouchableShape
import com.eudycontreras.chartasticlibrary.properties.Bounds
import com.eudycontreras.chartasticlibrary.properties.PathPlot
import com.eudycontreras.chartasticlibrary.properties.PathPoint
import com.eudycontreras.chartasticlibrary.properties.Shadow
import com.eudycontreras.chartasticlibrary.utilities.extensions.dp
import com.eudycontreras.chartasticlibrary.utilities.global.mapRange

/**
 * Created by eudycontreras.
 */

class InterceptorTooltip: Shape(), TouchableShape {

    override fun onTouch(event: MotionEvent, x: Float, y: Float) {
        touchProcessor?.invoke(this, event, x, y)
    }

    override fun onLongPressed(event: MotionEvent, x: Float, y: Float) {

    }

    var pointerOffset = 0.5f

    var pointerWidth = 10.dp

    var pointerLength = 10.dp

    var cornerRadius = 8.dp
        set(value) {
            field = value
            corners.rx = field / 2
            corners.ry = field / 2
        }

    var parentBounds: Bounds = Bounds()

    private val pathPlot: PathPlot = PathPlot(Path())

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        isAntiAlias = true
    }

    override fun render(path: Path, paint: Paint, canvas: Canvas, renderingProperties: ShapeRenderer.RenderingProperties) {
        if (!render) {
            return
        }

        val offsetLeft = 1f * mapRange(pointerOffset, 0f, 0.5f, 0f, 0.5f, 0f, 1f)
        val offsetRight = 1f * mapRange(pointerOffset, 0.5f, 1f, 0.5f, 0f, 0f, 1f)

        if (!pathPlot.pathCreated) {

            pathPlot.width = dimension.width - (cornerRadius * 2)
            pathPlot.height = dimension.height - (cornerRadius * 2)

            pathPlot.contentBounds.dimension.width = pathPlot.width + cornerRadius
            pathPlot.contentBounds.dimension.height = pathPlot.height + cornerRadius

            val shift = (pathPlot.width - pointerWidth)

            pathPlot.startX = left
            pathPlot.startY = top + cornerRadius

            pathPlot.points.add(PathPoint(PathPlot.Type.LINE, true, -(pointerWidth * offsetLeft), -pointerLength))
            pathPlot.points.add(PathPoint(PathPlot.Type.LINE, true, -((pointerOffset * shift)), 0f))
            pathPlot.points.add(PathPoint(PathPlot.Type.QUAD, true, -cornerRadius, 0f, -cornerRadius, -cornerRadius))
            pathPlot.points.add(PathPoint(PathPlot.Type.LINE, true, 0f, -pathPlot.height))
            pathPlot.points.add(PathPoint(PathPlot.Type.QUAD, true, 0f, -cornerRadius, cornerRadius, -cornerRadius))
            pathPlot.points.add(PathPoint(PathPlot.Type.LINE, true, pathPlot.width, 0f))
            pathPlot.points.add(PathPoint(PathPlot.Type.QUAD, true, cornerRadius, 0f, cornerRadius, cornerRadius))
            pathPlot.points.add(PathPoint(PathPlot.Type.LINE, true, 0f, pathPlot.height))
            pathPlot.points.add(PathPoint(PathPlot.Type.QUAD, true, 0f, cornerRadius, -cornerRadius, cornerRadius))
            pathPlot.points.add(PathPoint(PathPlot.Type.LINE, true, -(shift - (pointerOffset * shift)), 0f))
            pathPlot.points.add(PathPoint(PathPlot.Type.LINE, true, -(pointerWidth * offsetRight), pointerLength))

            pathPlot.build()
        }

        pathPlot.startX = left
        pathPlot.startY= top + cornerRadius

        val shift = (pathPlot.width - pointerWidth)

        pathPlot.points[0].startX = -(pointerWidth * offsetLeft)
        pathPlot.points[1].startX =  -((pointerOffset * shift))
        pathPlot.points[9].startX = -(shift-(pointerOffset * shift))
        pathPlot.points[10].startX = -(pointerWidth * offsetRight)

        pathPlot.contentBounds.coordinate.x = left - ((pathPlot.width/2) + (cornerRadius/2))
        pathPlot.contentBounds.coordinate.y = top - (pointerLength + (pathPlot.height + (cornerRadius / 2)))

        pathPlot.build()

        /*
        pathPlot.translate(left, top + cornerRadius)
        */
        if (drawShadow && shadow?.shadowType == Shadow.Type.OUTER) {
            this.paint.setShadowLayer(elevation, 0f, 0f, shadow!!.shadowColor.updateAlpha(255).toColor())
        }

        this.paint.style = Paint.Style.FILL
        this.paint.color = color.toColor()

        canvas.drawPath(pathPlot.path,  this.paint)

        if (showStroke) {

            strokeColor?.let {
                this.paint.style = Paint.Style.STROKE
                this.paint.strokeWidth = strokeWidth
                this.paint.color = it.toColor()

                canvas.drawPath(pathPlot.path,  this.paint)
            }
        }

    }
}