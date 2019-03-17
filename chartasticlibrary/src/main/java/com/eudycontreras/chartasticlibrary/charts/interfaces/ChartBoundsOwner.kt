package com.eudycontreras.chartasticlibrary.charts.interfaces

import com.eudycontreras.chartasticlibrary.charts.ChartLayoutManager
import com.eudycontreras.chartasticlibrary.properties.Bounds

interface ChartBoundsOwner {

        var computeBounds: Boolean
        var drawBounds: Boolean
        val anchor: ChartLayoutManager.BoundsAnchor
        val bounds: Bounds

        fun notifyBoundsChange(bounds: Bounds)
        fun propagateNewBounds(bounds: Bounds)
    }