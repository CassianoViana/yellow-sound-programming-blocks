package com.viana.soundprogramming.core

import com.viana.soundprogramming.blocks.Block
import com.viana.soundprogramming.board.Board

interface MusicBuilder {

    fun build(blocks: List<Block>, board: Board, onMusicReadyListener: OnMusicReadyListener)
    var maxVolume: Float
    var maxSoundBlockDiameter: Float
    var minSoundBlockDiameter: Float

    interface OnMusicReadyListener {
        fun ready(music: Music)
    }

    fun isWiredHeadsetOn(): Boolean

}