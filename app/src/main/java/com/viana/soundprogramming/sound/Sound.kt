package com.viana.soundprogramming.sound

import com.viana.soundprogramming.blocks.SoundBlock
import com.viana.soundprogramming.board.Board
import com.viana.soundprogramming.core.MusicBuilder
import com.viana.soundprogramming.timeline.TimelineTimer
import com.viana.soundprogramming.vibration.ProgrammingVibrator
import java.io.InputStream
import java.util.*

abstract class Sound {

    var volume: Float = 0f
    var volumeLeft: Float = 0f
    var volumeRight: Float = 0f

    abstract fun play()

    interface Builder {
        fun build(soundBlock: SoundBlock): Sound

        var musicBoard: Board
        var musicBuilder: MusicBuilder
        val minGlobalVolume: Float
        var variantVolume: Float
        val minVolume: Float

        fun calculateVolumeLeft(soundBlock: SoundBlock): Float {
            val end = musicBoard.timeline?.end!!
            val begin = musicBoard.timeline?.begin!!
            val deltaBeginEnd = end - begin
            return (deltaBeginEnd - (soundBlock.centerX - begin)) / deltaBeginEnd
        }

        fun calculateVolumeRight(soundBlock: SoundBlock): Float {
            val end = musicBoard.timeline?.end!!
            val begin = musicBoard.timeline?.begin!!
            val deltaBeginEnd = end - begin
            return (deltaBeginEnd - (end - soundBlock.centerX)) / deltaBeginEnd
        }

        fun calculateVolumeByDiameter(soundBlock: SoundBlock): Float {
            val maxDiameter = musicBuilder.maxSoundBlockDiameter
            val minDiameter = musicBuilder.minSoundBlockDiameter
            if (maxDiameter - minDiameter <= 10) return musicBuilder.maxVolume
            return (minVolume + variantVolume * ((soundBlock.diameter - minDiameter) / (maxDiameter - minDiameter))) * musicBuilder.maxVolume
        }

        fun calculateVolumeByDegree(soundBlock: SoundBlock): Float {
            var degree = soundBlock.degree
            degree += 180
            if (degree > 360)
                degree -= 360
            val volume = Math.abs(degree) / 360
            return volume * (musicBuilder.maxVolume - minGlobalVolume)
        }

        fun calculatePlayMoment(soundBlock: SoundBlock): Long =
                musicBoard.timeline?.let {
                    (it.secondsToTraverseWidth / it.speedFactor * 1000 *
                            (soundBlock.centerX - it.begin) / musicBoard.widthFloat).toLong()
                } ?: 0
    }

    class SoundPoolSoundBuilder(override var musicBuilder: MusicBuilder) : Builder {

        override var musicBoard: Board = musicBuilder.board
        override val minGlobalVolume = 0f
        override var variantVolume = 0.8f
        override val minVolume = 0.2f

        override fun build(soundBlock: SoundBlock): Sound {
            val soundId = soundBlock.soundId
            val sound = SoundSoundPool(soundId!!)
            val volume = calculateVolumeByDiameter(soundBlock)
            sound.volume = volume
            if (musicBuilder.isWiredHeadsetOn()) {
                sound.volumeLeft = calculateVolumeLeft(soundBlock) * volume
                sound.volumeRight = calculateVolumeRight(soundBlock) * volume
            } else {
                sound.volumeLeft = volume
                sound.volumeRight = volume
            }
            sound.delayMillis = calculatePlayMoment(soundBlock)
            return sound
        }
    }

    class SoundAudioTrackBuilder(
            override var musicBuilder: MusicBuilder) : Builder {

        override var musicBoard = musicBuilder.board
        override val minGlobalVolume = 0f
        override var variantVolume = 0.8f
        override val minVolume = 0.2f

        override fun build(soundBlock: SoundBlock): Sound {
            val sound = SoundAudioTrack(soundBlock.soundStream!!)
            val volume = calculateVolumeByDiameter(soundBlock)
            sound.volume = volume
            if (musicBuilder.isWiredHeadsetOn()) {
                sound.volumeLeft = calculateVolumeLeft(soundBlock) * volume
                sound.volumeRight = calculateVolumeRight(soundBlock) * volume
            } else {
                sound.volumeLeft = volume
                sound.volumeRight = volume
            }
            return sound
        }
    }
}

class SoundSoundPool(private val soundId: Int) : Sound() {
    private val timer = Timer()
    var timelineTimer: TimelineTimer? = null
    var delayMillis: Long = 500
    override fun play() {
        if (delayMillis > 0 && delayMillis < Int.MAX_VALUE)
            timer.schedule(object : TimerTask() {
                override fun run() {
                    if (!timelineTimer!!.cancelled) {
                        SoundManager.instance.play(soundId, volumeLeft, volumeRight)
                        ProgrammingVibrator.vibrate((volume * 5).toLong())
                    }
                }
            }, delayMillis)
    }
}

class SoundAudioTrack(var soundInputStream: InputStream) : Sound() {
    override fun play() {

    }
}