package com.eudycontreras.chartasticlibrary.charts.chart_data

/**
 * Created by eudycontreras.
 */

@FunctionalInterface
interface DataTableParser<T> {
    fun create(from: T): DataTable?
}