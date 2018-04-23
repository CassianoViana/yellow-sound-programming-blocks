package com.viana.soundprogramming.blocks

import topcodes.TopCode

class BlocksManager : TopCodesReader.Listener {

    private val listeners = mutableListOf<Listener>()
    private val blocksLibrary = BlocksLibrary()
    var blocks: List<Block> = listOf()

    override fun topCodesChanged(topCodes: List<TopCode>) {
        updateBlocksList(topCodes.mapNotNull {
            blocksLibrary.getTopCodeBlock(it)
        })
    }

    private fun updateBlocksList(blocks: List<Block>) {
        this.blocks = blocks
        listeners.forEach {
            it.updateBlocksList(blocks)
        }
    }

    fun addListener(blocksManagerListener: Listener): BlocksManager {
        listeners.add(blocksManagerListener)
        return this
    }

    interface Listener {
        fun updateBlocksList(blocks: List<Block>)
    }
}
