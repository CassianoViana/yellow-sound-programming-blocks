package com.viana.soundprogramming.sound

import com.viana.soundprogramming.vibration.ProgrammingVibrator
import java.util.*

class Sound(private val soundId: Int) {
    private val timer = Timer()
    var volume: Float = 0f
    var delayMillis: Long = 500

    fun play() {
        timer.schedule(object : TimerTask() {
            override fun run() {
                SoundManager.instance.play(soundId, volume.toFloat())
                ProgrammingVibrator.vibrate((volume * 10).toLong())
            }
        }, delayMillis)
    }
}