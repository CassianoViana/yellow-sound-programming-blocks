package com.viana.soundprogramming.sound

import android.media.AudioAttributes
import android.media.SoundPool
import android.util.Log
import com.viana.soundprogramming.appInstance

class SoundManager {

    companion object {
        val instance = SoundManager()
    }

    private val soundPool: SoundPool

    private val failedNotReadySounds: MutableList<Int> = mutableListOf()

    constructor() {
        var audioAttributes: AudioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

        soundPool = SoundPool.Builder()
                .setMaxStreams(10)
                .setAudioAttributes(audioAttributes)
                .build()

        soundPool.setOnLoadCompleteListener { a: SoundPool, soundId: Int, c: Int ->
            Log.i("SoundManager", "load sound $soundId, $c")
            if (failedNotReadySounds.contains(soundId)) {
                play(soundId)
                failedNotReadySounds.remove(soundId)
            }
        }
    }

    fun load(resId: Int): Int {
        return soundPool.load(appInstance, resId, 1)
    }

    fun load(path: String): Int {
        return soundPool.load(path, 1)
    }

    fun play(soundId: Int) {
        val playTryResult: Int = soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
        tryAgainIfNotRead(playTryResult, soundId)
    }

    fun play(soundId: Int, volume: Float) {
        val playTryResult = soundPool.play(soundId, volume, volume, 1, 0, 1f)
        tryAgainIfNotRead(playTryResult, soundId)
    }

    fun play(soundId: Int, volumeLeft: Float, volumeRight: Float) {
        //Log.i("play", "volumeLeft: $volumeLeft, volumeRight: $volumeRight")
        val playTryResult = soundPool.play(soundId, volumeLeft, volumeRight, 1, 0, 1f)
        tryAgainIfNotRead(playTryResult, soundId)
    }

    private fun tryAgainIfNotRead(playTryResult: Int, soundId: Int) {
        if (playTryResult == 0) {
            failedNotReadySounds.add(soundId)
        }
    }
}