package com.viana.soundprogramming.blocks

import topcodes.TopCode

class BlocksManager : TopCodesReader.Listener {

    val blocks = mutableListOf<Block>()
    private val listeners = mutableListOf<Listener>()
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
        val copiedBlocks = blocks.toList()
        listeners.forEach {
            it.updateBlocksList(copiedBlocks)
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
