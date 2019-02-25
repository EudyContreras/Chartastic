package com.eudycontreras.chartasticlibrary.properties

/**
 * Created by eudycontreras.
 */
data class Gradient(
    val colorOne: Color,
    val colorTwo: Color,
    val type: Int = TOP_TO_BOTTOM
) {
    companion object {
        const val TOP_TO_BOTTOM = 0
        const val BOTTOM_TO_TOP = 1
        const val LEFT_TO_RIGHT = 2
        const val RIGHT_TO_LEFT = 3
    }
}