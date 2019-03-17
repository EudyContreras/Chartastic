package com.eudycontreras.chartasticlibrary.charts

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.eudycontreras.chartasticlibrary.Shape
import com.eudycontreras.chartasticlibrary.ShapeRenderer
import com.eudycontreras.chartasticlibrary.charts.interfaces.ChartBoundsOwner
import com.eudycontreras.chartasticlibrary.properties.Bounds
import com.eudycontreras.chartasticlibrary.shapes.BoundingBox

/**
 * Created by eudycontreras.
 */

class ChartLayoutManager(parentBounds: Bounds, val layoutManager: ChartLayoutManager? = null): ChartBoundsOwner, ChartElement {

    constructor(layoutManager: ChartLayoutManager? = null):this(Bounds(), layoutManager)

    enum class BoundsAnchor {
        LEFT,
        RIGHT,
        TOP,
        BOTTOM,
        CENTER
    }

    enum class Type {
        INNER,
        OUTER
    }

    var type: Type = Type.OUTER

    private val boundsBox: Shape by lazy {
        BoundingBox().apply {
            this.bounds.update(this@ChartLayoutManager.bounds.drawableArea)
        }
    }

    override var computeBounds: Boolean = true
        set(value) {
            field = value
            if (!value) {
                layoutManager?.removeBoundsOwner(this)
            } else {
                layoutManager?.addBoundsOwner(this)
            }
        }

    override var render: Boolean = true

    override var drawBounds: Boolean = false

    override val anchor: BoundsAnchor = BoundsAnchor.CENTER

    override val bounds: Bounds = Bounds(parentBounds,this)

    private val boundsOwners = LinkedHashMap<BoundsAnchor,ChartBoundsOwner>()

    init {
        layoutManager?.addBoundsOwner(this)
    }

    override fun render(
        path: Path,
        paint: Paint,
        canvas: Canvas,
        renderingProperties: ShapeRenderer.RenderingProperties
    ) {
        if (!render || !computeBounds) {
            return
        }
        if (drawBounds) {
            boundsBox.render(path, paint, canvas, renderingProperties)
        }
    }

    override fun notifyBoundsChange(bounds: Bounds) {
        if(bounds != this.bounds) {
            this.bounds.update(bounds, false)
        }
        layoutManager?.notifyBoundsChange(this)
    }

    override fun propagateNewBounds(bounds: Bounds) {
        this.bounds.update(bounds, false)
        for (owner in boundsOwners) {
            owner.value.notifyBoundsChange(owner.value.bounds)
        }
    }

    fun addBoundsOwner(boundsOwner: ChartBoundsOwner) {
        boundsOwners[boundsOwner.anchor] = boundsOwner
        computeBoundsConstraints(boundsOwner.anchor)
    }

    fun removeBoundsOwner(boundsOwner: ChartBoundsOwner) {
        boundsOwners.remove(boundsOwner.anchor)
        computeRemovedBoundsConstraints(boundsOwner.anchor)
    }

    fun replaceoundsOwner(boundsOwner: ChartBoundsOwner) {
        removeBoundsOwner(boundsOwner)
        addBoundsOwner(boundsOwner)
    }

    fun notifyBoundsChange(boundsOwner: ChartBoundsOwner){
        if (boundsOwner.computeBounds)
        computeBoundsConstraints(boundsOwner.anchor)
    }

