package com.viana.soundprogramming.sound

import android.util.Log
import com.viana.soundprogramming.blocks.PresenceBlock
import com.viana.soundprogramming.blocks.SoundBlock
import com.viana.soundprogramming.board.Board
import com.viana.soundprogramming.core.MusicBuilder
import com.viana.soundprogramming.timeline.TimelineTimer
import com.viana.soundprogramming.timeline.countLoops
import java.util.*

abstract class Sound {

    var volume: Float = 0f
    var volumeLeft: Float = 0f
    var volumeRight: Float = 0f
    var delayMillis: Long = 500
    var musicId: UUID? = null
    var module: Byte = 1
    var conditionType: PresenceBlock.Type? = null
    var ifConditionSatisfied: Boolean = true

    abstract fun play(timer: TimelineTimer)

    interface Builder {
        fun build(soundBlock: SoundBlock): Sound

        var musicBoard: Board
        var musicBuilder: MusicBuilder
        var volumeVariation: Float
        val minVolume: Float

        fun calculateVolumeLeft(soundBlock: SoundBlock): Float {
            val end = musicBoard.timeline.end
            val begin = musicBoard.timeline.begin
            val deltaBeginEnd = end - begin
            return (deltaBeginEnd - (soundBlock.originalCenterX - begin)) / deltaBeginEnd
        }

        fun calculateVolumeRight(soundBlock: SoundBlock): Float {
            val end = musicBoard.timeline.end
            val begin = musicBoard.timeline.begin
            val deltaBeginEnd = end - begin
            return (deltaBeginEnd - (end - soundBlock.originalCenterX)) / deltaBeginEnd
        }

        fun calculateVolumeByDiameter(soundBlock: SoundBlock): Float {
            val maxDiameter = musicBuilder.maxSoundBlockDiameter
            val minDiameter = musicBuilder.minSoundBlockDiameter
            if (maxDiameter - minDiameter <= 10) return musicBuilder.maxVolume
            return (minVolume + volumeVariation * ((soundBlock.diameter - minDiameter) / (maxDiameter - minDiameter))) * musicBuilder.maxVolume
        }

        fun calculatePlayMoment(soundBlock: SoundBlock): Long =
                musicBoard.timeline.let {
                    (it.secondsToTraverseWidth / it.speedFactor * 1000 *
                            (soundBlock.centerX - it.begin) / musicBoard.widthFloat).toLong()
                }
    }

    class SoundPoolSoundBuilder(override var musicBuilder: MusicBuilder) : Builder {

        override var musicBoard: Board = musicBuilder.board
        override var volumeVariation = 0.8f
        override val minVolume = 0.2f

        override fun build(soundBlock: SoundBlock): Sound {
            val soundId = soundBlock.soundId
            val sound = SoundSoundPool(soundId)
            sound.conditionType = soundBlock.conditionType
            sound.ifConditionSatisfied = soundBlock.ifConditionSatisfied
            sound.musicId = MusicBuilder.currentMusicId
            sound.module = soundBlock.module
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
        override var volumeVariation = 0.8f
        override val minVolume = 0.2f

        override fun build(soundBlock: SoundBlock): Sound {
            val sound = SoundAudioTrack(soundBlock.soundShortArray)
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
}

class SoundSoundPool(private val soundId: Int) : Sound() {
    private val timer = Timer()
    override fun play(timer: TimelineTimer) {
        Log.i("playMoment", "$delayMillis, $soundId ")
        if (delayMillis > 0 && delayMillis < Int.MAX_VALUE)
            this.timer.schedule(object : TimerTask() {
                override fun run() {
                    if (musicId == MusicBuilder.currentMusicId && countLoops % module == 0 && ifConditionSatisfied) {
                        SoundManager.instance.play(soundId, volumeLeft, volumeRight)
                        Log.i("play", "$delayMillis, $soundId ")
                        //ProgrammingVibrator.vibrate((volume * 5).toLong())
                    }
                }
            }, delayMillis)
    }
}

class SoundAudioTrack(var soundShortArray: ShortArray) : Sound() {
    override fun play(timer: TimelineTimer) {
    }
}