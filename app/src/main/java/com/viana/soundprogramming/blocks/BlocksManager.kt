package com.viana.soundprogramming.blocks

import android.util.Log
import topcodes.TopCode
import java.util.*

class BlocksManager : TopCodesReader.Listener {

    private val listeners = mutableListOf<Listener>()
    private val blocksLibrary = BlocksLibrary()
    private val blocksUpdateAnalyzer = BlocksChangesAnalyzerByBlocksPropsList()

    override fun topCodesChanged(topCodes: List<TopCode>) {
        val blocks = topCodes.mapNotNull { blocksLibrary.getTopCodeBlock(it) }
        updateBlocksList(removeOutsideBlocks(blocks))
    }

    private fun removeOutsideBlocks(blocks: List<Block>): List<Block> {
        val cornerBlocks = blocks.filterIsInstance(CornerBlock::class.java)
        if (cornerBlocks.isEmpty())
            return blocks

        var maxX = Integer.MAX_VALUE
        var minX = 0
        var maxY = Integer.MAX_VALUE
        var minY = 0

        cornerBlocks.maxBy { it.centerX }.let { maxX = it?.centerX ?: 0 }
        cornerBlocks.maxBy { it.centerY }.let { maxY = it?.centerY ?: 0 }
        cornerBlocks.minBy { it.centerX }.let { minX = it?.centerX ?: 0 }
        cornerBlocks.minBy { it.centerY }.let { minY = it?.centerY ?: 0 }

        return blocks.toMutableList()
                .apply {
                    removeAll { it.centerX > maxX || it.centerX < minX || it.centerY < minY - 100 || it.centerY > maxY }
                }.toList()
    }

    private fun updateBlocksList(blocks: List<Block>) {
        blocksUpdateAnalyzer.checkIfBlocksChanged(blocks, object : BlocksChangesAnalyzer.Listener {
            override fun changed(changed: Boolean, blocks: List<Block>) {
                if (changed) {
                    blocks.toMutableList()
                            .apply {
                                listeners.forEach {
                                    it.updateBlocksList(this)
                                }
                            }

                }
            }
        })
    }

    fun addListener(blocksManagerListener: Listener): BlocksManager {
        listeners.add(blocksManagerListener)
        return this
    }

    interface Listener {
        fun updateBlocksList(blocks: List<Block>)
    }

    fun updateBlockSoundSoundId(code: Int, soundId: Int) {
        (blocksLibrary.blocks[code] as SoundBlock).soundId = soundId
    }

}

interface BlocksChangesAnalyzer {
    fun checkIfBlocksChanged(blocks: List<Block>, listener: Listener)

    interface Listener {
        fun changed(changed: Boolean, blocks: List<Block>)
    }
}

class AlwaysTrueBlocksChangeAnalyzer : BlocksChangesAnalyzer {
    override fun checkIfBlocksChanged(blocks: List<Block>, listener: BlocksChangesAnalyzer.Listener) {
        listener.changed(true, blocks)
    }
}

class BlocksChangesAnalyzerByBlocksPropsList : BlocksChangesAnalyzer {

    class BlocksProps(private val x: Int, private val y: Int, private val degree: Int, private val code: Int) {
        private val inconsiderableMovPxs = 40
        private val inconsiderableMovDegrees = 20
        override fun equals(other: Any?): Boolean {
            val positionToCompare = other as BlocksProps
            if (Math.abs(this.x - positionToCompare.x) > inconsiderableMovPxs)
                return false
            if (Math.abs(this.y - positionToCompare.y) > inconsiderableMovPxs)
                return false
            if (Math.abs(this.degree - positionToCompare.degree) > inconsiderableMovDegrees)
                return false
            if (other.code != this.code)
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
    override fun checkIfBlocksChanged(blocks: List<Block>, listener: BlocksChangesAnalyzer.Listener) {
        val blocksProps = blocks.map { BlocksProps(it.centerX, it.centerY, it.degree.toInt(), it.code) }
        val blocksWereUpdated = blocksProps.size != this.blocksProps.size || !this.blocksProps.containsAll(blocksProps)
        if (blocksWereUpdated) {
            Log.i("BlocksManager", "blocks updated")
            this.blocksProps = blocksProps
        }
        listener.changed(blocksWereUpdated, blocks)
    }

}

class BlocksChangesAnalyzerByBlocksPropsListAndTime : BlocksChangesAnalyzer {

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