    private fun computeBoundsConstraints(anchor: BoundsAnchor) {
        when(anchor) {
            BoundsAnchor.LEFT -> {
                val top = boundsOwners[BoundsAnchor.TOP]?.bounds?.bottom?:bounds.top
                val bottom = boundsOwners[BoundsAnchor.BOTTOM]?.bounds?.top?:bounds.bottom

                val owner =  boundsOwners.getValue(anchor)

                owner.bounds.coordinate.x = bounds.coordinate.x
                owner.bounds.coordinate.y = top
                owner.bounds.dimension.height = bottom - top

                owner.propagateNewBounds(owner.bounds)

                handleLeftBoundsChanges(owner)
            }
            BoundsAnchor.RIGHT -> {
                val top = boundsOwners[BoundsAnchor.TOP]?.bounds?.bottom?:bounds.top
                val bottom = boundsOwners[BoundsAnchor.BOTTOM]?.bounds?.top?:bounds.bottom
                val left = bounds.right - boundsOwners[anchor]!!.bounds.dimension.width

                val owner =  boundsOwners.getValue(anchor)

                owner.bounds.coordinate.x = left
                owner.bounds.coordinate.y = top
                owner.bounds.dimension.height = bottom - top

                owner.propagateNewBounds(owner.bounds)

                handleRightBoundsChanges(owner)
            }
            BoundsAnchor.TOP -> {
                val owner =  boundsOwners.getValue(anchor)

                if (type == Type.INNER) {
                    val x = boundsOwners[BoundsAnchor.CENTER]?.bounds?.drawableArea?.left
                    val width = boundsOwners[BoundsAnchor.CENTER]?.bounds?.drawableArea?.width

                    owner.bounds.coordinate.x = x?:bounds.left
                    owner.bounds.dimension.width = width?:(bounds.right - owner.bounds.coordinate.x)
                }
                else {
                    owner.bounds.coordinate.x = bounds.left
                    owner.bounds.dimension.width = bounds.dimension.width
                }

                owner.bounds.coordinate.y = bounds.top

                owner.propagateNewBounds(owner.bounds)

                handleTopBoundsChanges(owner)
            }
            BoundsAnchor.BOTTOM -> {
                val owner =  boundsOwners.getValue(anchor)

                if (type == Type.INNER) {
                    val x = boundsOwners[BoundsAnchor.CENTER]?.bounds?.drawableArea?.left
                    val width = boundsOwners[BoundsAnchor.CENTER]?.bounds?.drawableArea?.width

                    owner.bounds.coordinate.x = x?:bounds.left
                    owner.bounds.dimension.width = width?:(bounds.right - owner.bounds.coordinate.x)
                }
                else {
                    owner.bounds.coordinate.x = bounds.left
                    owner.bounds.dimension.width = bounds.dimension.width
                }

                owner.bounds.coordinate.y = bounds.bottom - owner.bounds.dimension.height

                owner.propagateNewBounds(owner.bounds)

                handleBottomBoundsChanges(owner)
            }
            BoundsAnchor.CENTER -> {
                val owner =  boundsOwners.getValue(anchor)

                owner.bounds.coordinate.x = boundsOwners[BoundsAnchor.LEFT]?.bounds?.right?:bounds.left
                owner.bounds.coordinate.y = boundsOwners[BoundsAnchor.TOP]?.bounds?.bottom?:bounds.top
                owner.bounds.dimension.width = (boundsOwners[BoundsAnchor.RIGHT]?.bounds?.left?:bounds.right) - owner.bounds.left
                owner.bounds.dimension.height = (boundsOwners[BoundsAnchor.BOTTOM]?.bounds?.top?:bounds.bottom) - owner.bounds.top

                owner.propagateNewBounds(owner.bounds)
            }
        }
    }

    private fun computeRemovedBoundsConstraints(anchor: BoundsAnchor) {
        when(anchor) {
            BoundsAnchor.LEFT -> {
                boundsOwners[BoundsAnchor.CENTER]?.let {
                    it.bounds.coordinate.x = bounds.coordinate.x
                    it.bounds.dimension.width = (boundsOwners[BoundsAnchor.RIGHT]?.bounds?.left?:bounds.right) - it.bounds.left
                    it.propagateNewBounds(it.bounds)
                }

                if (type == Type.INNER){
                    boundsOwners[BoundsAnchor.LEFT]?.let {
                        it.bounds.coordinate.x = bounds.coordinate.x
                        it.bounds.dimension.width = (boundsOwners[BoundsAnchor.RIGHT]?.bounds?.left?:bounds.right) - it.bounds.left
                        it.propagateNewBounds(it.bounds)
                    }
                    boundsOwners[BoundsAnchor.RIGHT]?.let {
                        it.bounds.coordinate.x = bounds.coordinate.x
                        it.bounds.dimension.width = (boundsOwners[BoundsAnchor.RIGHT]?.bounds?.left?:bounds.right) - it.bounds.left
                        it.propagateNewBounds(it.bounds)
                    }
                }
            }
            BoundsAnchor.RIGHT -> {
                boundsOwners[BoundsAnchor.CENTER]?.let {
                    it.bounds.dimension.width = bounds.right - it.bounds.left
                    it.propagateNewBounds(it.bounds)
                }

                if (type == Type.INNER){
                    boundsOwners[BoundsAnchor.TOP]?.let {
                        it.bounds.dimension.width = bounds.right - it.bounds.left
                        it.propagateNewBounds(it.bounds)
                    }
                    boundsOwners[BoundsAnchor.BOTTOM]?.let {
                        it.bounds.dimension.width = bounds.right - it.bounds.left
                        it.propagateNewBounds(it.bounds)
                    }
                }
            }
            BoundsAnchor.TOP -> {
                boundsOwners[BoundsAnchor.LEFT]?.let {
                    it.bounds.coordinate.y = bounds.top
                    it.bounds.dimension.height = (boundsOwners[BoundsAnchor.BOTTOM]?.bounds?.top?:bounds.bottom) - bounds.top
                    it.propagateNewBounds(it.bounds)
                }
                boundsOwners[BoundsAnchor.RIGHT]?.let {
                    it.bounds.coordinate.y = bounds.top
                    it.bounds.dimension.height = (boundsOwners[BoundsAnchor.BOTTOM]?.bounds?.top?:bounds.bottom) - bounds.top
                    it.propagateNewBounds(it.bounds)
                }
                boundsOwners[BoundsAnchor.CENTER]?.let {
                    it.bounds.coordinate.y = bounds.top
                    it.bounds.dimension.height = (boundsOwners[BoundsAnchor.BOTTOM]?.bounds?.top?:bounds.bottom) - bounds.top
                    it.propagateNewBounds(it.bounds)
                }
            }
            BoundsAnchor.BOTTOM -> {
                boundsOwners[BoundsAnchor.LEFT]?.let {
                    it.bounds.dimension.height = bounds.bottom - it.bounds.top
                    it.propagateNewBounds(it.bounds)
                }
                boundsOwners[BoundsAnchor.RIGHT]?.let {
                    it.bounds.dimension.height = bounds.bottom - it.bounds.top
                    it.propagateNewBounds(it.bounds)
                }
                boundsOwners[BoundsAnchor.CENTER]?.let {
                    it.bounds.dimension.height = bounds.bottom - it.bounds.top
                    it.propagateNewBounds(it.bounds)
                }
            }
            BoundsAnchor.CENTER -> {
                boundsOwners[BoundsAnchor.LEFT]?.let {
                    it.bounds.dimension.width = bounds.dimension.width / 2
                    it.propagateNewBounds(it.bounds)
                }
                boundsOwners[BoundsAnchor.RIGHT]?.let {
                    it.bounds.coordinate.x = bounds.left + bounds.dimension.width / 2
                    it.bounds.dimension.width = bounds.dimension.width / 2
                    it.propagateNewBounds(it.bounds)
                }

                if (type == Type.INNER) {

                }
            }
        }
    }

