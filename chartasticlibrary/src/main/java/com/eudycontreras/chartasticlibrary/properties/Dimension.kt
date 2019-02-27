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
}

