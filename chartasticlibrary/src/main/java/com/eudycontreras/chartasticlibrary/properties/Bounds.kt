package com.eudycontreras.chartasticlibrary.properties

/**
 * Created by eudycontreras.
 */

data class Bounds(
    var coordinate: Coordinate = Coordinate(),
    var dimension: Dimension = Dimension()
) {
    val left: Float
        get() = coordinate.x

    val right: Float
        get() = coordinate.x + dimension.width

    val top: Float
        get() = coordinate.y

    val bottom: Float
        get() = coordinate.y + dimension.height

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

    fun subtract(dp: Float): Bounds {
        val newX = coordinate.x + dp
        val newY = coordinate.y + dp
        val newWidth = dimension.width - (dp * 2)
        val newHeight = dimension.height - (dp * 2)
        return Bounds(Coordinate(newX, newY), Dimension(newWidth, newHeight))
    }

    fun subtractDimenion(width: Float, height: Float): Bounds {
        val newWidth = dimension.width - (width * 2)
        val newHeight = dimension.height - (height * 2)
        return Bounds(Coordinate(coordinate.x, coordinate.y), Dimension(newWidth, newHeight))
    }

    fun subtractCoordinates(x: Float, y: Float): Bounds {
        val newX = coordinate.x + x
        val newY = coordinate.y + y
        return Bounds(Coordinate(newX, newY), Dimension(dimension.width, dimension.height))
    }

    fun add(dp: Float): Bounds {
        val newX = coordinate.x - dp
        val newY = coordinate.y - dp
        val newWidth = dimension.width + dp
        val newHeight = dimension.height + dp
        return Bounds(Coordinate(newX, newY), Dimension(newWidth, newHeight))
    }

    fun addtDimenion(width: Float, height: Float): Bounds {
        val newWidth = dimension.width + (width * 2)
        val newHeight = dimension.height + (height * 2)
        return Bounds(Coordinate(coordinate.x, coordinate.y), Dimension(newWidth, newHeight))
    }

    fun addCoordinates(x: Float, y: Float): Bounds {
        val newX = coordinate.x - x
        val newY = coordinate.y - y
        return Bounds(Coordinate(newX, newY), Dimension(dimension.width, dimension.height))
    }
}

