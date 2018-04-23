package com.viana.soundprogramming.timeline

import android.animation.ObjectAnimator
import android.app.Activity
import android.graphics.Canvas
import android.view.View
import com.viana.soundprogramming.StateMachine
import com.viana.soundprogramming.blocks.Block
import com.viana.soundprogramming.board.Board
import com.viana.soundprogramming.board.BoardObject
import java.util.*

class Timeline(
        var board: Board,
        var parent: Activity,
        var timelineView: View,
        var count: Long = 0,
        var position: Float = 01F,
        var speed: Float = 0.0f,
        val secondsToTraverseWidth: Double = 2.0
) : BoardObject, StateMachine.Listener {

    private val listeners = mutableListOf<Listener>()
    private var timer: Timer = Timer()
    private val insignificantMovement: Int = 15

    private fun changingOrStarting(field: Float, value: Float, insignificantMovement: Int = 0) = (Math.abs(value - field) > insignificantMovement) && value > 0

    var begin: Float = 0f
        set(value) {
            if (changingOrStarting(field, value, insignificantMovement)) {
                field = value
                scheduleTimer()
            }
        }

    var end: Float = 1280f
        set(value) {
            val changingOrStarting = changingOrStarting(field, value, insignificantMovement)
            if (changingOrStarting) {
                field = value
                scheduleTimer()
            }
        }

    var speedFactor: Float = 1.00F
        set(value) {
            val changingOrStarting = changingOrStarting(field, value)
            val stopping = field != 0f && value == 0f
            if (changingOrStarting || stopping) {
                field = value
                when {
                    stopping -> stopTimer()
                    else -> scheduleTimer()
                }
            }
        }

    fun scheduleTimer() {
        stopTimer()
        timer = Timer()
        val percentageToTraverse = (end - begin) / board.widthFloat
        val cycleInterval = ((secondsToTraverseWidth * percentageToTraverse / speedFactor) * 1000).toLong()
        if (cycleInterval > 0)
            timer.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    position = begin
                    count++
                    translate(position, end, cycleInterval)
                    listeners.forEach {
                        it.onHitStart()
                    }
                }
            }, 0, cycleInterval)
    }

    private fun stopTimer() {
        timer.cancel()
        timer.purge()
    }

    private fun translate(startX: Float, endX: Float, duration: Long) {
        parent.runOnUiThread({
            timelineView.x = startX
            val animation = ObjectAnimator.ofFloat(timelineView, "translationX", endX)
            animation.duration = duration
            animation.start()
        })
    }

    override fun update() {

    }

    override fun draw(canvas: Canvas) {

    }

    fun addListener(listener: Listener) {
        listeners.add(listener)
    }

    fun intersects(block: Block): Boolean = false

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
        when (state) {
            StateMachine.State.PLAYING -> start()
            StateMachine.State.PAUSED -> stop()
            else -> {
            }
        }
    }
}