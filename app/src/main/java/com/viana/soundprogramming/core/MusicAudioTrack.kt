package com.viana.soundprogramming.core

import com.viana.soundprogramming.sound.AudioSequencer
import com.viana.soundprogramming.sound.Sound
import com.viana.soundprogramming.sound.SoundAudioTrack

class MusicAudioTrack(musicBuilder: MusicBuilder) : Music(Sound.SoundAudioTrackBuilder(musicBuilder)) {

    private val audioSequencer = AudioSequencer()

    override fun play() {
        sounds.forEach {
            val soundAudioTrack = it as SoundAudioTrack
            audioSequencer.add(soundAudioTrack.soundInputStream)
        }
        audioSequencer.play()
    }
}