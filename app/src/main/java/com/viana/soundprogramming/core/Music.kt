package com.viana.soundprogramming.core

import com.viana.soundprogramming.sound.Sound
import com.viana.soundprogramming.timeline.TimelineTimer

abstract class Music {

    var sounds = listOf<Sound>()
    abstract fun play(timelineTimer: TimelineTimer)

}
