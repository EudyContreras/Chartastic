package com.eudycontreras.chartasticlibrary.utilities.global

import com.eudycontreras.chartasticlibrary.listeners.ObservableProperty
import com.eudycontreras.chartasticlibrary.listeners.ObservableValue
import com.eudycontreras.chartasticlibrary.listeners.PropertyChangeObservable
import kotlin.reflect.KProperty

/**
 * Created by eudycontreras.
 */

enum class Notation {
    SHORT,
    LONG,
    CURRENCY
}

fun <T> from(value: T): ObservableValue<T>{
    return ObservableValue(value)
}

fun <T> T.toObservable(property: KProperty<Any>, observable: PropertyChangeObservable): ObservableProperty<T>{
    return ObservableProperty(this, property.name, observable)
}

fun <T> from(value: T, property: KProperty<Any>, observable: PropertyChangeObservable): ObservableProperty<T>{
    return ObservableProperty(value, property.name, observable)
}

fun mapRange(value: Long, fromMin: Long, fromMax: Long, toMin: Long, toMax: Long): Long {
    return (value - fromMin) * (toMax - toMin) / (fromMax - fromMin) + toMin
}

fun mapRange(value: Float, fromMin: Float, fromMax: Float, toMin: Float, toMax: Float): Float {
    return (value - fromMin) * (toMax - toMin) / (fromMax - fromMin) + toMin
}

fun mapRange(value: Double, fromMin: Double, fromMax: Double, toMin: Double, toMax: Double): Double {
    return (value - fromMin) * (toMax - toMin) / (fromMax - fromMin) + toMin
}

fun mapRange(value: Int, fromMin: Int, fromMax: Int, toMin: Int, toMax: Int): Int {
    return (value - fromMin) * (toMax - toMin) / (fromMax - fromMin) + toMin
}