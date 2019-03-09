package com.eudycontreras.chartasticlibrary.views

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.*
import android.widget.ScrollView
import androidx.core.widget.NestedScrollView
import com.eudycontreras.chartasticlibrary.ShapeRenderer
import com.eudycontreras.chartasticlibrary.charts.Chart
import com.eudycontreras.chartasticlibrary.charts.ChartRenderer
import com.eudycontreras.chartasticlibrary.charts.ChartView
import com.eudycontreras.chartasticlibrary.properties.*


/**
 * Created by eudycontreras.
 */
class RectangleView : View, ChartView {

    private var chartRenderer: ChartRenderer = ChartRenderer(this)

    private var scrollingParent: ViewParent? = null

    constructor(context: Context, chart: Chart) : this(context) {
        chartRenderer.addChart(chart)
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val typedArray =
            context.obtainStyledAttributes(attrs, com.eudycontreras.chartasticlibrary.R.styleable.RectangleView)
        try {
            setUpAttributes(typedArray)
        } finally {
            typedArray.recycle()
        }
    }

    private var initialized: Boolean = false

    private var paint: Paint = Paint()

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, paint)
    }

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

        val bounds = Bounds(Coordinate(0f, 0f), Dimension(usableWidth, usableHeight))

        chartRenderer.shapeRenderer = ShapeRenderer(paint).apply {
            properties.lightSource = LightSource(usableWidth / 2, usableHeight, 10f, 10f)
        }
        chartRenderer.buildCharts(bounds)

        initialized = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (!initialized) {
            initializeValues()
            chartRenderer.renderCharts(canvas)
            invalidate()
        } else {
            chartRenderer.renderCharts(canvas)
        }
    }

    private var sizeRatio = 0.5f

    private var fullyVisible: Boolean = false

    override var onFullyVisible: ((ChartView) -> Unit)? = null

    override fun fullyVisible(): Boolean = fullyVisible

    fun observeVisibility() {
        val scrollBounds = Rect()
        scrollingParent = findScrollParent(this.parent as ViewGroup)

        scrollingParent?.let { parent ->
            if (parent is ScrollView) {
                parent.getDrawingRect(scrollBounds)

                var top = this.y
                var bottom = top + this.height

                if (scrollBounds.top < (top + ((bottom - top) * sizeRatio)) && scrollBounds.bottom > (bottom - ((bottom - top) * sizeRatio))) {
                    if (!fullyVisible) {
                        fullyVisible = true
                        onFullyVisible?.invoke(this)
                    }
                }
                parent.viewTreeObserver.addOnScrollChangedListener {
                    val scrollY = parent.scrollY
                    val scrollX = parent.scrollX

                    parent.getDrawingRect(scrollBounds)

                    top = this.y
                    bottom = top + this.height

                    if (scrollBounds.top < (top + ((bottom - top) * sizeRatio)) && scrollBounds.bottom > (bottom - ((bottom - top) * sizeRatio))) {
                        if (!fullyVisible) {
                            fullyVisible = true
                            onFullyVisible?.invoke(this)
                        }
                    }
                }
            }
        }
    }

    private fun findScrollParent(parent: ViewGroup): ViewParent? {
        val property: Property<ViewGroup?> = Property(parent)

        fun digOutParent(parent: ViewGroup?) {
            if (parent != null) {
                if (parent is ScrollView || parent is NestedScrollView) {
                    property.setValue(parent)
                } else {
                    digOutParent(parent.parent as ViewGroup)
                }
            } else {
                property.setValue(null)
            }
        }

        return if (parent !is ScrollView && parent !is NestedScrollView) {

            digOutParent(parent.parent as ViewGroup)

            property.getValue()

        } else {
            parent
        }
    }

    private val myListener =  object : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(event: MotionEvent): Boolean {
            return true
        }

        override fun onLongPress(event: MotionEvent) {
            super.onLongPress(event)
            parent.requestDisallowInterceptTouchEvent(true)
            chartRenderer.delegateLongPressEvent(event, event.x, event.y)

            invalidate()
        }
    }

    private val detector: GestureDetector = GestureDetector(context, myListener)

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return detector.onTouchEvent(event).let { result ->
            val x = event.x
            val y = event.y

            chartRenderer.delegateTouchEvent(event, x, y)

            when (event.action) {
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_OUTSIDE -> {
                    parent.requestDisallowInterceptTouchEvent(false)
                }
            }

            invalidate()

            result
        }
    }

    override fun updateView() {
        invalidate()
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