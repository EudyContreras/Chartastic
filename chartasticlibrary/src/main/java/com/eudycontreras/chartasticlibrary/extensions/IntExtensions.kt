package com.eudycontreras.chartasticlibrary.extensions

import android.content.res.Resources

/**
 * Created by eudycontreras.
 */
val Int.f: Float
    get() = this.toFloat()

/**
 * Use the float as density independent pixels and return the pixel value
 */
val Int.dp: Float
    get() = this * Resources.getSystem().displayMetrics.density

/**
 * Use the float as scale independent pixels and return the pixel value
 */
val Int.sp: Float
    get() = this * Resources.getSystem().displayMetrics.scaledDensity
