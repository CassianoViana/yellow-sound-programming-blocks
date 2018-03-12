package com.viana.soundprogramming.timeline

import com.viana.soundprogramming.blocks.Block

interface CollisionDetector {

    companion object {
        val instance = CollisionDetectorImpl()
    }

    fun detectCollision(timeline: Timeline, blocks: List<Block>)

}