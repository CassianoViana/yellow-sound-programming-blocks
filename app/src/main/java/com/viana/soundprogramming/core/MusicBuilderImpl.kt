package com.viana.soundprogramming.core

import android.content.Context
import android.media.AudioManager
import android.util.Log
import com.viana.soundprogramming.appInstance
import com.viana.soundprogramming.blocks.*
import com.viana.soundprogramming.board.Board
import com.viana.soundprogramming.exceptions.BaseOutOfCornersError
import com.viana.soundprogramming.exceptions.SoundProgrammingError
import java.util.*

class MusicBuilderImpl : MusicBuilder {

    override fun isWiredHeadsetOn(): Boolean =
            (appInstance.getSystemService(Context.AUDIO_SERVICE) as AudioManager)
                    .isWiredHeadsetOn

    override lateinit var board: Board
    override lateinit var music: Music

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
            checkIfIsInsideCorners()
            calculateSpeed()
            calculateGlobalVolume()
            removeFalseTestBlocks()
            computeModuleBlocks(blocks)
            repeatRepeatableBlocks(blocks)
            buildSounds()
            defineMusicBeginEnd()
            (board as BlocksManager.Listener).updateBlocksList(this.blocks)
            music.prepare()
            onMusicReadyListener.ready(music)
            Log.i("Sounds", music.sounds.size.toString())
        } catch (e: SoundProgrammingError) {
            music.sounds = listOf()
            e.printStackTrace()
            onMusicReadyListener.error(e)
        }
    }

    private fun checkIfIsInsideCorners() {
        if (blocks.filterIsInstance(CornerBlock::class.java).size < 2)
            throw BaseOutOfCornersError()

    }

    private fun repeatRepeatableBlocks(blocks: List<Block>) {
        val repeatableBlocks = blocks.filterIsInstance(ControllableBlock::class.java)
        val loopParamBlocks = blocks.filterIsInstance(LoopParamBlock::class.java)
        val loopBlocks = blocks.filterIsInstance(LoopBlock::class.java)
        this.blocks.addAll(loopBlocks.flatMap {
            val repeatedBlocks = it.repeatBlocks(repeatableBlocks, loopParamBlocks)
            it.moveFollowingToRight(blocks, it, repeatedBlocks)
            repeatedBlocks
        })
    }

    private fun computeModuleBlocks(blocks: List<Block>) {
        val repeatableBlocks = blocks.filterIsInstance(ControllableBlock::class.java)
        val moduleBlocks = blocks.filterIsInstance(ModuleBlock::class.java)
        val loopParamBlocks = blocks.filterIsInstance(LoopParamBlock::class.java)
        moduleBlocks.forEach { it.affect(loopParamBlocks, repeatableBlocks) }
    }

    private fun removeFalseTestBlocks() {
        val ifBlocks = blocks.filterIsInstance(IfBlock::class.java)
        val ifTargetBlocks = blocks.filterIsInstance(ControllableBlock::class.java)
        val ifParamBlocks = blocks.filterIsInstance(IfParamBlock::class.java)
        val presenceBlocks = blocks.filterIsInstance(PresenceBlock::class.java)
        val falseBlocks = ifBlocks.flatMap {
            it.findFalseTestBlocks(ifTargetBlocks, ifParamBlocks, presenceBlocks)
        }
        blocks.removeAll { falseBlocks.contains(it) }
    }

    private fun calculateSpeed() {
        blocks.filterIsInstance(SpeedBlock::class.java)
                .firstOrNull()?.calculateSpeed(board.timeline)
    }

    private fun calculateGlobalVolume() {
        blocks.filterIsInstance(VolumeBlock::class.java)
                .firstOrNull()?.calculateVolume(this)
    }

    private fun defineMusicBeginEnd() {
        val soundBlocks = blocks.filterIsInstance(SoundBlock::class.java)
        val mostLeft = soundBlocks.minBy { it.centerX }
        val mostRight = soundBlocks.maxBy { it.centerX }
        mostLeft?.let {
            board.timeline.begin = it.centerX.toFloat() - 30
        }
        mostRight?.let {
            val lastPadding = (it.centerX - mostLeft?.centerX!!) / soundBlocks.size
            board.timeline.end = it.centerX.toFloat() + if (lastPadding == 0) 100 else lastPadding
        }
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