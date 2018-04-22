package com.viana.soundprogramming.core

import com.viana.soundprogramming.sound.Sound

abstract class Music {

    var sounds = listOf<Sound>()
    abstract fun play()

}
