package com.eudycontreras.chartasticlibrary.listeners

import com.eudycontreras.chartasticlibrary.utilities.global.ValueChangeListener

/**
 * Created by eudycontreras.
 */

data class ObservableValue<T>(private val _value: T) {

    var value = _value
        set(value) {
            if(value != field) {
                val old = value
                field = value
                changeListeners.forEach { it.invoke(old, field) }
            }
        }

    var changeListeners = ArrayList<ValueChangeListener<T>>()

    fun set(value: T) {
        this.value = value
    }

    fun get(): T = value

    fun addChangeListener(changeListener: ValueChangeListener<T>) {
        changeListeners.add(changeListener)
    }

    fun removeChangeListener(changeListener: ValueChangeListener<T>) {
        changeListeners.remove(changeListener)
    }
}