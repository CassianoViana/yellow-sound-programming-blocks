package com.viana.soundprogramming.blocks

import com.viana.soundprogramming.camera.TopCodesReaderListener
import topcodes.TopCode

class BlocksManager : TopCodesReaderListener {

    private val blocks = mutableListOf<Block>()
    private val listeners = mutableListOf<BlocksManagerListener>()
    private val blocksLibrary = BlocksLibrary()

    override fun topCodesChanged(topCodes: List<TopCode>) {
        filterTopCodes(topCodes)
        updateBlocksList()
    }

    private fun filterTopCodes(topCodes: List<TopCode>) {
        blocks.clear()
        topCodes.forEach {
            val block = blocksLibrary.get(it.code)
            if (block != null) {
                block.topCode = it
                blocks.add(block)
            }
        }
    }

    private fun updateBlocksList() {
        listeners.forEach {
            it.updateBlocksList(blocks)
        }
    }

    fun addListener(blocksManagerListener: BlocksManagerListener) {
        listeners.add(blocksManagerListener)
    }
}
