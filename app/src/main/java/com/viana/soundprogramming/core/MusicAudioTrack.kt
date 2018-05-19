package com.viana.soundprogramming.core

import com.viana.soundprogramming.sound.AudioSequencer
import com.viana.soundprogramming.sound.Sound
import com.viana.soundprogramming.sound.SoundAudioTrack

class MusicAudioTrack(val musicBuilder: MusicBuilder) : Music(Sound.SoundAudioTrackBuilder(musicBuilder)) {

    private val audioSequencer = AudioSequencer()
    private var playing: Boolean = false

    override fun play() {
        if (playing) return
        playing = true
        musicBuilder.board.timeline?.secondsToTraverseWidth?.toFloat()?.let {
            audioSequencer.setup((it * 1000).toInt())
        }
        sounds.forEach {
            val soundAudioTrack = it as SoundAudioTrack
            audioSequencer.add(it.delayMillis, soundAudioTrack.soundInputStream)
        }
        audioSequencer.play()
        playing = false
    }

    override fun stop() {
        audioSequencer.stop()
    }
}