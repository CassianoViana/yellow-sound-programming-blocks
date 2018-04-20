package com.viana.soundprogramming.timeline

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import com.viana.soundprogramming.StateMachine
import com.viana.soundprogramming.averageFps
import com.viana.soundprogramming.blocks.Block
import com.viana.soundprogramming.board.Board
import com.viana.soundprogramming.board.BoardObject

class Timeline(
        var board: Board,
        var count: Long = 0,
        var end: Float = 1280f,
        var begin: Float = 0f,
        var position: Float =01F,
        var speedFactor: Float = 1F,
        var speed: Float = 0.0f,
        val secondsToTraverseWidth: Double = 2.0
) : BoardObject, StateMachine.Listener {

    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var rect: Rect = Rect()
    private val listeners = mutableListOf<Listener>()


    override fun update() {
        calculateSpeed()
        updatePosition()
        updateRectBounds()
        controlReachTheEnd()
    }

    private fun calculateSpeed() {
        speed = (board.widthFloat / averageFps / secondsToTraverseWidth)
                .toFloat() * speedFactor
    }

    private fun updatePosition() {
        position += speed
    }

    private fun updateRectBounds() {
        val left = position.toInt()
        val top = 0
        val right = (position + speed).toInt()
        val bottom = board.heightFloat.toInt()
        rect.set(left, top, right, bottom)
    }

    override fun draw(canvas: Canvas) {
        paint.color = Color.YELLOW
        canvas.drawRect(rect, paint)
    }

    private fun controlReachTheEnd() {
        if (position >= end) {
            count++
            position = begin
            listeners.forEach { it.onHitStart() }
        }
    }

    fun addListener(listener: Listener) {
        listeners.add(listener)
    }

    fun intersects(block: Block): Boolean {
        return Rect.intersects(rect, block.centerPoint())
    }

    interface Listener {
        fun onHitStart()
    }

    fun resetBegin() {
        begin = 0f
    }

    fun resetEnd() {
        end = board.widthFloat
    }

    fun start() {
        speedFactor = 1F
    }

    fun stop() {
        speedFactor = 0F
    }

    override fun stateChanged(state: StateMachine.State) {
        when(state){
            StateMachine.State.PLAYING -> start()
            StateMachine.State.PAUSED -> stop()
            else -> {
            }
        }
    }
}