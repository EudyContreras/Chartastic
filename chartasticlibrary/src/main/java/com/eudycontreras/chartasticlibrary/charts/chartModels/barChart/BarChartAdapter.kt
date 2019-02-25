package com.eudycontreras.chartasticlibrary.charts.chartModels.barChart

/**
 * Created by eudycontreras.
 */

@FunctionalInterface
interface BarChartAdapter<ARGS, RESULT> {
    fun convert(arg: ARGS): RESULT
}