package com.eudycontreras.chartasticlibrary.charts.chart_interceptor

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent
import com.eudycontreras.chartasticlibrary.ShapeRenderer
import com.eudycontreras.chartasticlibrary.charts.ChartElement
import com.eudycontreras.chartasticlibrary.charts.interfaces.TouchableElement
import com.eudycontreras.chartasticlibrary.properties.*
import com.eudycontreras.chartasticlibrary.shapes.Circle
import com.eudycontreras.chartasticlibrary.shapes.Line
import com.eudycontreras.chartasticlibrary.utilities.extensions.dp

/**
 * Created by eudycontreras.
 */
class ValueInterceptor : ChartElement, TouchableElement {

    override var render: Boolean = true

    enum class Style {
        STYLE_ONE,
        STYLE_TWO,
        STYLE_THREE,
        STYLE_FOUR,
        STYLE_FIVE
    }

    var style: Style = Style.STYLE_ONE

    var bounds: Bounds = Bounds()
        private set(value) {
            field = value
        }

    var coordinates: Coordinate
        get() = bounds.coordinate
        private set(value) {
            bounds.coordinate = value
        }

    var dimensions: Dimension
        get() = bounds.dimension
        private set(value) {
            bounds.dimension = value
        }

    var markerRadius: Float = 30.dp
        set(value) {
            field = value
            marker.radius = value
        }

    var lineThickness: Float = 0f
        set(value) {
            field = value
            lineLeft.dimension.height = value
            lineRight.dimension.height = value
            lineTop.dimension.width = value
            lineBottom.dimension.width = value
        }

    var lineColor: MutableColor
        get() = lineLeft.color
        set(value) {
            lineLeft.color = value
            lineRight.color = value
            lineTop.color = value
            lineBottom.color = value
        }

    var markerColor: MutableColor
        get() = marker.color
        set(value) {
            marker.color = MutableColor(value).updateAlpha(0.4f)
            marker.strokeColor = MutableColor(value).updateAlpha(0.8f)
        }

    var markerFillColor: MutableColor
        get() = marker.color
        set(value) {
            marker.color = MutableColor(value).updateAlpha(0.4f)
        }

    var markerStrokeColor: MutableColor
        get() = marker.color
        set(value) {
            marker.strokeColor = MutableColor(value).updateAlpha(0.8f)
        }

    var showShadows: Boolean = false
        set(value) {
            field = value
            if (value) {
                lineLeft.elevation = 4.dp
                lineLeft.drawShadow = true
                lineLeft.shadow?.let {
                    it.shadowColor = MutableColor.fromColor(Shadow.DefaultColor)
                }
                lineRight.elevation = 4.dp
                lineRight.drawShadow = true
                lineRight.shadow?.let {
                    it.shadowColor = MutableColor.fromColor(Shadow.DefaultColor)
                }
                lineTop.elevation = 4.dp
                lineTop.drawShadow = true
                lineTop.shadow?.let {
                    it.shadowColor = MutableColor.fromColor(Shadow.DefaultColor)
                }
                lineBottom.elevation = 4.dp
                lineBottom.drawShadow = true
                lineBottom.shadow?.let {
                    it.shadowColor = MutableColor.fromColor(Shadow.DefaultColor)
                }
                marker.elevation = 4.dp
                marker.drawShadow = true
                marker.shadow?.let {
                    it.shadowColor = MutableColor.fromColor(Shadow.DefaultColor)
                }
            } else {
                lineLeft.elevation = 0.dp
                lineLeft.drawShadow = false

                lineRight.elevation = 0.dp
                lineRight.drawShadow = false

                lineTop.elevation = 0.dp
                lineTop.drawShadow = false

                lineBottom.elevation = 0.dp
                lineBottom.drawShadow = false

                marker.elevation = 0.dp
                marker.drawShadow = false
            }
        }

    var allowIntercept: Boolean = true
        get() = field && visible
        set(value) {
            shouldRender = value
            visible = value
        }

    var shouldRender: Boolean = false
        private set(value) {
            field = value
            if (showHorizontalLine) {
                lineLeft.render = value
                lineRight.render = value
            } else {
                lineLeft.render = false
                lineRight.render = false
            }
            if (showVerticalLine) {
                lineTop.render = value
                lineBottom.render = value
            } else {
                lineTop.render = false
                lineBottom.render = false
            }
            marker.render = value
        }

