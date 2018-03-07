package com.viana.soundprogramming

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

class Timeline(private var board: Board) : BoardObject {

    private var xposition: Float = 0F
    private var speed: Float = 0.0f
    private var strokeWidth: Float = 5F
    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun update() {
        speed = (board.widthFloat() / averageFps).toFloat()
        strokeWidth = speed
        xposition += speed
        if (xposition >= board.widthFloat())
            xposition = 0F
    }

    override fun draw(canvas: Canvas) {
        paint.color = Color.RED
        paint.strokeWidth = strokeWidth
        canvas.drawLine(xposition, 0F, xposition, board.heightFloat(), paint)
    }
}