package com.viana.soundprogramming.core

import com.viana.soundprogramming.timeline.TimelineTimer

class MusicImpl : Music() {

    override fun play(timelineTimer: TimelineTimer) {
        sounds.forEach{
            it.play(timelineTimer)
        }
    }
}