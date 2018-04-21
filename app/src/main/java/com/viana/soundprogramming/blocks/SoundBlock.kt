package com.viana.soundprogramming.blocks

import android.graphics.Canvas
import android.graphics.Color
import com.viana.soundprogramming.board.Board
import com.viana.soundprogramming.core.MusicBuilder
import com.viana.soundprogramming.sound.Sound

open class SoundBlock(private val soundId: Int) : Block() {

    private var maxVolume = 1f
    private val minVolume = 0f

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        paint.color = Color.BLUE
        paint.alpha = 100
        canvas?.drawRect(rect, paint)
    }

    fun buildSound(board: Board, musicBuilder: MusicBuilder): Sound {
        maxVolume = musicBuilder.maxVolume
        val soundId = this.soundId
        val sound = Sound(soundId)
        val volume = calculateVolume(board)
        sound.volume = volume
        sound.delayMillis = calculatePlayMoment(board)
        return sound
    }

    private fun calculateVolume(board: Board) =
            ((board.heightFloat - this.centerY) * (maxVolume - minVolume) / board.heightFloat) + minVolume

    private fun calculatePlayMoment(board: Board) =
            (board.timeline.secondsToTraverseWidth / board.timeline.speedFactor * 1000 *
                    (this.centerX - board.timeline.begin) / board.widthFloat).toLong()
}