package com.eudycontreras.chartasticlibrary.listeners

/**
 * Created by eudycontreras.
 */

data class ObservableProperty<T>(private val _value: T, val name: String, private val changeObservable: PropertyChangeObservable) {

    var value: T = _value
        set(value) {
           if (value != field) {
               val old = field
               field = value
               if (changeObservable.processChanges) {
                   changeObservable.onPropertyChange(old!!, value!!, name)
               }
           }
        }

    fun set(value: T) {
        this.value = value
    }

    fun get(): T = value
}