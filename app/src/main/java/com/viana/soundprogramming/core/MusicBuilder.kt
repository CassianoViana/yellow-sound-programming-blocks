package com.viana.soundprogramming.core

import com.viana.soundprogramming.blocks.Block
import com.viana.soundprogramming.board.Board
import com.viana.soundprogramming.exceptions.SoundSyntaxError

interface MusicBuilder {

    var maxVolume: Float
    var maxSoundBlockDiameter: Float
    var minSoundBlockDiameter: Float
    var board: Board

    fun isWiredHeadsetOn(): Boolean
    fun build(blocks: List<Block>, board: Board, onMusicReadyListener: OnMusicReadyListener)

    interface OnMusicReadyListener {
        fun ready(music: Music)
        fun error(e: SoundSyntaxError)
    }

}