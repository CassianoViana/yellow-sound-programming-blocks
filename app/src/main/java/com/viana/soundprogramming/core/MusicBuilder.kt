package com.viana.soundprogramming.core

import com.viana.soundprogramming.blocks.Block

interface MusicBuilder {

    fun build(blocks: List<Block>): Music

}