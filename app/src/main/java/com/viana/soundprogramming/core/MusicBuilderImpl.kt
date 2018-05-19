package com.viana.soundprogramming.core

import android.content.Context
import android.media.AudioManager
import android.util.Log
import com.viana.soundprogramming.appInstance
import com.viana.soundprogramming.blocks.*
import com.viana.soundprogramming.board.Board
import com.viana.soundprogramming.exceptions.SoundSyntaxError

class MusicBuilderImpl : MusicBuilder {

    override fun isWiredHeadsetOn(): Boolean =
            (appInstance.getSystemService(Context.AUDIO_SERVICE) as AudioManager)
                    .isWiredHeadsetOn

    override lateinit var board: Board
    private lateinit var music: Music

    override var maxVolume: Float = 1f
    override var maxSoundBlockDiameter: Float = 1f

    override var minSoundBlockDiameter: Float = 1f
    private var blocks: List<Block> = listOf()

    override fun build(blocks: List<Block>, board: Board, onMusicReadyListener: MusicBuilder.OnMusicReadyListener) {
        Thread({
            try {
                this.board = board
                music = MusicAudioTrack(this)
                blocks.toMutableList()
                this.blocks = blocks.toMutableList().apply {
                    addAll(repeatRepeatableBlocks(blocks))
                }
                muteFalseTestBlocks()
                calculateSpeed()
                calculateGlobalVolume()
                defineMusicBeginEnd()
                buildSounds()
                onMusicReadyListener.ready(music)
                Log.i("Sounds", music.sounds.size.toString())
            } catch (e: SoundSyntaxError) {
                onMusicReadyListener.error(e)
            }
        }).start()
    }

    private fun repeatRepeatableBlocks(blocks: List<Block>): List<Block> {
        val repeatableBlocks = blocks.filterIsInstance(ControllableBlock::class.java)
        val loopParamBlocks = blocks.filterIsInstance(LoopParamBlock::class.java)
        val loopBlocks = blocks.filterIsInstance(LoopBlock::class.java)
        return loopBlocks.flatMap {
            it.repeatBlocks(repeatableBlocks, loopParamBlocks)
        }
    }

    private fun muteFalseTestBlocks() {
        val ifBlocks = blocks.filterIsInstance(IfBlock::class.java)
        val ifTargetBlocks = blocks.filterIsInstance(ControllableBlock::class.java)
        val ifParamBlocks = blocks.filterIsInstance(IfParamBlock::class.java)
        val presenceBlock = blocks.filterIsInstance(PresenceBlock::class.java)
        ifBlocks.forEach {
            it.muteFalseTestBlocks(ifTargetBlocks, ifParamBlocks, presenceBlock)
        }
    }

    private fun calculateSpeed() {
        val speedBlock: Block? = blocks.firstOrNull { it.javaClass == SpeedBlock::class.java }
        speedBlock?.let {
            (it as SpeedBlock).calculateSpeed(board)
        }
    }

    private fun calculateGlobalVolume() {
        val volumeBlock: Block? = blocks.firstOrNull { it.javaClass == VolumeBlock::class.java }
        volumeBlock?.let {
            (it as VolumeBlock).calculateVolume(this)
        }
    }

    private fun defineMusicBeginEnd() {
        val beginBlocks = blocks.filter { it.javaClass == BeginBlock::class.java }
        val endBlocks = blocks.filter { it.javaClass == EndBlock::class.java }
        if (beginBlocks.isEmpty())
            board.timeline?.resetBegin()
        if (endBlocks.isEmpty())
            board.timeline?.resetEnd()
        val blocks = mutableListOf<Block>()
        blocks.addAll(beginBlocks)
        blocks.addAll(endBlocks)
        blocks.forEach {
            it.board = board
            it.execute()
        }
    }

    private fun buildSounds() {
        board.timeline?.let { timeline ->
            val soundBlocks = blocks
                    .filterIsInstance(SoundBlock::class.java)
                    .filter {
                        val isBeforeEnd = it.centerX < timeline.end
                        val isAfterStart = it.centerX > timeline.begin
                        (it.active && isAfterStart && isBeforeEnd) || it.isRepetitionBlock
                    }
            this.maxSoundBlockDiameter = soundBlocks.map { it.diameter }.max() ?: 0f
            if (soundBlocks.size == 1) {
                minSoundBlockDiameter = 0f
            } else {
                this.minSoundBlockDiameter = soundBlocks.map { it.diameter }.min() ?: 0f
            }
            music.sounds = soundBlocks
                    .map {
                        music.soundBuilder.build(it)
                    }
        }
    }

}