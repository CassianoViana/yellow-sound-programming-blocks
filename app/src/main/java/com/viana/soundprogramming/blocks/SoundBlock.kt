package com.viana.soundprogramming.blocks

import android.media.MediaPlayer
import com.viana.soundprogramming.SoundProgrammingApp

class SoundBlock(rawSound: Int) : Block() {

    private val mediaPlayer: MediaPlayer = MediaPlayer.create(SoundProgrammingApp.instance, rawSound)

    override fun execute() {
        mediaPlayer.start()
    }
}