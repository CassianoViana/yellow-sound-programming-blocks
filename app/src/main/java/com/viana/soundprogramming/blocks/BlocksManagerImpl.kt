package com.viana.soundprogramming.blocks

import com.viana.soundprogramming.camera.TopCodesChangedListener
import topcodes.TopCode

class BlocksManagerImpl : TopCodesChangedListener {

    val blocks: MutableList<Block> = mutableListOf()

    private val blocksLibrary: BlocksLibrary = BlocksLibrary()

    override fun topCodesChanged(topCodes: List<TopCode>) {
        synchronized(blocks) {
            blocks.clear()
            topCodes.forEach {
                val block = blocksLibrary.get(it.code)
                if (block != null) {
                    block.topCode = it
                    blocks.add(block)
                }
            }
        }
    }
}
