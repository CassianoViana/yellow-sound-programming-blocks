package com.viana.soundprogramming.core

import com.viana.soundprogramming.blocks.Block
import com.viana.soundprogramming.board.Board

interface MusicBuilder {

    fun build(blocks: List<Block>, board: Board): Music
    var maxVolume: Float

}