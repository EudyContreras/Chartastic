package com.eudycontreras.chartasticlibrary.charts.interfaces

import android.view.MotionEvent
import com.eudycontreras.chartasticlibrary.ShapeRenderer

/**
 * Created by eudycontreras.
 */
interface TouchableElement {
    fun onTouch(event: MotionEvent, x: Float, y: Float, shapeRenderer: ShapeRenderer)
    fun onLongPressed(event: MotionEvent, x: Float, y: Float)
}