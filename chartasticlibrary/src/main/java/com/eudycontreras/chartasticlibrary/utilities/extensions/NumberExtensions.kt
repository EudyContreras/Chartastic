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

fun Int.roundToNearest(scale: Float = 1.0f): Int {
    if (this >= 0) {
        return when {
            this <= 10 -> {
                this
            }
            this <= 100 -> {
                val ratio = (10 * scale).toInt()
                (this + ratio) / ratio * ratio
            }
            this <= 1_000 -> {
                val ratio = (100 * scale).toInt()
                (this + ratio) / ratio * ratio
            }
            this <= 10_000 -> {
                val ratio = (1_000 * scale).toInt()
                (this + ratio) / ratio * ratio
            }
            this <= 100_000 -> {
                val ratio = (10_000 * scale).toInt()
                (this + ratio) / ratio * ratio
            }
            this <= 1_000_000 -> {
                val ratio = (100_000 * scale).toInt()
                (this + ratio) / ratio * ratio
            }
            this <= 10_000_000 -> {
                val ratio = (1_000_000 * scale).toInt()
                (this + ratio) / ratio * ratio
            }
            this <= 100_000_000 -> {
                val ratio = (10_000_000 * scale).toInt()
                (this + ratio) / ratio * ratio
            }
            this <= 1_000_000_000 -> {
                val ratio = (100_000_000 * scale).toInt()
                (this + ratio) / ratio * ratio
            }
            this <= 10_000_000_000 -> {
                val ratio = (1_000_000_000 * scale).toInt()
                (this + ratio) / ratio * ratio
            }
            this <= 100_000_000_000 -> {
                val ratio = (10_000_000_000 * scale).toInt()
                (this + ratio) / ratio * ratio
            }
            else -> {
                this
            }
        }
    } else {
        return when {
            this >= -10 -> {
                this
            }
            this >= -100 -> {
                val ratio = (10 * scale).toInt()
                (this + -ratio) / ratio * ratio
            }
            this >= -1_000 -> {
                val ratio = (100 * scale).toInt()
                (this + -ratio) / ratio * ratio
            }
            this >= -10_000 -> {
                val ratio = (1_000 * scale).toInt()
                (this + -ratio) / ratio * ratio
            }
            this >= -100_000 -> {
                val ratio = (10_000 * scale).toInt()
                (this + -ratio) / ratio * ratio
            }
            this >= -1_000_000 -> {
                val ratio = (100_000 * scale).toInt()
                (this + -ratio) / ratio * ratio
            }
            this >= -10_000_000 -> {
                val ratio = (1_000_000 * scale).toInt()
                (this + -ratio) / ratio * ratio
            }
            this >= -100_000_000 -> {
                val ratio = (10_000_000 * scale).toInt()
                (this + -ratio) / ratio * ratio
            }
            this >= -1_000_000_000 -> {
                val ratio = (100_000_000 * scale).toInt()
                (this + -ratio) / ratio * ratio
            }
            this >= -10_000_000_000 -> {
                val ratio = (1_000_000_000 * scale).toInt()
                (this + -ratio) / ratio * ratio
            }
            this >= -100_000_000_000 -> {
                val ratio = (10_000_000_000 * scale).toInt()
                (this + -ratio) / ratio * ratio
            }
            this >= -1_000_000_000_000 -> {
                val ratio = (100_000_000_000 * scale).toInt()
                (this + -ratio) / ratio * ratio
            }
            this >= -10_000_000_000_000 -> {
                val ratio = (1_000_000_000_000 * scale).toInt()
                (this + -ratio) / ratio * ratio
            }
            this >= -100_000_000_000_000 -> {
                val ratio = (10_000_000_000_000 * scale).toInt()
                (this + -ratio) / ratio * ratio
            }
            this >= -1_000_000_000_000_000 -> {
                val ratio = (100_000_000_000_000 * scale).toInt()
                (this + -ratio) / ratio * ratio
            }
            this >= -10_000_000_000_000_000 -> {
                val ratio = (1_000_000_000_000_000 * scale).toInt()
                (this + -ratio) / ratio * ratio
            }
            this >= -100_000_000_000_000_000 -> {
                val ratio = (10_000_000_000_000_000 * scale).toInt()
                (this + -ratio) / ratio * ratio
            }
            else -> {
                this
            }
        }
    }
}

