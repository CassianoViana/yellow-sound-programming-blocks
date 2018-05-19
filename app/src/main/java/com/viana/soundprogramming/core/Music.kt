package com.viana.soundprogramming.core

import com.viana.soundprogramming.sound.Sound
import com.viana.soundprogramming.timeline.TimelineTimer

abstract class Music(var soundBuilder: Sound.Builder) {

    var sounds = listOf<Sound>()
    abstract fun play(timelineTimer: TimelineTimer)

    open fun stop(){}

}