    var visible: Boolean = false
        set(value) {
            field = value
            shouldRender = false
        }

    var showVerticalLine: Boolean = true
        set(value) {
            field = value
            lineTop.render = value
            lineBottom.render = value
        }

    var showHorizontalLine: Boolean = true
        set(value) {
            field = value
            lineLeft.render = value
            lineRight.render = value
        }

    var positionX: Float = 0f
        private set(value) {
            field = value
            marker.centerX = (value - shiftOffsetX)
            if (marker.centerX < (bounds.left + marker.radius / 2)) {
                marker.centerX = (bounds.left + marker.radius / 2)
            } else if (marker.centerX > (bounds.right - marker.radius / 2)) {
                marker.centerX = (bounds.right - marker.radius / 2)
            }

            lineTop.coordinate.x = (marker.centerX - (lineTop.dimension.width / 2))
            lineBottom.coordinate.x = lineTop.coordinate.x

            lineLeft.dimension.width = (marker.centerX - (marker.radius / 2)) - bounds.left

            lineRight.coordinate.x = (marker.centerX + (marker.radius / 2))
            lineRight.dimension.width = bounds.right - lineRight.coordinate.x
        }

    var positionY: Float = 0f
        private set(value) {
            field = value
            marker.centerY = (value - shiftOffsetY)
            if (marker.centerY < (bounds.top + marker.radius / 2)) {
                marker.centerY = (bounds.top + marker.radius / 2)
            } else if (marker.centerY > (bounds.bottom - marker.radius / 2)) {
                marker.centerY = (bounds.bottom - marker.radius / 2)
            }

            lineTop.dimension.height = (marker.centerY - (marker.radius / 2)) - bounds.top

            lineBottom.coordinate.y = (marker.centerY + (marker.radius / 2))
            lineBottom.dimension.height = bounds.bottom - lineBottom.coordinate.y

            lineLeft.coordinate.y = (marker.centerY)
            lineRight.coordinate.y = lineLeft.coordinate.y

        }

    var shiftOffsetX: Float = Float.MAX_VALUE
        get() = if (field == Float.MAX_VALUE) (marker.radius) else field

    var shiftOffsetY: Float = Float.MAX_VALUE
        get() = if (field == Float.MAX_VALUE) (marker.radius) else field

    private var lineLeft: Line = Line()
    private var lineRight: Line = Line()
    private var lineTop: Line = Line()
    private var lineBottom: Line = Line()

    private var marker: Circle = Circle()

    fun build(bounds: Bounds = Bounds()) {
        this.bounds.update(bounds)

        lineTop.coordinate.y = bounds.coordinate.y

        lineLeft.coordinate.x = bounds.coordinate.x

        marker.showStroke = true
        marker.strokeWidth = 1.5f.dp

        lineRight.render = false
        lineLeft.render = false
        lineBottom.render = false
        lineTop.render = false
    }

    override fun render(
        path: Path,
        paint: Paint,
        canvas: Canvas,
        renderingProperties: ShapeRenderer.RenderingProperties
    ) {
        lineLeft.render(path, paint, canvas, renderingProperties)
        lineRight.render(path, paint, canvas, renderingProperties)
        lineTop.render(path, paint, canvas, renderingProperties)
        lineBottom.render(path, paint, canvas, renderingProperties)
        marker.render(path, paint, canvas, renderingProperties)
    }

    override fun onTouch(event: MotionEvent, x: Float, y: Float, shapeRenderer: ShapeRenderer) {
        if(!allowIntercept)
            return

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                shapeRenderer.delegateTouchEvent(event, x, y)
            }
            MotionEvent.ACTION_UP -> {
                if (shouldRender && visible) {
                    shouldRender = false
                }
                shapeRenderer.delegateTouchEvent(event, x, y)
            }
            MotionEvent.ACTION_MOVE -> {
                if (visible) {
                    positionX = x
                    positionY = y
                    shapeRenderer.delegateTouchEvent(event, marker.centerX, marker.centerY)
                } else {
                    shapeRenderer.delegateTouchEvent(event, x, y)
                }
            }
        }
    }

    override fun onLongPressed(event: MotionEvent, x: Float, y: Float) {
        if(!allowIntercept)
            return
        if (!shouldRender && visible) {
            shouldRender = true
            positionX = x
            positionY = y
        }
    }
    internal class ValueInterceptorTooltip {
        var bounds: Bounds = Bounds()
    }
}