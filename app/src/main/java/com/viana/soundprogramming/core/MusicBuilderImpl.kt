package com.viana.soundprogramming.core

import android.content.Context
import android.media.AudioManager
import android.util.Log
import com.viana.soundprogramming.appInstance
import com.viana.soundprogramming.blocks.*
import com.viana.soundprogramming.board.Board
import com.viana.soundprogramming.exceptions.BaseOutOfCornersError
import com.viana.soundprogramming.exceptions.SoundProgrammingError

class MusicBuilderImpl : MusicBuilder {

    override fun isWiredHeadsetOn(): Boolean =
            (appInstance.getSystemService(Context.AUDIO_SERVICE) as AudioManager)
                    .isWiredHeadsetOn

    override lateinit var board: Board
    private lateinit var music: Music

    override var maxVolume: Float = 1f
    override var maxSoundBlockDiameter: Float = 1f

    override var minSoundBlockDiameter: Float = 1f
    private var blocks: MutableList<Block> = mutableListOf()

    override fun build(blocks: List<Block>, board: Board, onMusicReadyListener: MusicBuilder.OnMusicReadyListener) {
        Thread({
            try {
                this.board = board
                music = MusicSoundPool(this)
                this.blocks = blocks.toMutableList()
                checkIfIsInsideCorners()
                removeFalseTestBlocks()
                removeOutsideBlocks()
                computeModuleBlocks()
                repeatRepeatableBlocks(blocks)
                calculateSpeed()
                calculateGlobalVolume()
                buildSounds()
                defineMusicBeginEnd()
                (board as BlocksManager.Listener).updateBlocksList(this.blocks)
                music.prepare()
                onMusicReadyListener.ready(music)
                Log.i("Sounds", music.sounds.size.toString())
            } catch (e: SoundProgrammingError) {
                onMusicReadyListener.error(e)
            }
        }).start()
    }

    private fun removeOutsideBlocks() {
        val cornerBlocks = this.blocks.filterIsInstance(CornerBlock::class.java)

        var maxX = Integer.MAX_VALUE
        var minX = 0
        var maxY = Integer.MAX_VALUE
        var minY = 0

        cornerBlocks.maxBy { it.centerX }.let { maxX = it?.centerX ?: 0 }
        cornerBlocks.maxBy { it.centerY }.let { maxY = it?.centerY ?: 0 }
        cornerBlocks.minBy { it.centerX }.let { minX = it?.centerX ?: 0 }
        cornerBlocks.minBy { it.centerY }.let { minY = it?.centerY ?: 0 }

        this.blocks.removeAll { it.centerX > maxX || it.centerX < minX || it.centerY < minY || it.centerY > maxY }
    }

    private fun checkIfIsInsideCorners() {
        if (blocks.filterIsInstance(CornerBlock::class.java).size < 3)
            throw BaseOutOfCornersError()

    }

    private fun computeModuleBlocks() {
        val moduleBlocks = blocks.filterIsInstance(ModuleBlock::class.java)
        val controllableBlocks = blocks.filterIsInstance(ControllableBlock::class.java)
        moduleBlocks.forEach { it.disableSomeBlocks(controllableBlocks) }
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

    private fun removeFalseTestBlocks() {
        val ifBlocks = blocks.filterIsInstance(IfBlock::class.java)
        val ifTargetBlocks = blocks.filterIsInstance(ControllableBlock::class.java)
        val ifParamBlocks = blocks.filterIsInstance(IfParamBlock::class.java)
        val presenceBlock = blocks.filterIsInstance(PresenceBlock::class.java)
        val falseBlocks = ifBlocks.flatMap {
            it.findFalseTestBlocks(ifTargetBlocks, ifParamBlocks, presenceBlock)
        }
        blocks.removeAll { falseBlocks.contains(it) }
    }

    private fun calculateSpeed() {
        val speedBlock: Block? = blocks.firstOrNull { it.javaClass == SpeedBlock::class.java }
        speedBlock?.let {
            (it as SpeedBlock).calculateSpeed(board.timeline)
        }
    }

    private fun calculateGlobalVolume() {
        val volumeBlock: Block? = blocks.firstOrNull { it.javaClass == VolumeBlock::class.java }
        volumeBlock?.let {
            (it as VolumeBlock).calculateVolume(this)
        }
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
                .filter { it.active }
        this.maxSoundBlockDiameter = soundBlocks.map { it.diameter }.max() ?: 0f
        if (soundBlocks.size == 1) {
            minSoundBlockDiameter = 0f
        } else {
            this.minSoundBlockDiameter = soundBlocks.map { it.diameter }.min() ?: 0f
        }
        music.sounds = soundBlocks.map { music.soundBuilder.build(it) }
    }

}