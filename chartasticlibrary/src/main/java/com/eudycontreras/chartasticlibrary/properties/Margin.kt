package com.eudycontreras.chartasticlibrary.properties

/**
 * Created by eudycontreras.
 */
data class Margin(
    val start: Float = 0f,
    val end: Float = 0f,
    val top: Float = 0f,
    val bottom: Float = 0f
) {
    constructor(all: Float):this(all, all, all, all)
}