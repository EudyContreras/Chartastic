package com.eudycontreras.chartasticlibrary.utilities.extensions

/**
 * Created by eudycontreras.
 */
fun Any.asString() = this.toString()

fun Any.asBoolean() = this.toString().toBoolean()

fun Any.asInt() = this.toString().toInt()

fun Any.asDouble() = this.toString().toDouble()

fun Any.asFloat() = this.toString().toFloat()

inline fun <reified T: Any> Any.cast(): T{
    return this as T
}