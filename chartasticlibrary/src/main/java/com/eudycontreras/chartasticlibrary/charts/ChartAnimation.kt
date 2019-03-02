package com.eudycontreras.chartasticlibrary.charts

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.view.animation.Interpolator
import androidx.core.math.MathUtils.clamp
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.eudycontreras.chartasticlibrary.extensions.asFloat
import com.eudycontreras.chartasticlibrary.global.mapRange

/**
 * Created by eudycontreras.
 */
class ChartAnimation {

    var interpolator: Interpolator = FastOutSlowInInterpolator()
    var sequential: Boolean = false
    var stagger: Long = 0
    var duration: Long = 0
    var delay: Long = 0

    var onEnd: (() -> Unit)? = null
    var onStart: (() -> Unit)? = null

    var type: AnimationType = AnimationType.LEFT_TO_RIGHT

    enum class AnimationType {
        LEFT_TO_RIGHT,
        RIGHT_TO_LEFT,
        EDGE_TO_CENTER,
        CENTER_TO_EDGE,
    }

    interface Animateable {
        fun onPreAnimation()
        fun onPostAnimation()
        fun onAnimate(delta: Float)
    }

    fun animate(view: ChartView, animateable: List<Animateable>) {
        if (sequential) {
            performSequentialAnimation(view, animateable)
        } else {
            performAnimation(view, animateable)
        }
    }

    private fun performAnimation(view: ChartView, animateable: List<ChartAnimation.Animateable>) {
        val valueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f)

        valueAnimator.startDelay = delay
        valueAnimator.duration = duration
        valueAnimator.interpolator = interpolator
        valueAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                super.onAnimationStart(animation)
                animateable.forEach { it.onPreAnimation() }
                onStart?.invoke()
            }

            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                onEnd?.invoke()
            }
        })
        valueAnimator.addUpdateListener {
            val animate = it.animatedValue.asFloat()
            for(item in animateable) {
                item.onAnimate(animate)
            }
            view.updateView()
        }
        valueAnimator.start()
    }

    private fun performSequentialAnimation(view: ChartView, animateable: List<ChartAnimation.Animateable>) {
        val valueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f)

        val startPoints = ArrayList<Pair<Float, Float>>()

        val values = ArrayList<Pair<Float, Float>>()

        var start = 0L
        var end = start + duration
        var totalDuration = duration

        startPoints.add(start.toFloat() to end.toFloat())

        for (i in 0 until animateable.size) {
            values.add(0.0f to 1.0f)
            start += stagger
            end = start + duration
            totalDuration += stagger
            startPoints.add(start.toFloat() to end.toFloat())
        }

        valueAnimator.startDelay = delay
        valueAnimator.duration = totalDuration
        valueAnimator.interpolator = interpolator
        valueAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                super.onAnimationStart(animation)
                animateable.forEach { it.onPreAnimation() }
                onStart?.invoke()
            }

            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                onEnd?.invoke()
            }
        })

        valueAnimator.addUpdateListener {
            for ((index, value) in values.withIndex()) {
                val currentTime = it.currentPlayTime.toFloat()
                if (currentTime >= startPoints[index].first) {
                    val animate = clamp(
                        mapRange(
                            currentTime,
                            startPoints[index].first,
                            startPoints[index].second,
                            value.first,
                            value.second
                        ),
                        value.first,
                        value.second
                    )
                    animateable[index].onAnimate(it.interpolator.getInterpolation(animate))
                }
            }
            view.updateView()
        }
        valueAnimator.start()
    }
}