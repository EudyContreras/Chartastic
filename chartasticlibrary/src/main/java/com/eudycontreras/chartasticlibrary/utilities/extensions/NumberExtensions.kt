package com.eudycontreras.chartasticlibrary.utilities.extensions

import android.content.res.Resources

/**
 * Created by eudycontreras.
 */
val Int.f: Float
    get() = this.toFloat()

val Float.i: Int
    get() = this.toInt()

/**
 * Use the float as density independent pixels and return the pixel value
 */
val Int.dp: Float
    get() = this * Resources.getSystem().displayMetrics.density

val Float.dp: Float
    get() = this * Resources.getSystem().displayMetrics.density

/**
 * Use the float as scale independent pixels and return the pixel value
 */
val Int.sp: Float
    get() = this * Resources.getSystem().displayMetrics.scaledDensity

val Float.sp: Float
    get() = this * Resources.getSystem().displayMetrics.scaledDensity


fun Int.clamp(maxValue: Int, minRatio: Float, scale: Int): Int {
    val ratio = (maxValue.toFloat() / this.toFloat()) / scale
    if (ratio < minRatio) {
        val increase = (minRatio * 10f)
        val result = Math.floor((maxValue / increase).toDouble())
        return result.toInt()
    }
    return this
}

private var positiveScale = intArrayOf(10, 100, 1_000, 10_000, 100_000, 1_000_000, 10_000_000, 100_000_000, 1_000_000_000)
private var negativeScale = intArrayOf(-10, -100, -1_000, -10_000, -100_000, -1_000_000, -10_000_000, -100_000_000, -1_000_000_000)

fun Int.magnitude(): Pair<Int, Float> {
    return when{
        this <= 4 -> Pair(2, 0.25f)
        this <= 9 -> Pair(2, 0.5f)
        this <= 19 -> Pair(2, 0.5f)
        this <= 49 -> Pair(2, 0.5f)
        this <= 74 -> Pair(2, 0.5f)
        this <= 99 -> Pair(1, 0.5f)
        else -> Pair(1, 1f)
    }
}

fun Int.normalize(ratio: Float = 1.0f): Int{

    val shift = 1
    val multiplier = 10
    val divider = if (ratio < 0.5) 0.2f else if (ratio < 1.0 && Math.abs(this) < 100) 0.5f else 1.0f

    if (this > 0) {
        if (this <= positiveScale[0]) {
            return  (this + 5) / 5 * 5
        } else {
            for(i in 1 until positiveScale.size) {

                val scale = positiveScale[i]

                if(this <= scale) {

                    val actual = (positiveScale[i - shift] * divider).toInt()

                    if(i > 1) {
                        if (this < ((actual * multiplier) * ratio)) {
                            val previous = (positiveScale[i - (shift + 1)] * divider).toInt()

                            val value = (this + previous) / previous * previous

                            return if ((((this - 1) + actual) / actual * actual) == value) {
                                this
                            } else {
                                value
                            }
                        }
                    }
                    return (this + actual) / actual * actual
                }
            }
        }
    } else if (this < 0) {
        if (this >= negativeScale[0]) {
            return (this + -5) / 5 * 5
        } else {

            for(i in 1 until negativeScale.size) {

                val scale = negativeScale[i]

                if(this >= scale) {

                    val actual = (positiveScale[i - shift] * divider).toInt()

                    if(i > 1) {
                        if (this > ((-actual * multiplier) * 0.5f)) {
                            val previous = (positiveScale[i - (shift + 1)] * divider).toInt()
                            return (this + -previous) / previous * previous
                        }
                    }
                    return (this + -actual) / actual * actual
                }
            }
        }
    }
    return this
}

enum class RoundMethod {
    UP,
    DOWN
}

fun Int.roundToNearest(ratio: Float = 1.0f, shift: Int = 2, multiple: Int? = null, method: RoundMethod = RoundMethod.UP): Int {
    if(this == 0) {
        return this
    }
    val scaleDivider = 2f
    val min = positiveScale[0] /  2
    val multiplier = 0.75f

    if (this > 0 && this <= positiveScale[0]) {
        return  getRounding(this, min, method)
    } else if (this < 0 && this >= negativeScale[0]){
        return  getRounding(this, min, method)
    } else {
        for(i in shift until positiveScale.size) {
            var actual = (positiveScale[i - shift] * ratio).toInt()

            if(this >= 0) {
                if(this <= multiple?:positiveScale[i]) {
                    val value = getRounding(this, actual, method)
                    if(this < value * multiplier) {
                        actual = (positiveScale[i - shift] * ratio / scaleDivider).toInt()
                        return getRounding(this, actual, method)
                    }
                    return  value
                }
            } else {
                if(this >= if (multiple != null) -multiple else negativeScale[i]) {
                    val value = getRounding(this, actual, method)
                    if(this > value * multiplier) {
                        actual = (positiveScale[i - shift] * ratio / scaleDivider).toInt()
                        return getRounding(this, actual, method)
                    }
                    return value
                }
            }
        }
    }
    return this
}

private fun getRounding(value: Int, scale: Int, method: RoundMethod): Int {
    return when (method) {
        RoundMethod.UP ->  (value + if (value < 0) -scale else scale) / scale * scale
        RoundMethod.DOWN -> (value - if (value < 0) -scale else scale) / scale * scale
    }
}

fun Long.roundToNearest(): Long {
    return this
}

fun Double.roundToNearest(): Double {
    return this
}

fun Float.roundToNearest(): Float {
    return this
}