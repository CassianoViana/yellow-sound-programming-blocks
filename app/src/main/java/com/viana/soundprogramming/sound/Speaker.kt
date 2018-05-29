package com.viana.soundprogramming.sound

import android.media.MediaPlayer
import com.viana.soundprogramming.appInstance

class Speaker {

    companion object {
        val instance: Speaker = Speaker()
    }

    fun say(resId: Int) {
        MyMediaPlayer.play(resId)
    }
}

object MyMediaPlayer {
    private var mediaPlayer: MediaPlayer? = null
    fun play(resId: Int) {
        if (mediaPlayer != null && mediaPlayer!!.isPlaying)
            return
        mediaPlayer = MediaPlayer.create(appInstance, resId)
        mediaPlayer?.start()
    }
}