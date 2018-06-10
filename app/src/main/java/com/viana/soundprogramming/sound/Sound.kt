package com.viana.soundprogramming.sound

import com.viana.soundprogramming.blocks.PresenceBlock
import com.viana.soundprogramming.blocks.SoundBlock
import com.viana.soundprogramming.board.Board
import com.viana.soundprogramming.core.MusicBuilder
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
    var index: Int = 0

    abstract fun play()

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

        fun calculateVolumeByY(soundBlock: SoundBlock): Float {
            val diffBoardHeight: Int = Math.abs(musicBuilder.maxY - musicBuilder.minY)
            var diffBlockYFromTop = soundBlock.centerY - musicBuilder.minY
            if (diffBlockYFromTop < 20) diffBlockYFromTop = 0
            return (minVolume + volumeVariation * diffBlockYFromTop / diffBoardHeight) * musicBuilder.maxVolume
        }

        fun calculatePlayMoment(soundBlock: SoundBlock): Long =
                musicBoard.timeline.let {
                    (it.secondsToTraverseWidth / it.speedFactor * 1000 *
                            (soundBlock.centerX - it.begin) / musicBoard.widthFloat).toLong()
                }

        fun calculatePlayIndex(soundBlock: SoundBlock): Int {
            val raiaWidth = musicBoard.timeline.raiaWidth()
            val begin = musicBoard.timeline.begin
            var i = 0
            while (i < 8) {
                if (soundBlock.centerX > begin + i * raiaWidth && soundBlock.centerX < begin + (i + 1) * raiaWidth) {
                    return i
                }
                i++
            }
            return 0
        }
    }

    class SoundPoolSoundBuilder(override var musicBuilder: MusicBuilder) : Builder {

        override var musicBoard: Board = musicBuilder.board
        override var volumeVariation = 1.0f
        override val minVolume = 0.0f

        override fun build(soundBlock: SoundBlock): Sound {
            val soundId = soundBlock.soundId
            val sound = SoundSoundPool(soundId)
            sound.conditionType = soundBlock.conditionType
            sound.ifConditionSatisfied = soundBlock.ifConditionSatisfied
            sound.musicId = MusicBuilder.currentMusicId
            sound.module = soundBlock.module
            val volume = calculateVolumeByY(soundBlock)
            sound.volume = volume
            if (musicBuilder.isWiredHeadsetOn()) {
                sound.volumeLeft = calculateVolumeLeft(soundBlock) * volume
                sound.volumeRight = calculateVolumeRight(soundBlock) * volume
            } else {
                sound.volumeLeft = volume
                sound.volumeRight = volume
            }
            //sound.delayMillis = calculatePlayMoment(soundBlock)
            sound.index = calculatePlayIndex(soundBlock)
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
            val volume = calculateVolumeByY(soundBlock)
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
    override fun play() {
        //musicId == MusicBuilder.currentMusicId &&
        if (countLoops % module == 0 && ifConditionSatisfied) {
            SoundManager.instance.play(soundId, volumeLeft, volumeRight)
            //ProgrammingVibrator.vibrate((volume * 5).toLong())
        }
    }
}

class SoundAudioTrack(var soundShortArray: ShortArray) : Sound() {
    override fun play() {
    }
}