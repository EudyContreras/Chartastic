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

    fun copy(cornerRadii: CornerRadii) {
        this.rx = cornerRadii.rx
        this.ry = cornerRadii.ry

        this.topLeft = cornerRadii.topLeft
        this.topRight = cornerRadii.topRight
        this.bottomLeft = cornerRadii.bottomLeft
        this.bottomRight = cornerRadii.bottomRight
    }
}