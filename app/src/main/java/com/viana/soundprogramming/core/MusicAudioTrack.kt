package com.viana.soundprogramming.core

import com.viana.soundprogramming.sound.AudioSequencer
import com.viana.soundprogramming.sound.Sound
import com.viana.soundprogramming.sound.SoundAudioTrack
import com.viana.soundprogramming.timeline.TimelineTimer

class MusicAudioTrack(musicBuilder: MusicBuilder) : Music(Sound.SoundAudioTrackBuilder(musicBuilder)) {

    private val audioSequencer = AudioSequencer()
    private var playing: Boolean = false

    override fun play(timelineTimer: TimelineTimer) {
        if (playing) return
        playing = true
        audioSequencer.clear()
        sounds.forEach {
            val soundAudioTrack = it as SoundAudioTrack
            audioSequencer.add(soundAudioTrack.soundInputStream)
        }
        audioSequencer.play()
        playing = false
    }

    override fun stop(){
        audioSequencer.stop()
    }
}