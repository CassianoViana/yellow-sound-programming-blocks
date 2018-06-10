package com.viana.soundprogramming.blocks

import com.viana.soundprogramming.timeline.Timeline

class SpeedBlock : Block(), NotMovableBlock {

    fun calculateSpeed(timeline: Timeline) {
        var degree = this.degree
        degree += 180
        if (degree > 360)
            degree -= 360
        val notStopped = timeline.speedFactor != 0f
        if (notStopped) {
            val speedFactor = Math.abs(degree) / 90
            var fl = 4f - speedFactor
            if (fl < 0.25) fl = 0.25f
            timeline.speedFactor = fl
        }
    }
}
