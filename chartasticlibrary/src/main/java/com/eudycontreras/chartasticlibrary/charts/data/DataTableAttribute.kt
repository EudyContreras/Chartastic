package com.eudycontreras.chartasticlibrary.charts.data

/**
 * Created by eudycontreras.
 */

data class DataTableAttribute(var name: String, var type: Any) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DataTableAttribute) return false
        if (name != other.name) return false
        if (type != other.type) return false
        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }
}