package com.viana.soundprogramming.core

import com.viana.soundprogramming.sound.Sound
import com.viana.soundprogramming.sound.SoundSoundPool
import com.viana.soundprogramming.timeline.TimelineTimer

class MusicSoundPool(var musicBuilder: MusicBuilder) : Music(Sound.SoundPoolSoundBuilder(musicBuilder)) {

    override fun play(timelineTimer: TimelineTimer) {
        val timer: TimelineTimer? = musicBuilder.board.timeline?.timer
        if (timer != null) {
            sounds.forEach {
                (it as SoundSoundPool).play(timer)
            }
        }
    }
}