package com.eudycontreras.chartasticlibrary.utilities.global

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent
import com.eudycontreras.chartasticlibrary.charts.chart_data.DataTableGroup
import com.eudycontreras.chartasticlibrary.charts.chart_data.DataTableValue


typealias AndroidColor = Color

typealias ShapeStyle = (Canvas?, Paint, Path, x:Float, y:Float, width:Float, height:Float) -> Unit

typealias InterceptHandler = (MotionEvent, Float, Float) -> Unit

typealias HighlightCriteria = (item:Any) -> Boolean

typealias MatrixProperties = Array<Triple<String, Any, Any>>

typealias DataTableMatrix = Array<Array<out Any>>

typealias DataTableGrouper = Pair<String?, DataTableGroup.GroupPointer>

typealias DataChangeListener = (DataTableValue) -> Unit

typealias ValueChangeListener <T> = (old: T, new: T) -> Unit

typealias PropertyChangeListener <T> = (oldValue: T, newValue: T, name: String) -> Unit