package com.viana.soundprogramming.core

import com.viana.soundprogramming.sound.Sound

abstract class Music {

    val sounds = mutableListOf<Sound>()
    abstract fun play()
    abstract fun pause()
    abstract fun clear()

}
