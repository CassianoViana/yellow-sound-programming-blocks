package com.viana.soundprogramming.sound

import com.viana.soundprogramming.timeline.TimelineTimer
import com.viana.soundprogramming.vibration.ProgrammingVibrator
import java.util.*

class Sound(private val soundId: Int) {
    private val timer = Timer()
    var volume: Float = 0f
    var volumeLeft: Float = 0f
    var volumeRight: Float = 0f
    var delayMillis: Long = 500

    fun play(timelineTimer: TimelineTimer) {
        if (delayMillis > 0 && delayMillis < Int.MAX_VALUE)
            timer.schedule(object : TimerTask() {
                override fun run() {
                    if (!timelineTimer.cancelled) {
                        SoundManager.instance.play(soundId, volumeLeft, volumeRight)
                        ProgrammingVibrator.vibrate((volume * 5).toLong())
                    }
                }
            }, delayMillis)
    }
}
