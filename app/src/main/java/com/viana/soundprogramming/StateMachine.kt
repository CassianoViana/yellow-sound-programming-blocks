package com.viana.soundprogramming

import com.viana.soundprogramming.blocks.Block
import com.viana.soundprogramming.blocks.BlocksManager
import com.viana.soundprogramming.blocks.PlayBlock

class StateMachine : BlocksManager.Listener {

    enum class State {
        PLAYING, PAUSED, RECORDING, EXPLAINING
    }

    val listeners = mutableListOf<Listener>()

    var state: State = State.PAUSED
        set(value) {
            val changed = value != field
            field = value
            if (changed)
                listeners.forEach { it.stateChanged(state) }
        }

    override fun updateBlocksList(blocks: List<Block>) {
        checkIfPlayBlockEntered(blocks)
    }

    private fun checkIfPlayBlockEntered(blocks: List<Block>) {
        val beginBlocks: List<Block> = blocks.filter { it.javaClass == PlayBlock::class.java }
        state = if (!beginBlocks.isEmpty()) State.PLAYING else State.PAUSED
    }

    fun addListener(listener: Listener): StateMachine {
        this.listeners.add(listener)
        return this
    }

    interface Listener {
        fun stateChanged(state: State) {}
    }
}