package com.viana.soundprogramming

import com.viana.soundprogramming.blocks.Block
import com.viana.soundprogramming.blocks.BlocksManager
import com.viana.soundprogramming.sound.Speaker

class Helper : BlocksManager.Listener {
    override fun updateBlocksList(blocks: List<Block>) {
        val blockToExplain = blocks.firstOrNull { it.diameter > 120 }
        blockToExplain?.let {
            Speaker.instance.say(it.soundHelpResId)
        }
    }
}
