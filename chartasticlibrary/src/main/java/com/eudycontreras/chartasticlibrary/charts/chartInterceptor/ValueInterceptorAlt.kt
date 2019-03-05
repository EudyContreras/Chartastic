package com.eudycontreras.chartasticlibrary.charts.chartInterceptor

import android.view.MotionEvent
import com.eudycontreras.chartasticlibrary.Shape
import com.eudycontreras.chartasticlibrary.ShapeRenderer
import com.eudycontreras.chartasticlibrary.charts.interfaces.TouchableElement
import com.eudycontreras.chartasticlibrary.utilities.extensions.dp
import com.eudycontreras.chartasticlibrary.properties.*
import com.eudycontreras.chartasticlibrary.shapes.Circle
import com.eudycontreras.chartasticlibrary.shapes.Line

/**
 * Created by eudycontreras.
 */
class ValueInterceptorAlt : TouchableElement {

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
        set(value){
            field = value
            marker.radius = value
        }

    var lineThickness: Float = 0f
        set(value) {
            field = value
            lineX.dimension.height = value
            lineY.dimension.width = value
        }

    var lineColor: MutableColor
        get() = lineX.color
        set(value) {
            lineX.color = value
            lineY.color = value
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
                lineX.elevation = 8.dp
                lineX.drawShadow = true
                lineX.shadow?.let {
                    it.shadowColor = MutableColor.fromColor(Shadow.DefaultColor)
                }
                lineY.elevation = 8.dp
                lineY.drawShadow = true
                lineY.shadow?.let {
                    it.shadowColor = MutableColor.fromColor(Shadow.DefaultColor)
                }
                marker.elevation = 8.dp
                marker.drawShadow = true
                marker.shadow?.let {
                    it.shadowColor = MutableColor.fromColor(Shadow.DefaultColor)
                }
            } else {
                lineX.elevation = 0.dp
                lineX.drawShadow = false

                lineY.elevation = 0.dp
                lineY.drawShadow = false

                marker.elevation = 0.dp
                marker.drawShadow = false
            }
        }

    var shouldRender: Boolean = false
        private set(value) {
            field = value
            lineX.render = value
            lineY.render = value
            marker.render = value
        }

    var visible: Boolean = false
        set(value) {
            field = value
            shouldRender = false
        }

    var positionX: Float = 0f
        private set(value) {
            field = value
            marker.centerX = (value - shiftOffset)
            if (marker.centerX < bounds.left + (marker.radius / 2)) {
                marker.centerX = bounds.left + (marker.radius / 2)
            } else if (marker.centerX > bounds.right - (marker.radius / 2)) {
                marker.centerX = bounds.right - (marker.radius / 2)
            }
            lineY.coordinate.x = (marker.centerX - (lineY.dimension.width / 2))
        }

    var positionY: Float = 0f
        private set(value) {
            field = value
            marker.centerY = (value - shiftOffset)
            if (marker.centerY < bounds.top + (marker.radius / 2)) {
                marker.centerY = bounds.top + (marker.radius / 2)
            } else if (marker.centerY > bounds.bottom - (marker.radius / 2)) {
                marker.centerY = bounds.bottom - (marker.radius / 2)
            }
            lineX.coordinate.y = (marker.centerY - (lineY.dimension.width / 2))
        }

    var shiftOffset: Float = Float.MAX_VALUE
        get() = if (field == Float.MAX_VALUE) (marker.radius / 2) else field

    private var lineX: Line = Line()
    private var lineY: Line = Line()

    private var marker: Circle = Circle()

    fun build(bounds: Bounds) {
        this.bounds = bounds.copyProps()

        lineX.coordinate.x = bounds.coordinate.x
        lineX.coordinate.y = bounds.coordinate.y
        lineX.dimension.width = bounds.dimension.width

        lineY.coordinate.x = bounds.coordinate.x
        lineY.coordinate.y = bounds.coordinate.y
        lineY.dimension.height = bounds.dimension.height

        marker.showStroke = true
        marker.strokeWidth = 1.5f.dp
    }

    override fun onTouch(event: MotionEvent, x: Float, y: Float, shapeRenderer: ShapeRenderer) {
        if (!visible) {
            return
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (!shouldRender) {
                    positionX = x
                    positionY = y
                    shouldRender = true
                }
            }
            MotionEvent.ACTION_UP -> {
                if (shouldRender) {
                    shouldRender = false
                }
            }
            MotionEvent.ACTION_MOVE -> {
                positionX = x
                positionY = y
            }
        }
    }

    override fun onLongPressed(event: MotionEvent, x: Float, y: Float) {

    }

    fun getElements(): Collection<Shape> {
        return arrayListOf(lineX, lineY, marker)
    }
}