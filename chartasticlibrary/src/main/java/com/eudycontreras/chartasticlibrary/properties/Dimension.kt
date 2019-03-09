package com.eudycontreras.chartasticlibrary.properties

/**
 * Created by eudycontreras.
 */

data class Dimension(
    var width: Float = 0f,
    var height: Float = 0f
) {

    operator fun plus(other: Dimension): Dimension {
        this.width += other.width
        this.height += other.height
        return this
    }

    fun subtractWidth(dp: Float): Dimension {
        val newWidth = width - (dp * 2)
        return Dimension(newWidth, this.height)
    }

    fun addWidth(dp: Float): Dimension {
        val newWidth = width - (dp * 2)
        return Dimension(newWidth, this.height)
    }

    fun subtractHeight(dp: Float): Dimension {
        val newHeight = height - (dp * 2)
        return Dimension(this.width, newHeight)
    }

    fun addHeiggt(dp: Float): Dimension {
        val newHeight = height - (dp * 2)
        return Dimension(this.width, newHeight)
    }

    operator fun plusAssign(other: Dimension) {
        this.width += other.width
        this.height += other.height
    }

    operator fun minus(other: Dimension): Dimension {
        this.width -= other.width
        this.height -= other.height
        return this
    }

    operator fun minusAssign(other: Dimension) {
        this.width -= other.width
        this.height -= other.height
    }

    fun copyProps(): Dimension {
        return Dimension(width, height)
    }
}

