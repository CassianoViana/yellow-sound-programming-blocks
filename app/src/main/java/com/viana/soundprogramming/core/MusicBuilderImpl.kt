package com.viana.soundprogramming.core

import android.content.Context
import android.media.AudioManager
import android.util.Log
import com.viana.soundprogramming.appInstance
import com.viana.soundprogramming.blocks.*
import com.viana.soundprogramming.board.Board
import com.viana.soundprogramming.exceptions.SoundProgrammingError
import java.util.*

class MusicBuilderImpl : MusicBuilder {

    override fun isWiredHeadsetOn(): Boolean =
            (appInstance.getSystemService(Context.AUDIO_SERVICE) as AudioManager)
                    .isWiredHeadsetOn

    override lateinit var board: Board
    override lateinit var music: Music

    override var minY: Int = 0
    override var maxY: Int = 0

    override var maxVolume: Float = 1f
    override var maxSoundBlockDiameter: Float = 1f

    override var minSoundBlockDiameter: Float = 1f
    private var blocks: MutableList<Block> = mutableListOf()

    override fun build(blocks: List<Block>, board: Board, onMusicReadyListener: MusicBuilder.OnMusicReadyListener) {
        MusicBuilder.currentMusicId = UUID.randomUUID()
        Log.i("MusicBuilder", "build music")
        try {
            this.board = board
            music = MusicSoundPool(this)
            this.blocks = blocks.toMutableList()
            defineMusicBeginEnd()
            calculateSpeed()
            calculateGlobalVolume()
            normalizeDiameters()
            computeIfBlocks()
            computeModuleBlocks(blocks)
            repeatRepeatableBlocks(blocks)
            buildSounds()
            (board as BlocksManager.Listener).updateBlocksList(this.blocks)
            music.prepare()
            onMusicReadyListener.ready(music)
        } catch (e: SoundProgrammingError) {
            music.sounds = listOf()
            e.printStackTrace()
            onMusicReadyListener.error(e)
        }
    }

    private fun repeatRepeatableBlocks(blocks: List<Block>) {
        val repeatableBlocks = blocks.filterIsInstance(ControllableBlock::class.java).filter { it.playOnEachXLoops == 1.toByte() }
        val loopParamBlocks = blocks.filterIsInstance(LoopParamBlock::class.java)
        //val loopBlocks = blocks.filterIsInstance(LoopBlock::class.java)
        this.blocks.addAll(loopParamBlocks.flatMap {
            val repeatedBlocks = it.repeatBlocks(repeatableBlocks.filter { it.playOnEachXLoops == (1).toByte() })
            //it.moveFollowingToRight(blocks, it, repeatedBlocks)
            repeatedBlocks
        })
    }

    private fun computeModuleBlocks(blocks: List<Block>) {
        val repeatableBlocks = blocks.filterIsInstance(ControllableBlock::class.java)
        val moduleBlocks = blocks.filterIsInstance(ModuleBlock::class.java)
        val loopParamBlocks = blocks.filterIsInstance(LoopParamBlock::class.java)
        moduleBlocks.forEach { it.affect(loopParamBlocks, repeatableBlocks) }
    }

    private fun computeIfBlocks() {
        //val ifBlocks = blocks.filterIsInstance(IfBlock::class.java)
        val ifTargetBlocks = blocks.filterIsInstance(ControllableBlock::class.java)
        val ifParamBlocks = blocks.filterIsInstance(IfParamBlock::class.java)
        val presenceBlocks = blocks.filterIsInstance(PresenceBlock::class.java)
        ifParamBlocks.forEach {
            it.computeIfBlocks(ifTargetBlocks, presenceBlocks)
        }
    }

    private fun calculateSpeed() {
        blocks.filterIsInstance(SpeedBlock::class.java)
                .firstOrNull()?.calculateSpeed(board.timeline)
    }

    private fun calculateGlobalVolume() {
        blocks.filterIsInstance(VolumeBlock::class.java)
                .firstOrNull()?.calculateVolume(this)
    }

    private fun normalizeDiameters() {
        val controllableBlocks = blocks.filter { it is ControllableBlock || it is LoopParamBlock }
        val averageDiameter = controllableBlocks.map { it.diameter }.average()
        controllableBlocks.forEach { it.diameter = averageDiameter.toFloat() }
    }

    private fun defineMusicBeginEnd() {
        val cornerBlocks = blocks.filterIsInstance(CornerBlock::class.java)
        board.timeline.begin = cornerBlocks
                .filter { it.positions.contains(CornerBlock.Corner.LEFT) }
                .map { it.centerX + it.diameter / 1.5 }.average().toFloat()
        board.timeline.end = cornerBlocks
                .filter { it.positions.contains(CornerBlock.Corner.RIGHT) }
                .map { it.centerX - it.diameter / 1.5 }.average().toFloat()

        val minY = cornerBlocks
                .filter { it.positions.contains(CornerBlock.Corner.TOP) }
                .map { it.centerY }.average().toInt()

        val maxY = cornerBlocks
                .filter { it.positions.contains(CornerBlock.Corner.BOTTOM) }
                .map { it.centerY - it.diameter * 3 }.average().toInt()

        if (minY > 0)
            this.minY = minY

        if (maxY > 0)
            this.maxY = maxY
    }

    private fun buildSounds() {
        val soundBlocks = blocks
                .filterIsInstance(SoundBlock::class.java)
                .sortedBy { it.centerX }
        this.maxSoundBlockDiameter = soundBlocks.map { it.diameter }.max() ?: 0f
        if (soundBlocks.size == 1) {
            minSoundBlockDiameter = 0f
        } else {
            this.minSoundBlockDiameter = soundBlocks.map { it.diameter }.min() ?: 0f
        }
        music.sounds = soundBlocks.map { music.soundBuilder.build(it) }
    }

}