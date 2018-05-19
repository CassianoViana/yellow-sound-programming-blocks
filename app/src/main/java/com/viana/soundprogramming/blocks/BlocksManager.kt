package com.viana.soundprogramming.blocks

import android.util.Log
import topcodes.TopCode

class BlocksManager : TopCodesReader.Listener {

    private val listeners = mutableListOf<Listener>()
    private val blocksLibrary = BlocksLibrary()
    private val blocksUpdateAnalyzer = BlocksChangesAnalyzerByBlocksPropsList()

    override fun topCodesChanged(topCodes: List<TopCode>) {
        updateBlocksList(topCodes.mapNotNull {
            blocksLibrary.getTopCodeBlock(it)
        })
    }

    private fun updateBlocksList(blocks: List<Block>) {
        if (blocksUpdateAnalyzer.blocksChanged(blocks)) {
            blocks.toMutableList()
                    .apply {
                        listeners.forEach {
                            it.updateBlocksList(this)
                        }
                    }
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

interface BlocksChangesAnalyzer {
    fun blocksChanged(blocks: List<Block>): Boolean
}

class BlocksChangesAnalyzerByCount : BlocksChangesAnalyzer {
    private var countBlocks = 0
    override fun blocksChanged(blocks: List<Block>): Boolean {
        val blocksWereUpdated = blocks.size != countBlocks
        if (blocksWereUpdated) {
            countBlocks = blocks.size
        }
        return blocksWereUpdated
    }
}

class BlocksChangesAnalyzerByPositionAndCount : BlocksChangesAnalyzer {

    var positions: String? = ""
    override fun blocksChanged(blocks: List<Block>): Boolean {
        val positions = blocks.joinToString { it.centerX.toString() + it.centerY.toString() }
        val blocksWereUpdated = positions != this.positions
        if (blocksWereUpdated) {
            Log.i("BlocksManager", "blocks updated")
            this.positions = positions
        }
        return blocksWereUpdated
    }

}

class BlocksChangesAnalyzerByBlocksPropsList : BlocksChangesAnalyzer {

    class BlocksProps(private val x: Int, private val y: Int, private val degree: Int) {
        private val inconsiderableMovPxs = 40
        private val inconsiderableMovDegrees = 10
        override fun equals(other: Any?): Boolean {
            val positionToCompare = other as BlocksProps
            if (Math.abs(this.x - positionToCompare.x) > inconsiderableMovPxs)
                return false
            if (Math.abs(this.y - positionToCompare.y) > inconsiderableMovPxs)
                return false
            if (Math.abs(this.degree - positionToCompare.degree) > inconsiderableMovDegrees)
                return false
            return true
        }

        override fun hashCode(): Int {
            var result = x
            result = 31 * result + y
            result = 31 * result + degree
            return result
        }
    }

    private var blocksProps: List<BlocksProps> = listOf()
    override fun blocksChanged(blocks: List<Block>): Boolean {
        val blocksProps = blocks.map { BlocksProps(it.centerX, it.centerY, it.degree.toInt()) }
        val blocksWereUpdated = blocksProps.size != this.blocksProps.size || !this.blocksProps.containsAll(blocksProps)
        if (blocksWereUpdated) {
            Log.i("BlocksManager", "blocks updated")
            this.blocksProps = blocksProps
        }
        return blocksWereUpdated
    }

}