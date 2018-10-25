package com.viana.soundprogramming

import com.viana.soundprogramming.blocks.Block
import com.viana.soundprogramming.blocks.BlocksManager
import com.viana.soundprogramming.blocks.LockBlock
import com.viana.soundprogramming.blocks.PresenceBlock
import com.viana.soundprogramming.board.Board
import com.viana.soundprogramming.core.Music
import com.viana.soundprogramming.core.MusicBuilder
import com.viana.soundprogramming.core.MusicBuilderImpl
import com.viana.soundprogramming.exceptions.SoundProgrammingError
import com.viana.soundprogramming.sound.Speaker

class MusicManager(
        private val stateMachine: StateMachine,
        private val board: Board) : BlocksManager.Listener {

    var music: Music? = null
    private val musicBuilder = MusicBuilderImpl()
    private var boardBlocks = mutableListOf<Block>()

    override fun updateBlocksList(blocks: List<Block>) {
        val locked = blocks.any { it.javaClass == LockBlock::class.java }
        if (locked) return
        if (stateMachine.state == StateMachine.State.PLAYING) {
            val onlyPresenceBlocksWereAddedOrRemoved = onlyPresenceBlocksWereAddedOrRemoved(blocks)
            boardBlocks = blocks.toMutableList()
            if (onlyPresenceBlocksWereAddedOrRemoved) {
                updateCurrentMusicSoundsAffectedByIfTests()
            } else {
                buildMusic()
            }
        }
    }

    private fun onlyPresenceBlocksWereAddedOrRemoved(blocks: List<Block>): Boolean {
        val newPresenceBlocks = blocks.filter { it is PresenceBlock }
        val oldPresenceBlocks = boardBlocks.filter { it is PresenceBlock }
        val newNotPresenceBlocks = blocks.filter { it !is PresenceBlock }
        val oldNotPresenceBlocks = boardBlocks.filter { it !is PresenceBlock }
        return oldPresenceBlocks.size != newPresenceBlocks.size && newNotPresenceBlocks.size == oldNotPresenceBlocks.size
    }

    private fun updateCurrentMusicSoundsAffectedByIfTests() {
        val presenceBlocksSituation = boardBlocks.filterIsInstance(PresenceBlock::class.java).map { it.type }
        music?.let { music ->
            music.sounds
                    .filter { it.conditionType != null }
                    .forEach {
                        it.ifConditionSatisfied = presenceBlocksSituation.contains(it.conditionType)
                    }
        }
    }

    private fun buildMusic() {
        musicBuilder.build(boardBlocks,
                board,
                object : MusicBuilder.OnMusicReadyListener {
                    override fun ready(music: Music) {
                        this@MusicManager.music = music
                    }

                    override fun error(e: SoundProgrammingError) {
                        e.printStackTrace()
                        Speaker.instance.say(e.explanationResId)
                    }
                })
    }

}
