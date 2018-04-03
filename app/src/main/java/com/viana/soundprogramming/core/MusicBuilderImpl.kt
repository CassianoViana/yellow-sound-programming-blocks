package com.viana.soundprogramming.core

import com.viana.soundprogramming.blocks.*
import com.viana.soundprogramming.board.Board
import com.viana.soundprogramming.sound.Sound

open class MusicBuilderImpl : MusicBuilder {

    private var music = MusicImpl()
    private lateinit var blocks: List<Block>
    private lateinit var board: Board

    override fun build(blocks: List<Block>, board: Board): Music {
        music.clear()
        this.board = board
        this.blocks = blocks
        calculateSpeed()
        defineMusicBeginEnd()
        computeModuleBlocks()
        buildSounds()
        return music
    }

    private fun calculateSpeed() {
        val speedBlock = blocks.filter { it.javaClass == SpeedBlock::class.java }
        if (speedBlock.isNotEmpty()) {
            val first: Block? = speedBlock.first()
            first?.let {
                (it as SpeedBlock).calculateSpeed(board.timeline())
            }
        }
    }

    private fun computeModuleBlocks() {
        val moduleBlocks = blocks.filter { it.javaClass == ModuleBlock::class.java }
        val soundBlocks = blocks.filter { it.javaClass == SoundBlock::class.java }
        soundBlocks.forEach { it.active = true }
        val count = board.timeline().count
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
            board.timeline().resetBegin()
        if (endBlocks.isEmpty())
            board.timeline().resetEnd()
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
                    && it.centerX > board.timeline().begin
                    && it.centerX < board.timeline().end
                    && it.active
        }
        soundBlocks.forEach {
            val soundId = (it as SoundBlock).soundId
            val sound = Sound(soundId)
            val timeline = board.timeline()
            val boardWidth = board.widthFloat()
            sound.delayMillis = (timeline.secondsToTraverseWidth / timeline.speedFactor * 1000 * (it.centerX - timeline.begin) / boardWidth).toLong()
            sound.volume = Math.abs(it.degree / 360)
            music.sounds.add(sound)
        }
    }
}