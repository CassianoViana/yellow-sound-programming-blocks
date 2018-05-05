package com.viana.soundprogramming.blocks

import android.graphics.Canvas
import android.graphics.Color
import com.viana.soundprogramming.board.Board
import com.viana.soundprogramming.core.MusicBuilder
import com.viana.soundprogramming.sound.Sound

open class SoundBlock(private val soundId: Int) : RepeatableBlock() {

    private var maxGlobalVolume = 1f
    private val minGlobalVolume = 0f
    private var variantVolume = 0.8f
    private val minVolume = 0.2f

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        paint.color = Color.BLUE
        paint.alpha = 100
        canvas?.drawRect(rect, paint)
    }

    fun buildSound(board: Board, musicBuilder: MusicBuilder): Sound {
        maxGlobalVolume = musicBuilder.maxVolume
        val soundId = this.soundId
        val sound = Sound(soundId)
        val volume = calculateVolumeByDiameter(musicBuilder)
        sound.volume = volume
        sound.volumeLeft = calculateVolumeLeft(board) * volume
        sound.volumeRight = calculateVolumeRight(board) * volume
        sound.delayMillis = calculatePlayMoment(board)
        return sound
    }

    private fun calculateVolumeLeft(board: Board): Float {
        val end = board.timeline?.end!!
        val begin = board.timeline?.begin!!
        val deltaBeginEnd = end - begin
        return (deltaBeginEnd - (centerX - begin)) / deltaBeginEnd
    }

    private fun calculateVolumeRight(board: Board): Float {
        val end = board.timeline?.end!!
        val begin = board.timeline?.begin!!
        val deltaBeginEnd = end - begin
        return (deltaBeginEnd - (end - centerX)) / deltaBeginEnd
    }

    override fun copy(): Block {
        val block = SoundBlock(soundId)
        fillWithProperties(block)
        return block
    }

    private fun calculateVolumeByDiameter(musicBuilder: MusicBuilder): Float {
        val maxDiameter = musicBuilder.maxSoundBlockDiameter
        val minDiameter = musicBuilder.minSoundBlockDiameter
        return (minVolume + variantVolume * ((diameter - minDiameter) / (maxDiameter - minDiameter))) * maxGlobalVolume
    }

    private fun calculateVolumeByDegree(board: Board): Float {
        var degree = this.degree
        degree += 180
        if (degree > 360)
            degree -= 360
        val volume = Math.abs(degree) / 360
        return volume * (maxGlobalVolume - minGlobalVolume)
    }

    private fun calculateByYposition(board: Board) =
            ((board.heightFloat - this.centerY) / board.heightFloat * (maxGlobalVolume - minGlobalVolume)) + minGlobalVolume

    private fun calculatePlayMoment(board: Board): Long =
            board.timeline?.let {
                (it.secondsToTraverseWidth / it.speedFactor * 1000 *
                        (this.centerX - it.begin) / board.widthFloat).toLong()
            } ?: 0
}