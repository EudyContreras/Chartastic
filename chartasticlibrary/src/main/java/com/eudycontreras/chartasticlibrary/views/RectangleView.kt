package com.eudycontreras.chartasticlibrary.views

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import androidx.core.content.ContextCompat
import com.eudycontreras.chartasticlibrary.R
import com.eudycontreras.chartasticlibrary.ShapeRenderer
import com.eudycontreras.chartasticlibrary.charts.grid.ChartGrid
import com.eudycontreras.chartasticlibrary.extensions.dp
import com.eudycontreras.chartasticlibrary.properties.*
import com.eudycontreras.chartasticlibrary.shapes.Rectangle

/**
 * Created by eudycontreras.
 */
class RectangleView : View {

    private var properties: ShapeRenderer.RenderingProperties = ShapeRenderer.RenderingProperties()

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

    private var paint: Paint = Paint()

    private var renderer: ShapeRenderer? = null

    init {

    }

    private fun setUpAttributes(typedArray: TypedArray) {

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

        renderer = ShapeRenderer(paint, bounds, properties)
        addShape(
            x = (bounds.dimension.width/2) - ((bounds.dimension.width/2)),
            y = (bounds.dimension.height/2) - ((bounds.dimension.height/2)/2),
            width =  bounds.dimension.width,
            height = bounds.dimension.height/2
        )
    }

    private fun addShape(x: Float, y: Float, width: Float, height: Float) {
        val rectangle = Rectangle()
        rectangle.drawShadow = false
        rectangle.showStroke = false
        rectangle.elevation = 30.dp
        rectangle.cornerRadii = 0.dp
        rectangle.coordinate = Coordinate(x, y)
        rectangle.dimension = Dimension(width, height)
        rectangle.color = Color.toColor(ContextCompat.getColor(context, R.color.colorAccent))
        rectangle.strokeColor = Color.Blue
        rectangle.strokeWidth = 1.dp
        renderer?.addShape(rectangle)

        val chartGrid = ChartGrid(16.dp, rectangle.getBounds())
        chartGrid.borderColor = Color.White
        chartGrid.innerLinesColor = Color.White.subtractAlpha(0.4f)
        chartGrid.borderThickness = 4.dp
        chartGrid.showBorder(ChartGrid.Border.TOP, false)
        chartGrid.showBorder(ChartGrid.Border.RIGHT, false)
        chartGrid.setHorizontalPointCount(10)
        chartGrid.build()
        renderer?.addShape(chartGrid.getShapes())
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (renderer == null) {
            initializeValues()
            if (renderer != null) {
                renderer?.renderShape(canvas)
            }
            invalidate()
        } else {
            renderer?.renderShape(canvas)
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