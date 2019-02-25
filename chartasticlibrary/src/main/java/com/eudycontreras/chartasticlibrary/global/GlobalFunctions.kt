package com.eudycontreras.chartasticlibrary.global

/**
 * Created by eudycontreras.
 */

enum class Notation {
    SHORT,
    LONG,
    CURRENCY
}
fun mapRange(value: Long, fromMin: Long, fromMax: Long, toMin: Long, toMax: Long): Long {
    return (value - fromMin) * (toMax - toMin) / (fromMax - fromMin) + toMin
}

fun mapRange(value: Float, fromMin: Float, fromMax: Float, toMin: Float, toMax: Float): Float {
    return (value - fromMin) * (toMax - toMin) / (fromMax - fromMin) + toMin
}

fun mapRange(value: Double, fromMin: Double, fromMax: Double, toMin: Double, toMax: Double): Double {
    return (value - fromMin) * (toMax - toMin) / (fromMax - fromMin) + toMin
}

fun mapRange(value: Int, fromMin: Int, fromMax: Int, toMin: Int, toMax: Int): Int {
    return (value - fromMin) * (toMax - toMin) / (fromMax - fromMin) + toMin
}