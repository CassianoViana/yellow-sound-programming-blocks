package com.viana.soundprogramming

import com.viana.soundprogramming.blocks.*

class StateMachine : BlocksManager.Listener {

    enum class State {
        PLAYING,
        PAUSED,
        RECORDING,
        HELPING;
    }

    private val listeners = mutableListOf<Listener>()

    var state: State = State.PAUSED
        set(value) {
            val changed = value != field
            val previous = field
            field = value
            if (changed) {
                listeners.forEach { it.stateChanged(state, previous) }
            }
        }

    override fun updateBlocksList(blocks: List<Block>) {
        checkBlocksToChangeState(blocks)
    }

    private fun checkBlocksToChangeState(blocks: List<Block>) {
        val recordBlock = blocks.firstOrNull { it.javaClass == RecordBlock::class.java }
        if (recordBlock != null) {
            state = State.RECORDING
            return
        }

        val playBlock = blocks.firstOrNull { it.javaClass == PlayBlock::class.java }
        if (playBlock != null) {
            state = State.PLAYING
            return
        }

        val pauseBlock = blocks.firstOrNull { it.javaClass == PauseBlock::class.java }
        if (pauseBlock != null) {
            state = State.PAUSED
            return
        }

        val helpBlock = blocks.firstOrNull { it.javaClass == HelpBlock::class.java }
        if (helpBlock != null) {
            state = State.HELPING
            return
        }
    }

    fun addListener(listener: Listener?): StateMachine {
        listener?.let { this.listeners.add(it) }
        return this
    }

    interface Listener {
        fun stateChanged(state: State, previous: State) {}
    }
}