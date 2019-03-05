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

fun Int.roundToNearest(): Int {
    if (this > 0) {
        when {
            this < 10 -> {
                return this
            }
            this < 100 -> {
                return (this + 1) / 1 * 1
            }
            this < 1_000 -> {
                return (this + 10) / 10 * 10
            }
            this < 10_000 -> {
                return (this + 100) / 100 * 100
            }
            this < 100_000 -> {
                return (this + 1_000) / 1_000 * 1_000
            }
            this < 1_000_000 -> {
                return (this + 10_000) / 10_000 * 10_000
            }
            this < 10_000_000 -> {
                return (this + 100_000) / 100_000 * 100_000
            }
            this < 100_000_000 -> {
                return (this + 1_000_000) / 1_000_000 * 1_000_000
            }
            this < 1_000_000_000 -> {
                return (this + 10_000_000) / 10_000_000 * 10_000_000
            }
            this < 10_000_000_000 -> {
                return (this + 100_000_000) / 100_000_000 * 100_000_000
            }
            this < 100_000_000_000 -> {
                return (this + 1_000_000_000) / 1_000_000_000 * 1_000_000_000
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
    }
    return this
}

fun Float.roundToNearest(): Float {
    if (this > 0f) {
        when {
            this < 10f -> {
                return this
            }
            this < 100f -> {
                return (this + 1f) / 1f * 1f
            }
            this < 1_000f -> {
                return (this + 10f) / 10f * 10f
            }
            this < 10_000f -> {
                return (this + 100f) / 100f * 100f
            }
            this < 100_000f -> {
                return (this + 1_000f) / 1_000f * 1_000f
            }
            this < 1_000_000f -> {
                return (this + 10_000f) / 10_000f * 10_000f
            }
            this < 10_000_000f -> {
                return (this + 100_000f) / 100_000f * 100_000f
            }
            this < 100_000_000f -> {
                return (this + 1_000_000f) / 1_000_000f * 1_000_000f
            }
            this < 1_000_000_000f -> {
                return (this + 10_000_000f) / 10_000_000f * 10_000_000f
            }
            this < 10_000_000_000f -> {
                return (this + 100_000_000f) / 100_000_000f * 100_000_000f
            }
            this < 100_000_000_000f -> {
                return (this + 1_000_000_000f) / 1_000_000_000f * 1_000_000_000f
            }
            this < 1_000_000_000_000f -> {
                return (this + 10_000_000_000f) / 10_000_000_000f * 10_000_000_000f
            }
            this < 10_000_000_000_000f -> {
                return (this + 100_000_000_000f) / 100_000_000_000f * 100_000_000_000f
            }
            this < 100_000_000_000_000f -> {
                return (this + 1_000_000_000_000f) / 1_000_000_000_000f * 1_000_000_000_000f
            }
            this < 1_000_000_000_000_000f -> {
                return (this + 10_000_000_000_000f) / 10_000_000_000_000f * 10_000_000_000_000f
            }
            this < 10_000_000_000_000_000f -> {
                return (this + 100_000_000_000_000f) / 100_000_000_000_000f * 100_000_000_000_000f
            }
        }
    }
    return this
}