package com.eudycontreras.chartasticlibrary.properties

/**
 * Created by eudycontreras.
 */
class CornerRadii {
    var rx: Float = 0f
    var ry: Float = 0f

    var topLeft: Boolean = false
    var topRight: Boolean = false
    var bottomLeft: Boolean = false
    var bottomRight: Boolean = false

    fun reset() {
        rx = 0f
        ry = 0f

        topLeft = false
        topRight = false
        bottomLeft = false
        bottomRight = false
    }
}