package com.viana.soundprogramming.core

import com.viana.soundprogramming.blocks.*
import com.viana.soundprogramming.board.Board

open class MusicBuilderImpl : MusicBuilder {
    override var maxVolume: Float = 1f
    private var music = MusicImpl()
    private lateinit var blocks: List<Block>
    private lateinit var board: Board

    override fun build(blocks: List<Block>, board: Board): Music {
        music.clear()
        this.board = board
        this.blocks = blocks
        calculateSpeed()
        calculateVolume()
        defineMusicBeginEnd()
        computeModuleBlocks()
        buildSounds()
        return music
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
        val moduleBlocks = blocks.filter { it.javaClass == ModuleBlock::class.java }
        val soundBlocks = blocks.filter { it.javaClass == SoundBlock::class.java }
        soundBlocks.forEach { it.active = true }
        val count = board.timeline.count
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
            board.timeline.resetBegin()
        if (endBlocks.isEmpty())
            board.timeline.resetEnd()
        val blocks = mutableListOf<Block>()
        blocks.addAll(beginBlocks)
        blocks.addAll(endBlocks)
        blocks.forEach {
            it.board = board
            it.execute()
        }
    }

    private fun buildSounds() {
        val soundBlocks = blocks.filter {
            it.javaClass == SoundBlock::class.java
                    && it.centerX > board.timeline.begin
                    && it.centerX < board.timeline.end
                    && it.active
        }
        soundBlocks.forEach {
            val sound = (it as SoundBlock).buildSound(board, this)
            music.sounds.add(sound)
        }
    }
}