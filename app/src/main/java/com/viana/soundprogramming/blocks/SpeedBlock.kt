package com.viana.soundprogramming.blocks

import com.viana.soundprogramming.timeline.Timeline

class SpeedBlock : Block() {

    fun calculateSpeed(timeline: Timeline){
        timeline.speedFactor = Math.abs(this.degree / 360) * 3
    }

}
