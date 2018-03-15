package com.viana.soundprogramming.sound

import android.media.AudioAttributes
import android.media.SoundPool
import com.viana.soundprogramming.appInstance

class SoundManager {

    companion object {
        val instance = SoundManager()
    }

    private val soundPool: SoundPool

    constructor() {

        var audioAttributes: AudioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

        soundPool = SoundPool.Builder()
                .setMaxStreams(10)
                .setAudioAttributes(audioAttributes)
                .build()


    }

    fun load(resId: Int): Int {
        return soundPool.load(appInstance, resId, 1)
    }

    fun play(soundId: Int) {
        soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
    }
}