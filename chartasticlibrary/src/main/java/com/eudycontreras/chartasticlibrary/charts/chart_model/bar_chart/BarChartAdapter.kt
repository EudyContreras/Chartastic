package com.eudycontreras.chartasticlibrary.charts.chart_model.bar_chart

/**
 * Created by eudycontreras.
 */

@FunctionalInterface
interface BarChartAdapter<ARGS, RESULT> {
    fun convert(arg: ARGS): RESULT
}