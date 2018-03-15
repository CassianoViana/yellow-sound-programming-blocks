package com.viana.soundprogramming.core

import com.viana.soundprogramming.R
import com.viana.soundprogramming.blocks.Block
import com.viana.soundprogramming.sound.Sound

open class MusicBuilderImpl : MusicBuilder{

    private var music = MusicImpl()

    override fun build(blocks: List<Block>): Music {
        music.clear()
        blocks.forEach {
            music.sounds.add(Sound(R.raw.cat1))
        }
        return music
    }

}