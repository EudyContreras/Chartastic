package com.eudycontreras.chartasticlibrary.charts

import android.view.animation.Interpolator

/**
 * Created by eudycontreras.
 */
interface ChartAnimation<T> {

    var interpolator: Interpolator
    var duration: Long
    var delay: Long

    var onEnd: (() -> Unit)?
    var onStart: (() -> Unit)?

    interface Animateable {
        fun onPreAnimation()
        fun onPostAnimation()
        fun onAnimate(delta: Float)
    }

    fun animate(view: ChartView, animateable: T)
}