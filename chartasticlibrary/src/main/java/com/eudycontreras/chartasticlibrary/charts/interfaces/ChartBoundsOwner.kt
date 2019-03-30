package com.eudycontreras.chartasticlibrary.charts.interfaces

import com.eudycontreras.chartasticlibrary.charts.ChartLayoutManager
import com.eudycontreras.chartasticlibrary.properties.Bounds
import com.eudycontreras.chartasticlibrary.utilities.global.BoundsChangeListener

interface ChartBoundsOwner {
        val changeListeners: ArrayList<BoundsChangeListener>

        var computeBounds: Boolean
        val anchor: ChartLayoutManager.BoundsAnchor
        val bounds: Bounds

        fun notifyBoundsChange(bounds: Bounds)
        fun propagateNewBounds(bounds: Bounds)
    }