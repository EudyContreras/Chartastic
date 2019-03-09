package com.eudycontreras.chartasticlibrary.charts

import com.eudycontreras.chartasticlibrary.properties.Bounds

/**
 * Created by eudycontreras.
 */

class ChartBoundsManager(private val parentBounds: Bounds){

    enum class BoundsAnchor {
        LEFT,
        RIGHT,
        TOP,
        BOTTOM,
        CENTER
    }

    interface ChartBoundsOwner {

        var computeBounds: Boolean
        var drawBounds: Boolean
        val anchor: BoundsAnchor
        val bounds: Bounds

        fun notifyBoundsChange(bounds: Bounds)
        fun propagateNewBounds(bounds: Bounds)
    }

    private val boundsOwners = LinkedHashMap<BoundsAnchor,ChartBoundsOwner>()

    fun addBoundsOwner(boundsOwner: ChartBoundsOwner) {
        boundsOwners[boundsOwner.anchor] = boundsOwner
        computeBoundsConstraints(boundsOwner.anchor)
    }

    fun removeBoundsOwner(boundsOwner: ChartBoundsOwner) {
        boundsOwner.computeBounds = false
        boundsOwners.remove(boundsOwner.anchor)
        computeRemovedBoundsConstraints(boundsOwner.anchor)
    }

    fun notifyBoundsChange(boundsOwner: ChartBoundsOwner){
        computeBoundsConstraints(boundsOwner.anchor)
    }

    private fun computeBoundsConstraints(anchor: BoundsAnchor) {
        when(anchor) {
            BoundsAnchor.LEFT -> {
                val top = boundsOwners[BoundsAnchor.TOP]?.bounds?.bottom?:parentBounds.top
                val bottom = boundsOwners[BoundsAnchor.BOTTOM]?.bounds?.top?:parentBounds.bottom

                val owner =  boundsOwners.getValue(anchor)

                owner.bounds.coordinate.x = parentBounds.coordinate.x
                owner.bounds.coordinate.y = top
                owner.bounds.dimension.height = bottom - top

                owner.propagateNewBounds(owner.bounds)

                handleLeftBoundsChanges(owner)
            }
            BoundsAnchor.RIGHT -> {
                val top = boundsOwners[BoundsAnchor.TOP]?.bounds?.bottom?:parentBounds.top
                val bottom = boundsOwners[BoundsAnchor.BOTTOM]?.bounds?.top?:parentBounds.bottom
                val left = parentBounds.right - boundsOwners[anchor]!!.bounds.dimension.width

                val owner =  boundsOwners.getValue(anchor)

                owner.bounds.coordinate.x = left
                owner.bounds.coordinate.y = top
                owner.bounds.dimension.height = bottom - top

                owner.propagateNewBounds(owner.bounds)

                handleRightBoundsChanges(owner)
            }
            BoundsAnchor.TOP -> {
                val owner =  boundsOwners.getValue(anchor)

                owner.bounds.coordinate.x = parentBounds.left
                owner.bounds.coordinate.y = parentBounds.top
                owner.bounds.dimension.width = parentBounds.dimension.width

                owner.propagateNewBounds(owner.bounds)

                handleTopBoundsChanges(owner)
            }
            BoundsAnchor.BOTTOM -> {
                val owner =  boundsOwners.getValue(anchor)

                owner.bounds.coordinate.x = parentBounds.left
                owner.bounds.coordinate.y = parentBounds.bottom - owner.bounds.dimension.height
                owner.bounds.dimension.width = parentBounds.dimension.width

                owner.propagateNewBounds(owner.bounds)

                handleBottomBoundsChanges(owner)
            }
            BoundsAnchor.CENTER -> {
                val owner =  boundsOwners.getValue(anchor)

                owner.bounds.coordinate.x = boundsOwners[BoundsAnchor.LEFT]?.bounds?.right?:parentBounds.left
                owner.bounds.coordinate.y = boundsOwners[BoundsAnchor.TOP]?.bounds?.bottom?:parentBounds.top
                owner.bounds.dimension.width = boundsOwners[BoundsAnchor.RIGHT]?.bounds?.left?:parentBounds.right - owner.bounds.left
                owner.bounds.dimension.height = boundsOwners[BoundsAnchor.BOTTOM]?.bounds?.top?:parentBounds.bottom - owner.bounds.top

                owner.propagateNewBounds(owner.bounds)
            }
        }
    }

