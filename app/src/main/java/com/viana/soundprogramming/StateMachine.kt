package com.viana.soundprogramming

import com.viana.soundprogramming.blocks.Block
import com.viana.soundprogramming.blocks.BlocksManager
import com.viana.soundprogramming.blocks.PauseBlock
import com.viana.soundprogramming.blocks.PlayBlock

class StateMachine : BlocksManager.Listener {

    enum class State {
        PLAYING, PAUSED, RECORDING, EXPLAINING
    }

    private val listeners = mutableListOf<Listener>()

    private var state: State = State.PLAYING
        set(value) {
            val changed = value != field
            field = value
            if (changed)
                listeners.forEach { it.stateChanged(state) }
        }

    override fun updateBlocksList(blocks: List<Block>) {
        checkIfPlayPauseBlocksEntered(blocks)
    }

    private fun checkIfPlayPauseBlocksEntered(blocks: List<Block>) {
        val playBlock: Block? = blocks.firstOrNull { it.javaClass == PlayBlock::class.java }
        val pauseBlock: Block? = blocks.firstOrNull { it.javaClass == PauseBlock::class.java }
        state = if (playBlock != null || (pauseBlock == null && state != State.PAUSED)) State.PLAYING else State.PAUSED
    }

    fun addListener(listener: Listener?): StateMachine {
        listener?.let { this.listeners.add(it) }
        return this
    }

    interface Listener {
        fun stateChanged(state: State) {}
    }
}