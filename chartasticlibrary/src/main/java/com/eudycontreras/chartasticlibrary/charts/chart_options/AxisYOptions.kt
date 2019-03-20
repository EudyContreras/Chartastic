package com.eudycontreras.chartasticlibrary.charts.chart_options

import android.graphics.Typeface
import com.eudycontreras.chartasticlibrary.charts.chart_grid.ChartGridAxisY
import com.eudycontreras.chartasticlibrary.charts.chart_model.bar_chart.BarChartData
import com.eudycontreras.chartasticlibrary.listeners.ObservableProperty
import com.eudycontreras.chartasticlibrary.listeners.PropertyChangeObservable
import com.eudycontreras.chartasticlibrary.properties.MutableColor
import com.eudycontreras.chartasticlibrary.properties.Padding
import com.eudycontreras.chartasticlibrary.utilities.extensions.dp
import com.eudycontreras.chartasticlibrary.utilities.global.from

/**
 * Created by eudycontreras.
 */

class AxisYOptions: PropertyChangeObservable() {

    var valuePointCount: Int = 0
        set(value) {
            field = value
            negativeValuePointCount = value
            positiveValuePointCount = value
        }

    var negativeValuePointCount: Int
        get() = _nValuePointCount.value
        set(value) {
            _nValuePointCount.set(value)
        }

    var positiveValuePointCount: Int
        get() = _pValuePointCount.value
        set(value) {
            _pValuePointCount.set(value)
        }

    var render: Boolean
        get() = _render.value
        set(value) {
            _render.set(value)
        }

    var padding: Padding
        get() = _padding.value
        set(value) {
            _padding.set(value)
        }

    var labelTextColor: MutableColor
        get() = _labelTextColor.value
        set(value) {
            _labelTextColor.set(value)
        }

    var labelTextSize: Float
        get() = _labelTextSize.value
        set(value) {
            _labelTextSize.set(value)
        }

    var labelTypeFace: Typeface
        get() = _labelTypeFace.value
        set(value) {
            _labelTypeFace.set(value)
        }

    var showLabels: Boolean
        get() = _showLabels.value
        set(value) {
            _showLabels.set(value)
        }

    var showTickLines: Boolean
        get() = _showTickLines.value
        set(value) {
            _showTickLines.set(value)
        }

    var showTickLineBar: Boolean
        get() = _showTickLineBar.value
        set(value) {
            _showTickLineBar.set(value)
        }

    var labelValuePrepend: String
        get() = _labelValuePrepend.value
        set(value) {
            _labelValuePrepend.set(value)
        }

    var labelValueAppend: String
        get() = _labelValueAppend.value
        set(value) {
            _labelValueAppend.set(value)
        }

    var tickWidth = 1.dp
        get() = if(showTickLines) field else 0f

    var tickLength = 8.dp
        get() = if(showTickLines) field else 0f

    var tickElevation = 0.dp

    var tickColor= MutableColor.rgb(255)

    var dataReady: Boolean = false

    var chartData: BarChartData? = null

    private val _pValuePointCount: ObservableProperty<Int> by lazy { from(10, ::positiveValuePointCount, this) }
    private val _nValuePointCount: ObservableProperty<Int> by lazy { from(10, ::negativeValuePointCount, this) }
    private val _render: ObservableProperty<Boolean> by lazy {  from(true, ::render, this) }
    private val _padding: ObservableProperty<Padding> by lazy {  from(ChartGridAxisY.DEFAULT_PADDING, ::padding, this) }
    private val _labelTextColor: ObservableProperty<MutableColor> by lazy {  from(ChartGridAxisY.DEFAULT_LABEL_COLOR, ::labelTextColor, this) }
    private val _labelTextSize: ObservableProperty<Float> by lazy {  from(ChartGridAxisY.DEFAULT_LABEL_TEXT_SIZE, ::labelTextSize, this) }
    private val _labelTypeFace: ObservableProperty<Typeface> by lazy {  from(ChartGridAxisY.DEFAULT_LABEL_TYPEFACE, ::labelTypeFace, this) }
    private val _showLabels: ObservableProperty<Boolean> by lazy {  from(true, ::showLabels, this) }
    private val _showTickLines: ObservableProperty<Boolean> by lazy {  from(false, ::showTickLines, this) }
    private val _showTickLineBar: ObservableProperty<Boolean> by lazy {  from(true, ::showTickLineBar, this) }
    private val _labelValuePrepend: ObservableProperty<String> by lazy {  from("", ::labelValuePrepend, this) }
    private val _labelValueAppend: ObservableProperty<String> by lazy {  from("", ::labelValueAppend, this) }
}