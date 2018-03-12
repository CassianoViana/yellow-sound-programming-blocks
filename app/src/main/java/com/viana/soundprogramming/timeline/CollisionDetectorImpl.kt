package com.viana.soundprogramming.timeline

import com.viana.soundprogramming.blocks.Block

class CollisionDetectorImpl : CollisionDetector {

    override fun detectCollision(timeline: Timeline, blocks: List<Block>) {
        blocks.forEach {
            if (timeline.intersects(it))
                it.execute()
        }
    }
}