    abstract class BlocksUpdateTask(val blocksUpdateAnalysisRequest: BlocksUpdateAnalysisRequest) : TimerTask()
    class BlocksUpdateAnalysisRequest(val blocks: List<Block>) {
        val blocksProps = blocks.map { BlocksProps(it.centerX, it.centerY, it.degree.toInt()) }
    }

    private val analysisRequestBlocks: MutableList<BlocksUpdateAnalysisRequest> = mutableListOf()

    override fun checkIfBlocksChanged(blocks: List<Block>, listener: BlocksChangesAnalyzer.Listener) {
        val updateAnalysisRequest = BlocksUpdateAnalysisRequest(blocks)

        val firstRequest = analysisRequestBlocks.isEmpty()
        analysisRequestBlocks.add(updateAnalysisRequest)
        if (firstRequest)
            scheduleAnalysisRequest(updateAnalysisRequest, listener, 500)

        if (analysisRequestBlocks.size > 5) {
            analysisRequestBlocks.removeAt(analysisRequestBlocks.size - 1)
        }
    }

    private fun scheduleAnalysisRequest(blocksUpdateAnalysisRequest: BlocksUpdateAnalysisRequest, listener: BlocksChangesAnalyzer.Listener, timeInMillis: Long) {
        Log.i("BlocksManager", "scheduleAnalysisRequest")
        Timer().schedule(object : BlocksUpdateTask(blocksUpdateAnalysisRequest) {
            override fun run() {
                Log.i("BlocksManager", "run()")
                val blocksProps = this.blocksUpdateAnalysisRequest.blocksProps
                val hasNotOtherScreenUpdatesWithSameBlocks = analysisRequestBlocks.none { hasSameBlocks(it, blocksProps) }
                if (hasNotOtherScreenUpdatesWithSameBlocks) {
                    listener.changed(true, this.blocksUpdateAnalysisRequest.blocks)
                }
                analysisRequestBlocks.remove(blocksUpdateAnalysisRequest)
            }

            private fun hasSameBlocks(it: BlocksUpdateAnalysisRequest, updateAnalysisRequestBlocksProps: List<BlocksProps>): Boolean {
                return (it != this.blocksUpdateAnalysisRequest
                        && it.blocksProps.size == blocksUpdateAnalysisRequest.blocksProps.size
                        && updateAnalysisRequestBlocksProps.containsAll(it.blocksProps))
            }
        }, timeInMillis)
    }
}

class BlocksChangesAnalyzerByBlocksPropsListModa : BlocksChangesAnalyzer {

    class BlocksProps(private val x: Int, private val y: Int, private val degree: Int, private val code: Int) {
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
            if (other.code != this.code)
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

    class BlocksUpdateAnalysisRequest(val blocks: List<Block>) {
        private val blocksProps = blocks.map { BlocksProps(it.centerX, it.centerY, it.degree.toInt(), it.code) }

        override fun equals(other: Any?): Boolean {
            other as BlocksUpdateAnalysisRequest
            return this.blocksProps.size == other.blocksProps.size
                    && this.blocksProps.containsAll(other.blocksProps)
        }
    }

    private val blocksUpdatesFrequency = HashMap<BlocksUpdateAnalysisRequest, Int>()

    var count = 0L
    override fun checkIfBlocksChanged(blocks: List<Block>, listener: BlocksChangesAnalyzer.Listener) {
        val blocksUpdateAnalysisRequest = BlocksUpdateAnalysisRequest(blocks)
        val frequency: Int? = blocksUpdatesFrequency[blocksUpdateAnalysisRequest]
        blocksUpdatesFrequency[blocksUpdateAnalysisRequest] = frequency ?: 0 + 1
    }
}