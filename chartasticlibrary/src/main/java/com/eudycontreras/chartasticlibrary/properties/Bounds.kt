package com.eudycontreras.chartasticlibrary.properties

import com.eudycontreras.chartasticlibrary.charts.ChartBoundsManager

/**
 * Created by eudycontreras.
 */

data class Bounds(
    var coordinate: Coordinate = Coordinate(),
    var dimension: Dimension = Dimension()
) {

    private var boundsOwner: ChartBoundsManager.ChartBoundsOwner? = null

    constructor(boundsOwner: ChartBoundsManager.ChartBoundsOwner) : this() {
        this.boundsOwner = boundsOwner
    }

    val left: Float
        get() = coordinate.x

    val right: Float
        get() = coordinate.x + dimension.width

    val top: Float
        get() = coordinate.y

    val bottom: Float
        get() = coordinate.y + dimension.height

    var paddings: Padding? = null

    var margins: Margin? = null
        set(value) {
            field = value
            boundsOwner?.notifyBoundsChange(this)
        }

    val drawableArea: Bounds
        get() {
            return if (margins != null) {
                this.subtract(margins!!)
            } else {
                this
            }
        }

    fun copyProps(): Bounds {
        val coordinates = this.coordinate.copyProps()
        val dimensions = this.dimension.copyProps()
        return Bounds(coordinates, dimensions)
    }

    fun intercepts(other: Bounds): Boolean {
        val x = this.coordinate.x
        val y = this.coordinate.y
        val width = this.dimension.width
        val height = this.dimension.height

        return x < other.coordinate.x + other.dimension.width && x + width > other.coordinate.x && y < other.coordinate.y + other.dimension.height && y + height > other.coordinate.y
    }

    fun isInside(x: Float, y: Float): Boolean {
        return (x > left && x < right && y > top && y < bottom)
    }

    fun isInside(bounds: Bounds): Boolean {
        return isInside(bounds.top, bounds.left, bounds.bottom, bounds.right)
    }

    fun isInside(top: Float, left: Float, bottom: Float, right: Float): Boolean {
        return this.top >= top && this.left >= left && this.bottom <= bottom && this.right <= right
    }

    fun add(dp: Float): Bounds {
        val newX = coordinate.x - dp
        val newY = coordinate.y - dp
        val newWidth = dimension.width + (dp * 2)
        val newHeight = dimension.height + (dp * 2)
        return Bounds(Coordinate(newX, newY), Dimension(newWidth, newHeight))
    }

     fun subtract(dp: Float): Bounds {
        val newX = coordinate.x + dp
        val newY = coordinate.y + dp
        val newWidth = dimension.width - (dp * 2)
        val newHeight = dimension.height - (dp * 2)
        return Bounds(Coordinate(newX, newY), Dimension(newWidth, newHeight))
    }

    fun subtract(margin: Margin): Bounds {
        val newX = coordinate.x + margin.start
        val newY = coordinate.y + margin.top
        val newWidth = dimension.width - (margin.end + margin.start)
        val newHeight = dimension.height - (margin.bottom + margin.top)
        return Bounds(Coordinate(newX, newY), Dimension(newWidth, newHeight))
    }

    fun subtractDimension(width: Float, height: Float): Bounds {
        val newWidth = dimension.width - width
        val newHeight = dimension.height - height
        return Bounds(Coordinate(coordinate.x, coordinate.y), Dimension(newWidth, newHeight))
    }

    fun subtractCoordinates(x: Float, y: Float): Bounds {
        val newX = coordinate.x + x
        val newY = coordinate.y + y
        return Bounds(Coordinate(newX, newY), Dimension(dimension.width, dimension.height))
    }

    fun addDimension(width: Float, height: Float): Bounds {
        val newWidth = dimension.width + width
        val newHeight = dimension.height + height
        return Bounds(Coordinate(coordinate.x, coordinate.y), Dimension(newWidth, newHeight))
    }

    fun addCoordinates(x: Float, y: Float): Bounds {
        val newX = coordinate.x - x
        val newY = coordinate.y - y
        return Bounds(Coordinate(newX, newY), Dimension(dimension.width, dimension.height))
    }

    fun updateCoordinates(x: Float, y: Float, notifyChange: Boolean = true) {
        boundsOwner?.notifyBoundsChange(this)
        coordinate.x = x
        coordinate.y = y
        if (notifyChange) {
            boundsOwner?.notifyBoundsChange(this)
        }
    }

    fun updateDimensions(width: Float, height: Float, notifyChange: Boolean = true) {
        boundsOwner?.notifyBoundsChange(this)
        dimension.width = width
        dimension.height = height
        if (notifyChange) {
            boundsOwner?.notifyBoundsChange(this)
        }
    }

    fun update(bounds: Bounds, notifyChange: Boolean = true) {
        this.coordinate.x = bounds.coordinate.x
        this.coordinate.y = bounds.coordinate.y
        this.dimension.width = bounds.dimension.width
        this.dimension.height = bounds.dimension.height
        if (notifyChange) {
            boundsOwner?.notifyBoundsChange(this)
        }
    }
}

