package com.eudycontreras.chartasticlibrary.charts.data

/**
 * Created by eudycontreras.
 */

@FunctionalInterface
interface DataTableParser<T> {
    fun create(from: T): DataTable?
}