fun Long.roundToNearest(): Long {
    if (this >= 0) {
        when {
            this < 10 -> {
                return this
            }
            this < 100 -> {
                return (this + 10) / 10 * 10
            }
            this < 1_000 -> {
                return (this + 100) / 100 * 100
            }
            this < 10_000 -> {
                return (this + 1_000) / 1_000 * 1_000
            }
            this < 100_000 -> {
                return (this + 10_000) / 10_000 * 10_000
            }
            this < 1_000_000 -> {
                return (this + 100_000) / 100_000 * 100_000
            }
            this < 10_000_000 -> {
                return (this + 1_000_000) / 1_000_000 * 1_000_000
            }
            this < 100_000_000 -> {
                return (this + 10_000_000) / 10_000_000 * 10_000_000
            }
            this < 1_000_000_000 -> {
                return (this + 100_000_000) / 100_000_000 * 100_000_000
            }
            this < 10_000_000_000 -> {
                return (this + 1_000_000_000) / 1_000_000_000 * 1_000_000_000
            }
            this < 100_000_000_000 -> {
                return (this + 10_000_000_000) / 10_000_000_000 * 10_000_000_000
            }
            this < 1_000_000_000_000 -> {
                return (this + 100_000_000_000) / 100_000_000_000 * 100_000_000_000
            }
        }
    } else {
        when {
            this > -10-1 -> {
                return this
            }
            this > -100-1 -> {
                return (this + -10) / 10 * 10
            }
            this > -1_000-1 -> {
                return (this + -100) / 100 * 100
            }
            this > -10_000-1 -> {
                return (this + -1_000) / 1_000 * 1_000
            }
            this > -100_000-1 -> {
                return (this + -10_000) / 10_000 * 10_000
            }
            this > -1_000_000-1 -> {
                return (this + -100_000) / 100_000 * 100_000
            }
            this > -10_000_000-1 -> {
                return (this + -1_000_000) / 1_000_000 * 1_000_000
            }
            this < -100_000_000-1 -> {
                return (this + 10_000_000) / 10_000_000 * 10_000_000
            }
            this < 1_000_000_000-1 -> {
                return (this + 100_000_000) / 100_000_000 * 100_000_000
            }
            this < 10_000_000_000-1 -> {
                return (this + 1_000_000_000) / 1_000_000_000 * 1_000_000_000
            }
            this < 100_000_000_000-1 -> {
                return (this + 10_000_000_000) / 10_000_000_000 * 10_000_000_000
            }
            this < 1_000_000_000_000-1 -> {
                return (this + -100_000_000_000) / 100_000_000_000 * 100_000_000_000
            }
        }
    }
    return this
}

