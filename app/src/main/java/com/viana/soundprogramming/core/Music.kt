package com.viana.soundprogramming.core

import com.viana.soundprogramming.sound.Sound

abstract class Music(var soundBuilder: Sound.Builder) {

    var sounds = listOf<Sound>()
    open var speed: Float = 0f
    abstract fun play(index: Int)

    open fun stop() {}

    abstract fun prepare()
}
