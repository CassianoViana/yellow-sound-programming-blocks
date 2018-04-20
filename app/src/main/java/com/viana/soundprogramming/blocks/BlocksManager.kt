package com.viana.soundprogramming.blocks

import topcodes.TopCode

class BlocksManager : TopCodesReader.Listener {

    val blocks = mutableListOf<Block>()
    private val listeners = mutableListOf<Listener>()
    private val blocksLibrary = BlocksLibrary()

    override fun topCodesChanged(topCodes: List<TopCode>) {
        filterTopCodes(topCodes)
        updateBlocksList()
        checkEnterBlock()
    }

    private fun checkEnterBlock() {
        val beginBlocks: List<Block> = blocks.filter { it.javaClass == BeginBlock::javaClass }
        if (!beginBlocks.isEmpty())
            listeners.forEach { it.beginBlockEntered(beginBlocks.first() as BeginBlock) }
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

    fun addListener(blocksManagerListener: Listener): BlocksManager {
        listeners.add(blocksManagerListener)
        return this
    }

    interface Listener {
        fun updateBlocksList(blocks: List<Block>)
        fun beginBlockEntered(block: BeginBlock){}
        fun endBlockEntered(block: EndBlock){}
    }
}
