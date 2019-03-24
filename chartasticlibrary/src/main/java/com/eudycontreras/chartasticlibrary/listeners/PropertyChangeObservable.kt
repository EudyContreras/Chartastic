package com.eudycontreras.chartasticlibrary.listeners

import com.eudycontreras.chartasticlibrary.charts.chart_options.ChartOptions
import com.eudycontreras.chartasticlibrary.utilities.global.PropertyChangeListener

/**
 * Created by eudycontreras.
 */

abstract class PropertyChangeObservable: ChartOptions {
    protected var listeners = ArrayList<PropertyChangeListener<Any>>()

    internal var processChanges = false

    fun addPropertyChangeListener(changeListener: PropertyChangeListener<Any>){
        listeners.add(changeListener)
    }

    fun removePropertyChangeListener(changeListener: PropertyChangeListener<Any>){
        listeners.add(changeListener)
    }

    fun onPropertyChange(oldValue: Any, newValue: Any, name: String) {
        listeners.forEach { it.invoke(oldValue, newValue, name) }
    }
}