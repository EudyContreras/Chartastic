package com.eudycontreras.chartasticlibrary.charts.chart_model.bar_chart

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent
import com.eudycontreras.chartasticlibrary.Shape
import com.eudycontreras.chartasticlibrary.ShapeRenderer
import com.eudycontreras.chartasticlibrary.charts.ChartAnimation
import com.eudycontreras.chartasticlibrary.charts.ChartElement
import com.eudycontreras.chartasticlibrary.properties.*
import com.eudycontreras.chartasticlibrary.shapes.Rectangle
import com.eudycontreras.chartasticlibrary.utilities.extensions.dp
import com.eudycontreras.chartasticlibrary.utilities.global.ShapeStyle


/**
 * Created by eudycontreras.
 */
data class BarChartItem<Data>(
    var label: String,
    var value: Any,
    var data: Data,
    var action: ((Data) -> Unit)? = null
) : ChartAnimation.Animateable, ChartElement {

    companion object {
        const val DEFAULT_ROUND_RADIUS = -1f
    }

    override var render: Boolean = true

    private val cornerRadiiMultiplier = 0.75f

    private var savedState: Pair<Float, Float> = Pair(0f, 0f)

    var negativeOrientation: Boolean = false
        set(value) {
            field = value
            applyLazyFlip()
        }

    val shape: Rectangle = Rectangle()

    var activeColor: MutableColor = MutableColor()

    var hoverColor: MutableColor = MutableColor()

    var color: MutableColor = MutableColor()
        set(value) {
            field = value
            shape.color = value
        }

    var gradient: Gradient? = null
        set(value) {
            field = value
            field?.let {
                color = if (it.colors.isNotEmpty()) it.colors[0] else color
                shape.shader = Shape.getShader(it, x, y, thickness, length)
            }
        }

    var x: Float = 0f
        internal set(value) {
            field = value
            shape.coordinate.x = field
        }

    var y: Float = 0f
        internal set(value) {
            field = value
            shape.coordinate.y = field
        }

    var length: Float = 0f
        internal set(value) {
            field = value
            shape.dimension.height = field
        }

    var thickness: Float = 0f
        internal set(value) {
            field = value
            shape.dimension.width = field
            if (cornerRadius == DEFAULT_ROUND_RADIUS) {
                shape.corners.apply {
                    this.rx = field * cornerRadiiMultiplier
                    this.ry = field * cornerRadiiMultiplier
                }
            }
        }

    var barStyle: ShapeStyle? = null

    var highlightable: Boolean = false

    var strokeColor: MutableColor?
        get() = shape.strokeColor
        set(value) {
            shape.strokeColor = value
        }

    var strokeWidth: Float
        get() = shape.strokeWidth
        set(value) {
            shape.showStroke = value > 0
            shape.strokeWidth = value
        }
    var elevation: Float
        get() = shape.elevation
        set(value) {
            shape.drawShadow = value > 0
            shape.elevation = value
        }

    var roundedTop: Boolean
        get() = shape.corners.topLeft && shape.corners.topRight
        set(value) {
            shape.corners.topLeft = value
            shape.corners.topRight = value
        }

    var roundedBottom: Boolean
        get() = shape.corners.bottomLeft && shape.corners.bottomRight
        set(value) {
            shape.corners.bottomLeft = value
            shape.corners.bottomRight = value
        }

    var elevationShadowColor: Color? = Shadow.DefaultColor
        get() = field ?: Shadow.DefaultColor
        set(value) {
            field = value
            val last = shape.drawShadow
            shape.drawShadow = true
            shape.shadow?.let { shadow ->
                value?.let {
                    shadow.shadowColor = MutableColor.fromColor(it)
                }
            }
            shape.drawShadow = last
        }

    var elevationShadowPosition: LightSource.Position = LightSource.Position.TOP_LEFT_RIGHT
        set(value) {
            field = value
            shape.shadowPosition = value
        }

    var cornerRadius: Float = 0f
        set(value) {
            field = value
            if (value >= 0) {
                shape.corners.rx = value
                shape.corners.ry = value
            } else if (value == DEFAULT_ROUND_RADIUS) {
                shape.corners.rx = thickness * cornerRadiiMultiplier
                shape.corners.ry = thickness * cornerRadiiMultiplier
            }
        }

    var backgroundOptions: BackgroundOptions = BackgroundOptions()
        private set(value) {
            field = value
        }

    private var touching = false

    private var touchProcessor: (Shape, MotionEvent, Float, Float) -> Unit = { shape, motionEvent, x, y ->
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                touching = true
                if (shape.bounds.isInside(x, y)) {
                    if (shape.color != activeColor) {
                        shape.color = activeColor
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                touching = false
                action?.invoke(data)
                if (shape.color != color) {
                    shape.color = color
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (shape.bounds.isInside(x, y)) {
                    if (shape.color != hoverColor) {
                        shape.color = hoverColor
                    }
                } else {
                    if (shape.color != color) {
                        shape.color = color
                    }
                }
            }
            else -> {
                touching = false
                action?.invoke(data)
                if (shape.color != color) {
                    shape.color = color
                }
            }
        }
    }

    fun build(bounds: Bounds = Bounds()) {
        shape.color = color
        shape.coordinate.x = x
        shape.coordinate.y = y
        shape.dimension.width = thickness
        shape.dimension.height = length
        shape.shader = gradient?.let {
            Shape.getShader(it, x, y, thickness, length)
        }
        shape.shadow?.let {
            it.minStepCount = 0f
            it.maxStepCount = 18f
        }
        if (backgroundOptions.showBackground) {
            backgroundOptions.buildBackground()
        }

        shape.touchProcessor = touchProcessor
        shape.render = false
        shape.style = barStyle
    }

    override fun render(
        path: Path,
        paint: Paint,
        canvas: Canvas,
        renderingProperties: ShapeRenderer.RenderingProperties
    ) {
        if (!render) {
            return
        }
        if (backgroundOptions.showBackground) {
            backgroundOptions.background.render(path, paint, canvas, renderingProperties)
        }
        shape.render(path, paint, canvas, renderingProperties)
    }

    override fun onPreAnimation() {
        savedState = Pair(y, length)
        y += length
        length = 0f
        shape.render = true
    }

    override fun onPostAnimation() {}

    override fun onAnimate(delta: Float) {
        y = (savedState.first + savedState.second) - (savedState.second * delta)
        length = savedState.second * delta
    }

    fun applyHighlight() {
        thickness = 30.dp
    }

    private fun applyLazyFlip() {
        if(negativeOrientation && (roundedBottom && !roundedTop)) {
            shape.corners.topLeft = true
            shape.corners.topRight = true
            shape.corners.bottomLeft = false
            shape.corners.bottomRight = false
        }

        if(negativeOrientation && (roundedTop && !roundedBottom)) {
            shape.corners.topLeft = false
            shape.corners.topRight = false
            shape.corners.bottomLeft = true
            shape.corners.bottomRight = true
        }

        if(backgroundOptions.showBackground) {
            backgroundOptions.background.corners.copyProps(shape.corners)
        }
    }

    inner class BackgroundOptions {

        internal val background: Rectangle by lazy {
            Rectangle()
        }

        fun buildBackground() {
            background.render = showBackground
            background.coordinate.x = x
            background.coordinate.y = y
            background.dimension.height = height
            background.dimension.width = thickness
            background.corners.copyProps(shape.corners)
            background.coordinate.x -= padding
            background.coordinate.y -= padding
            background.dimension.width += (padding * 2)
            background.dimension.height += (padding * 2)
        }

        var padding: Float = 0f
            set(value) {
                field = value
                background.coordinate.x -= value
                background.coordinate.y -= value
                background.dimension.width += value
                background.dimension.height += value
            }

        var color: MutableColor = MutableColor()
            set(value) {
                field = value
                background.color.setColor(field)
            }

        var strokeWidth: Float = 1.dp
            set(value) {
                field = value
                background.strokeWidth = value
            }

        var strokeColor: MutableColor = MutableColor()
            set(value) {
                field = value
                background.showStroke = true
                background.strokeColor?.setColor(field)
            }

        var height: Float = length
            internal set(value) {
                field = value
                background.dimension.height = value
                background.dimension.height += (padding * 2)
            }

        var y: Float = 0f
            internal set(value) {
                field = value
                background.coordinate.y = value
                background.coordinate.y -= (padding * 2)
            }

        var showBackground: Boolean = false
    }
}