    private fun computeRemovedBoundsConstraints(anchor: BoundsAnchor) {
        when(anchor) {
            BoundsAnchor.LEFT -> {
                boundsOwners[BoundsAnchor.CENTER]?.let {
                    it.bounds.coordinate.x = parentBounds.coordinate.x
                    it.bounds.dimension.width = boundsOwners[BoundsAnchor.RIGHT]?.bounds?.left?:parentBounds.right - it.bounds.left
                    it.propagateNewBounds(it.bounds)
                }
            }
            BoundsAnchor.RIGHT -> {
                boundsOwners[BoundsAnchor.CENTER]?.let {
                    it.bounds.dimension.width = parentBounds.right - it.bounds.left
                    it.propagateNewBounds(it.bounds)
                }
            }
            BoundsAnchor.TOP -> {
                boundsOwners[BoundsAnchor.CENTER]?.let {
                    it.bounds.coordinate.y = parentBounds.top
                    it.bounds.dimension.height = boundsOwners[BoundsAnchor.BOTTOM]?.bounds?.top?:parentBounds.bottom - it.bounds.top
                    it.propagateNewBounds(it.bounds)
                }
                boundsOwners[BoundsAnchor.LEFT]?.let {
                    it.bounds.coordinate.y = parentBounds.top
                    it.bounds.dimension.height = boundsOwners[BoundsAnchor.BOTTOM]?.bounds?.top?:parentBounds.bottom - it.bounds.top
                    it.propagateNewBounds(it.bounds)
                }
                boundsOwners[BoundsAnchor.RIGHT]?.let {
                    it.bounds.coordinate.y = parentBounds.top
                    it.bounds.dimension.height = boundsOwners[BoundsAnchor.BOTTOM]?.bounds?.top?:parentBounds.bottom - it.bounds.top
                    it.propagateNewBounds(it.bounds)
                }
            }
            BoundsAnchor.BOTTOM -> {
                boundsOwners[BoundsAnchor.CENTER]?.let {
                    it.bounds.dimension.height = boundsOwners[BoundsAnchor.BOTTOM]?.bounds?.top?:parentBounds.bottom - it.bounds.top
                    it.propagateNewBounds(it.bounds)
                }
                boundsOwners[BoundsAnchor.LEFT]?.let {
                    it.bounds.dimension.height = boundsOwners[BoundsAnchor.BOTTOM]?.bounds?.top?:parentBounds.bottom - it.bounds.top
                    it.propagateNewBounds(it.bounds)
                }
                boundsOwners[BoundsAnchor.RIGHT]?.let {
                    it.bounds.dimension.height = boundsOwners[BoundsAnchor.BOTTOM]?.bounds?.top?:parentBounds.bottom - it.bounds.top
                    it.propagateNewBounds(it.bounds)
                }
            }
            BoundsAnchor.CENTER -> {
                boundsOwners[BoundsAnchor.LEFT]?.let {
                    it.bounds.dimension.width = parentBounds.dimension.width / 2
                    it.propagateNewBounds(it.bounds)
                }
                boundsOwners[BoundsAnchor.RIGHT]?.let {
                    it.bounds.coordinate.x = parentBounds.left + parentBounds.dimension.width / 2
                    it.bounds.dimension.width = parentBounds.dimension.width / 2
                    it.propagateNewBounds(it.bounds)
                }
            }
        }
    }

    private fun handleLeftBoundsChanges(boundsOwner: ChartBoundsOwner){
        val leftBounds = boundsOwner.bounds
        val rightBounds = boundsOwners[BoundsAnchor.RIGHT]

        boundsOwners[BoundsAnchor.CENTER]?.let {
            it.bounds.coordinate.x = leftBounds.right
            it.bounds.dimension.width = rightBounds?.bounds?.left?:parentBounds.right - leftBounds.right

            it.propagateNewBounds(it.bounds)
        }
    }

    private fun handleRightBoundsChanges(boundsOwner: ChartBoundsOwner){
        val rightBounds = boundsOwner.bounds

        boundsOwners[BoundsAnchor.CENTER]?.let {
            it.bounds.dimension.width = rightBounds.left - it.bounds.left

            it.propagateNewBounds(it.bounds)
        }
    }

    private fun handleTopBoundsChanges(boundsOwner: ChartBoundsOwner){
        val bottomBounds = boundsOwners[BoundsAnchor.BOTTOM]

        val newTop = boundsOwner.bounds.bottom

        boundsOwners[BoundsAnchor.LEFT]?.let {
            it.bounds.coordinate.y = newTop
            it.bounds.dimension.height = bottomBounds?.bounds?.top?:parentBounds.bottom - it.bounds.top

            it.propagateNewBounds(it.bounds)
        }

        boundsOwners[BoundsAnchor.RIGHT]?.let {
            it.bounds.coordinate.y = newTop
            it.bounds.dimension.height = bottomBounds?.bounds?.top?:parentBounds.bottom - it.bounds.top

            it.propagateNewBounds(it.bounds)
        }

        boundsOwners[BoundsAnchor.CENTER]?.let {
            it.bounds.coordinate.y = newTop
            it.bounds.dimension.height = bottomBounds?.bounds?.top?:parentBounds.bottom - it.bounds.top

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