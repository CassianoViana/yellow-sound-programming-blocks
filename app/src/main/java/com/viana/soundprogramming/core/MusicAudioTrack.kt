package com.viana.soundprogramming.core

import android.util.Log
import com.viana.soundprogramming.sound.AudioMixerShort
import com.viana.soundprogramming.sound.AudioTrackPlayer
import com.viana.soundprogramming.sound.Sound
import com.viana.soundprogramming.sound.SoundAudioTrack
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

class MusicAudioTrack(val musicBuilder: MusicBuilder) : Music(Sound.SoundAudioTrackBuilder(musicBuilder)) {

    private var stopRequested: Boolean = false
    private val audioTrackPlayer = AudioTrackPlayer()

    override fun play() {
        val cycleInterval = musicBuilder.board.timeline?.cycleInterval ?: 4000
        Log.i("MusicAudioTrack", "play, interval $cycleInterval")
        val audioMixer = AudioMixerShort(cycleInterval / 1000, 0.9f)
        audioTrackPlayer.setSpeedFactor(musicBuilder.board.timeline?.speedFactor!!)
        sounds.forEach {
            it as SoundAudioTrack
            val second = timeInSeconds(it)
            audioMixer.addSound(second, it.soundShortArray)
        }
        val mixAddedSounds = audioMixer.mixAddedSounds()
        play(mixAddedSounds)
    }

    private fun play(mixAddedSounds: ShortArray?) {
        try {
            audioTrackPlayer.start()
            audioTrackPlayer.onReachEnd(mixAddedSounds) {
                if (!stopRequested)
                    play(mixAddedSounds)
            }
            audioTrackPlayer.playShortSamples(mixAddedSounds)
            audioTrackPlayer.stopImmediately()
            audioTrackPlayer.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun timeInSeconds(it: Sound) =
            BigDecimal(it.delayMillis, MathContext(2, RoundingMode.HALF_UP))
                    .divide(BigDecimal(1000), 2, RoundingMode.HALF_UP).toFloat()

    override fun stop() {
        this.stopRequested = true
        try {
            audioTrackPlayer.stopImmediately()
            audioTrackPlayer.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}