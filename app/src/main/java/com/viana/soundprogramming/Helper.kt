package com.viana.soundprogramming

import com.viana.soundprogramming.blocks.Block
import com.viana.soundprogramming.blocks.BlocksManager
import com.viana.soundprogramming.sound.Speaker

class Helper : BlocksManager.Listener, StateMachine.Listener {

    private var helping: Boolean = false

    override fun updateBlocksList(blocks: List<Block>) {
        if (helping) {
            val blockToExplain = blocks.firstOrNull { it.diameter > 90 }
            blockToExplain?.let {
                Speaker.instance.say(it.soundHelpResId)
            }
        }
    }

    override fun stateChanged(state: StateMachine.State, previous: StateMachine.State) {
        this.helping = state == StateMachine.State.HELPING
    }
}
