package com.viana.soundprogramming.sound

import android.media.MediaPlayer
import com.viana.soundprogramming.appInstance

class Sound(rawSound: Int) {

    private val mediaPlayer = MediaPlayer.create(appInstance, rawSound)

    fun play() {
        mediaPlayer.start()
    }
}
