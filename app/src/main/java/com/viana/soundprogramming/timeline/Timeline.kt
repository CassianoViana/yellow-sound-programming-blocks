package com.viana.soundprogramming.timeline

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import com.viana.soundprogramming.averageFps
import com.viana.soundprogramming.blocks.Block
import com.viana.soundprogramming.board.Board
import com.viana.soundprogramming.board.BoardObject

class Timeline(private var board: Board) : BoardObject {

    private var xposition: Float = 0F
    private var speed: Float = 0.0f
    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var rect: Rect = Rect()
    private val secondsToTraverseWidth = 3
    private val begin = 30f
    private val end = 600f

    override fun update() {
        speed = (board.widthFloat() / averageFps / secondsToTraverseWidth)
                .toFloat()
        xposition += speed
        val left = xposition.toInt()
        val top = 0
        val right = (xposition + speed).toInt()
        val bottom = board.heightFloat().toInt()
        rect.set(left, top, right, bottom)
        if (xposition >= end)
            xposition = begin
    }

    override fun draw(canvas: Canvas) {
        paint.color = Color.GREEN
        canvas.drawRect(rect, paint)
    }

    fun intersects(block: Block): Boolean {
        return rect.intersects(block.left, block.top, block.right, block.bottom)
    }
}