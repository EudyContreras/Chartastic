package com.eudycontreras.chartasticlibrary.utilities.global

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent


typealias AndroidColor = Color

typealias ShapeStyle = (Canvas?, Paint, Path, x:Float, y:Float, width:Float, height:Float) -> Unit

typealias InterceptHandler = (MotionEvent, Float, Float) -> Unit

typealias HighlightCriteria = (item:Any) -> Boolean