    private fun handleLeftBoundsChanges(boundsOwner: ChartBoundsOwner){
        val leftBounds = boundsOwner.bounds
        val rightBounds = boundsOwners[BoundsAnchor.RIGHT]

        boundsOwners[BoundsAnchor.CENTER]?.let {
            it.bounds.coordinate.x = leftBounds.right
            it.bounds.dimension.width = (rightBounds?.bounds?.left?:bounds.right) - leftBounds.right

            it.propagateNewBounds(it.bounds)
        }

        if (type == Type.INNER){
            boundsOwners[BoundsAnchor.TOP]?.let {
                it.bounds.coordinate.x = leftBounds.right
                it.bounds.dimension.width = (rightBounds?.bounds?.left?:bounds.right) - leftBounds.right

                it.propagateNewBounds(it.bounds)
            }

            boundsOwners[BoundsAnchor.BOTTOM]?.let {
                it.bounds.coordinate.x = leftBounds.right
                it.bounds.dimension.width = (rightBounds?.bounds?.left?:bounds.right) - leftBounds.right

                it.propagateNewBounds(it.bounds)
            }
        }
    }

    private fun handleRightBoundsChanges(boundsOwner: ChartBoundsOwner){
        val rightBounds = boundsOwner.bounds

        boundsOwners[BoundsAnchor.CENTER]?.let {
            it.bounds.dimension.width = rightBounds.left - it.bounds.left

            it.propagateNewBounds(it.bounds)
        }

        if (type == Type.INNER) {
            boundsOwners[BoundsAnchor.TOP]?.let {
                it.bounds.dimension.width = rightBounds.left - it.bounds.left

                it.propagateNewBounds(it.bounds)
            }
            boundsOwners[BoundsAnchor.BOTTOM]?.let {
                it.bounds.dimension.width = rightBounds.left - it.bounds.left

                it.propagateNewBounds(it.bounds)
            }
        }
    }

    private fun handleTopBoundsChanges(boundsOwner: ChartBoundsOwner){
        val bottomBounds = boundsOwners[BoundsAnchor.BOTTOM]

        val newTop = boundsOwner.bounds.bottom

        boundsOwners[BoundsAnchor.LEFT]?.let {
            it.bounds.coordinate.y = newTop
            it.bounds.dimension.height = (bottomBounds?.bounds?.top?:bounds.bottom) - it.bounds.top

            it.propagateNewBounds(it.bounds)
        }

        boundsOwners[BoundsAnchor.RIGHT]?.let {
            it.bounds.coordinate.y = newTop
            it.bounds.dimension.height = (bottomBounds?.bounds?.top?:bounds.bottom) - it.bounds.top

            it.propagateNewBounds(it.bounds)
        }

        boundsOwners[BoundsAnchor.CENTER]?.let {
            it.bounds.coordinate.y = newTop
            it.bounds.dimension.height = (bottomBounds?.bounds?.top?:bounds.bottom) - it.bounds.top

            it.propagateNewBounds(it.bounds)
        }
    }

    private fun handleBottomBoundsChanges(boundsOwner: ChartBoundsOwner){
        val bottomBounds = boundsOwner.bounds

        val newBottom = bottomBounds.top

        boundsOwners[BoundsAnchor.LEFT]?.let {
            it.bounds.dimension.height = newBottom - it.bounds.top

            it.propagateNewBounds(it.bounds)
        }

        boundsOwners[BoundsAnchor.RIGHT]?.let {
            it.bounds.dimension.height = newBottom - it.bounds.top

            it.propagateNewBounds(it.bounds)
        }

        boundsOwners[BoundsAnchor.CENTER]?.let {
            it.bounds.dimension.height = newBottom - it.bounds.top

            it.propagateNewBounds(it.bounds)
        }
    }

    fun getBoundsOwners(): Collection<ChartBoundsOwner> {
        return boundsOwners.values
    }
}