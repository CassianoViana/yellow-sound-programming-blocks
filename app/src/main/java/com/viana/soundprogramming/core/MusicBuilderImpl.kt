package com.viana.soundprogramming.core

import android.util.Log
import com.viana.soundprogramming.blocks.*
import com.viana.soundprogramming.board.Board

open class MusicBuilderImpl : MusicBuilder {

    override var maxVolume: Float = 1f
    private var music = MusicImpl()
    private lateinit var board: Board
    private var blocks: List<Block> = listOf()

    override fun build(blocks: List<Block>, board: Board, onMusicReadyListener: MusicBuilder.OnMusicReadyListener) {
        Thread({
            this.blocks = blocks
            this.board = board
            calculateSpeed()
            calculateVolume()
            defineMusicBeginEnd()
            computeModuleBlocks()
            buildSounds()
            onMusicReadyListener.ready(music)
            Log.i("Sounds", music.sounds.size.toString())
        }).start()
    }

    private fun calculateSpeed() {
        val speedBlock: Block? = blocks.firstOrNull { it.javaClass == SpeedBlock::class.java }
        speedBlock?.let {
            (it as SpeedBlock).calculateSpeed(board)
        }
    }

    private fun calculateVolume() {
        val volumeBlock: Block? = blocks.firstOrNull { it.javaClass == VolumeBlock::class.java }
        volumeBlock?.let {
            (it as VolumeBlock).calculateVolume(this)
        }
    }

    private fun computeModuleBlocks() {
        val moduleBlocks = blocks.filterIsInstance(ModuleBlock::class.java)
        val soundBlocks = blocks.filterIsInstance(SoundBlock::class.java)
        soundBlocks.forEach { it.active = true }
        val count = board.timeline?.count ?: 0
        moduleBlocks.forEach { moduleBlock ->
            val intersectedSoundBlocks = soundBlocks.filter { soundBlock -> moduleBlock.intersects(soundBlock) }
            intersectedSoundBlocks.forEach {
                it.active = count % (moduleBlock as ModuleBlock).module == 0L
            }
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
            music.sounds = blocks
                    .filter {
                        it.javaClass == SoundBlock::class.java
                                && it.centerX > timeline.begin
                                && it.centerX < timeline.end
                                && it.active
                    }
                    .map {
                        (it as SoundBlock).buildSound(board, this)
                    }
        }
    }

}