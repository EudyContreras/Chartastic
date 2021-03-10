package com.eudycontreras.chartasticlibrary.views

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
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

    private var sizeRatio = 0.5f

    private var fullyVisible: Boolean = false
    private var initialized: Boolean = false

    private var chartRenderer: ChartRenderer = ChartRenderer(this)

    private var scrollingParent: ViewParent? = null

    override var onFullyVisible: ((ChartView) -> Unit)? = null

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
    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
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

        chartRenderer.shapeRenderer = ShapeRenderer().apply {
            properties.lightSource = LightSource(usableWidth / 2, usableHeight)
        }

        chartRenderer.buildCharts(bounds)

        initialized = true

        if(scrollingParent == null){
            fullyVisible = true
            onFullyVisible?.invoke(this)
            chartRenderer.notifyFullyVisible()
            return
        }
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
                        chartRenderer.notifyFullyVisible()
                    }
                }

                parent.viewTreeObserver.addOnScrollChangedListener {
                    parent.getDrawingRect(scrollBounds)

                    top = this.y
                    bottom = top + this.height

                    if (scrollBounds.top < (top + ((bottom - top) * sizeRatio)) && scrollBounds.bottom > (bottom - ((bottom - top) * sizeRatio))) {
                        if (!fullyVisible) {
                            fullyVisible = true
                            onFullyVisible?.invoke(this)
                            chartRenderer.notifyFullyVisible()
                        }
                    }
                }
            } else if (parent is NestedScrollView){
                parent.getDrawingRect(scrollBounds)

                var top = this.y
                var bottom = top + this.height

                if (scrollBounds.top < (top + ((bottom - top) * sizeRatio)) && scrollBounds.bottom > (bottom - ((bottom - top) * sizeRatio))) {
                    if (!fullyVisible) {
                        fullyVisible = true
                        onFullyVisible?.invoke(this)
                        chartRenderer.notifyFullyVisible()
                    }
                }
                parent.setOnScrollChangeListener { v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
                    parent.getDrawingRect(scrollBounds)

                    top = this.y
                    bottom = top + this.height

                    if (scrollBounds.top < (top + ((bottom - top) * sizeRatio)) && scrollBounds.bottom > (bottom - ((bottom - top) * sizeRatio))) {
                        if (!fullyVisible) {
                            fullyVisible = true
                            onFullyVisible?.invoke(this)
                            chartRenderer.notifyFullyVisible()
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
                    if (parent.parent != null) {
                        digOutParent(parent.parent as ViewGroup)
                    } else {
                        property.setValue(null)
                    }
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

            invalidate()

            val x = event.x
            val y = event.y

            chartRenderer.delegateTouchEvent(event, x, y)

            when (event.action) {
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_OUTSIDE -> {
                    parent.requestDisallowInterceptTouchEvent(false)
                }
            }

            result
        }
    }

    override fun updateView() {
        invalidate()
    }
}