fun Double.roundToNearest(): Double {
    if (this > 0.0) {
        when {
            this < 10.0 -> {
                return this
            }
            this < 100.0 -> {
                return (this + 1) / 1 * 1
            }
            this < 1_000.0 -> {
                return (this + 10.0) / 10.0 * 10.0
            }
            this < 10_000.0 -> {
                return (this + 100.0) / 100.0 * 100.0
            }
            this < 100_000.0 -> {
                return (this + 1_000.0) / 1_000.0 * 1_000.0
            }
            this < 1_000_000.0 -> {
                return (this + 10_000.0) / 10_000.0 * 10_000.0
            }
            this < 10_000_000.0 -> {
                return (this + 100_000.0) / 100_000.0 * 100_000.0
            }
            this < 100_000_000.0 -> {
                return (this + 1_000_000.0) / 1_000_000.0 * 1_000_000.0
            }
            this < 1_000_000_000.0 -> {
                return (this + 10_000_000.0) / 10_000_000.0 * 10_000_000.0
            }
            this < 10_000_000_000.0 -> {
                return (this + 100_000_000.0) / 100_000_000.0 * 100_000_000.0
            }
            this < 100_000_000_000.0 -> {
                return (this + 1_000_000_000.0) / 1_000_000_000.0 * 1_000_000_000.0
            }
            this < 1_000_000_000_000.0 -> {
                return (this + 10_000_000_000.0) / 10_000_000_000.0 * 10_000_000_000.0
            }
        }
    } else {
        when {
            this < -10.0-1 -> {
                return this
            }
            this < -100.0-1 -> {
                return (this - 1) / -1 * -1
            }
            this < -1_000.0-1 -> {
                return (this - 10.0) / -10.0 * -10.0
            }
            this < -10_000.0-1 -> {
                return (this - 100.0) / -100.0 * -100.0
            }
            this < -100_000.0-1 -> {
                return (this - 1_000.0) / -1_000.0 * -1_000.0
            }
            this < -1_000_000.0-1 -> {
                return (this + 10_000.0) / 10_000.0 * 10_000.0
            }
            this < -10_000_000.0-1 -> {
                return (this + 100_000.0) / 100_000.0 * 100_000.0
            }
            this < 100_000_000.0-1 -> {
                return (this + 1_000_000.0) / 1_000_000.0 * 1_000_000.0
            }
            this < 1_000_000_000.0-1 -> {
                return (this + 10_000_000.0) / 10_000_000.0 * 10_000_000.0
            }
            this < 10_000_000_000.0-1 -> {
                return (this + 100_000_000.0) / 100_000_000.0 * 100_000_000.0
            }
            this < 100_000_000_000.0-1 -> {
                return (this + 1_000_000_000.0) / 1_000_000_000.0 * 1_000_000_000.0
            }
            this < 1_000_000_000_000.0-1 -> {
                return (this + 10_000_000_000.0) / 10_000_000_000.0 * 10_000_000_000.0
            }
        }
    }
    return this
}

fun Float.roundToNearest(): Float {
    if (this >= 0) {
        when {
            this < 10 -> {
                return this
            }
            this < 100 -> {
                return (this + 10) / 10 * 10
            }
            this < 1_000 -> {
                return (this + 100) / 100 * 100
            }
            this < 10_000 -> {
                return (this + 1_000) / 1_000 * 1_000
            }
            this < 100_000 -> {
                return (this + 10_000) / 10_000 * 10_000
            }
            this < 1_000_000 -> {
                return (this + 100_000) / 100_000 * 100_000
            }
            this < 10_000_000 -> {
                return (this + 1_000_000) / 1_000_000 * 1_000_000
            }
            this < 100_000_000 -> {
                return (this + 10_000_000) / 10_000_000 * 10_000_000
            }
            this < 1_000_000_000 -> {
                return (this + 100_000_000) / 100_000_000 * 100_000_000
            }
            this < 10_000_000_000 -> {
                return (this + 1_000_000_000) / 1_000_000_000 * 1_000_000_000
            }
            this < 100_000_000_000 -> {
                return (this + 10_000_000_000) / 10_000_000_000 * 10_000_000_000
            }
            this < 1_000_000_000_000 -> {
                return (this + 100_000_000_000) / 100_000_000_000 * 100_000_000_000
            }
        }
    } else {
        when {
            this > -10-1 -> {
                return this
            }
            this > -100-1 -> {
                return (this + -10) / 10 * 10
            }
            this > -1_000-1 -> {
                return (this + -100) / 100 * 100
            }
            this > -10_000-1 -> {
                return (this + -1_000) / 1_000 * 1_000
            }
            this > -100_000-1 -> {
                return (this + -10_000) / 10_000 * 10_000
            }
            this > -1_000_000-1 -> {
                return (this + -100_000) / 100_000 * 100_000
            }
            this > -10_000_000-1 -> {
                return (this + -1_000_000) / 1_000_000 * 1_000_000
            }
            this < -100_000_000-1 -> {
                return (this + 10_000_000) / 10_000_000 * 10_000_000
            }
            this < 1_000_000_000-1 -> {
                return (this + 100_000_000) / 100_000_000 * 100_000_000
            }
            this < 10_000_000_000-1 -> {
                return (this + 1_000_000_000) / 1_000_000_000 * 1_000_000_000
            }
            this < 100_000_000_000-1 -> {
                return (this + 10_000_000_000) / 10_000_000_000 * 10_000_000_000
            }
            this < 1_000_000_000_000-1 -> {
                return (this + -100_000_000_000) / 100_000_000_000 * 100_000_000_000
            }
        }
    }
    return this
}