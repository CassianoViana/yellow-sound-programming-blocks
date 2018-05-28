package com.viana.soundprogramming.blocks

import com.viana.soundprogramming.core.MusicBuilder

class VolumeBlock : Block(), NotMovableBlock {
    fun calculateVolume(musicBuilder: MusicBuilder) {
        var degree = this.degree
        degree += 180
        if (degree > 360)
            degree -= 360
        val volume = Math.abs(degree) / 360
        musicBuilder.maxVolume = 1 - volume
    }

}
