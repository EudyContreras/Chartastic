package com.eudycontreras.chartasticlibrary.utilities.extensions

import com.eudycontreras.chartasticlibrary.charts.chart_data.DataType

/**
 * Created by eudycontreras.
 */
fun Any.asString() = this.toString()

fun Any.asBoolean() = (Boolean::class.java).cast(this)

fun Any.asInt() = this.toString().toInt()

fun Any.asDouble() = this.toString().toDouble()

fun Any.asFloat() = this.toString().toFloat()

fun Any.isNumeric(dataType: DataType): Boolean {
    return when (dataType) {
        DataType.ALPHABETIC -> false
        DataType.NUMERIC_WHOLE, DataType.NUMERIC_DECIMAL -> true
        DataType.BINARY -> false
    }
}

fun Any.isPositive(dataType: DataType): Boolean {
    if (dataType == DataType.NUMERIC_WHOLE) {
        return when {
            this is Int -> this > 0
            this is Long -> this > 0
            else -> {
                false
            }
        }
    }
    else {
        return when {
            this is Double -> this > 0
            this is Float -> this > 0
            else -> {
                false
            }
        }
    }
}

fun Any.isZero(dataType: DataType): Boolean {
    if (dataType == DataType.NUMERIC_WHOLE) {
        return when {
            this is Int -> this == 0
            this is Long -> this == 0
            else -> {
                false
            }
        }
    }
    else {
        return when {
            this is Double -> this == 0
            this is Float -> this == 0
            else -> {
                false
            }
        }
    }
}

fun <T> readValue(any: Any?, java: Class<T>): T? {
    return java.cast(any)
}

inline fun <reified T> Any.cast(): T{
    return this as T
}

inline fun <reified T> Any.restored(): T {
    return readValue(this, T::class.java)!!
}


