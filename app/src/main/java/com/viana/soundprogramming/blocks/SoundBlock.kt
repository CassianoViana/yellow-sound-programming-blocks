package com.viana.soundprogramming.blocks

import android.graphics.Canvas
import android.graphics.Color
import com.viana.soundprogramming.board.Board
import com.viana.soundprogramming.sound.Sound

open class SoundBlock(val soundId: Int) : Block() {

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        paint.color = Color.BLUE
        paint.alpha = 100
        canvas?.drawRect(rect, paint)
    }

    fun buildSound(board: Board): Sound {
        val soundId = this.soundId
        val sound = Sound(soundId)
        val volume = calculateVolume(board)
        sound.volume = volume
        sound.delayMillis = calculatePlayMoment(board)
        return sound
    }

    val max = 1 // volume
    val min = 0
    private fun calculateVolume(board: Board) =
            ((board.heightFloat - this.centerY) * (max - min) / board.heightFloat) + min

    private fun calculatePlayMoment(board: Board) =
            (board.timeline.secondsToTraverseWidth / board.timeline.speedFactor * 1000 *
                    (this.centerX - board.timeline.begin) / board.widthFloat).toLong()
}