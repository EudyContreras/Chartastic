package com.eudycontreras.chartasticlibrary.views

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import com.eudycontreras.chartasticlibrary.R
import com.eudycontreras.chartasticlibrary.ShapeRenderer
import com.eudycontreras.chartasticlibrary.charts.Chart
import com.eudycontreras.chartasticlibrary.charts.ChartRenderer
import com.eudycontreras.chartasticlibrary.properties.*

/**
 * Created by eudycontreras.
 */
class RectangleView : View {

    private var properties = ShapeRenderer.RenderingProperties()

    private var chartRenderer: ChartRenderer = ChartRenderer()

    constructor(context: Context, chart: Chart) : this(context) {
         chartRenderer.addChart(chart)
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RectangleView)
        try {
            setUpAttributes(typedArray)
        } finally {
            typedArray.recycle()
        }
    }

    private var initialized: Boolean = false

    private var paint: Paint = Paint()

    private fun setUpAttributes(typedArray: TypedArray) {

    }

    fun setChart(chart: Chart) {
        chartRenderer.addChart(chart)
    }

    private fun initializeValues() {
        val width = width
        val height = height

        val paddingLeft = paddingLeft
        val paddingRight = paddingRight
        val paddingTop = paddingTop
        val paddingBottom = paddingBottom

        val usableWidth = width - (paddingLeft + paddingRight).toFloat()
        val usableHeight = height - (paddingTop + paddingBottom).toFloat()

        val bounds = Bounds(Coordinate(0f,0f), Dimension(usableWidth, usableHeight))

        properties.lightSource = LightSource(usableWidth,0f,10f, 10f)
        chartRenderer.setShapeRenderer(ShapeRenderer(paint, properties))
        chartRenderer.buildCharts(bounds)
        initialized = true
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (!initialized) {
            initializeValues()
            chartRenderer.renderCharts(canvas)
            invalidate()
        } else {
            chartRenderer.renderCharts(canvas)
        }
    }

    private fun getCalculatedOffsetY(parent: ViewGroup): Int {
        val property = Property(parent.top)

        getCalculatedOffsetY(parent.parent, property)

        return property.getValue()
    }

    private fun getCalculatedOffsetX(parent: ViewGroup): Int {
        val property = Property(parent.left)

        getCalculatedOffsetX(parent.parent, property)

        return property.getValue()
    }

    private fun getCalculatedOffsetY(parent: ViewParent, offset: Property<Int>) {
        if (parent is ViewGroup) {
            offset.setValue(offset.getValue() + parent.top)
            if (parent.parent != null) {
                getCalculatedOffsetY(parent.parent, offset)
            }
        }
    }

    private fun getCalculatedOffsetX(parent: ViewParent, offset: Property<Int>) {
        if (parent is ViewGroup) {
            offset.setValue(offset.getValue() + parent.left)
            if (parent.parent != null) {
                getCalculatedOffsetX(parent.parent, offset)
            }
        }
    }
}