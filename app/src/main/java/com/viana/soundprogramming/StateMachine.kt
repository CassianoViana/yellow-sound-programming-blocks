package com.viana.soundprogramming

import com.viana.soundprogramming.blocks.*

class StateMachine : BlocksManager.Listener {

    enum class State {
        PLAYING,
        PAUSED,
        RECORDING,
        EXPLAINING;
    }

    private val listeners = mutableListOf<Listener>()

    private var state: State = State.PLAYING
        set(value) {
            val changed = value != field
            field = value
            if (changed) {
                listeners.forEach { it.stateChanged(state) }
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
    }

    fun addListener(listener: Listener?): StateMachine {
        listener?.let { this.listeners.add(it) }
        return this
    }

    interface Listener {
        fun stateChanged(state: State) {}
    }
}