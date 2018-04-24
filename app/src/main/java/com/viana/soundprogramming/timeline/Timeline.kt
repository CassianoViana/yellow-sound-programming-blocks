package com.viana.soundprogramming.timeline

import android.animation.ObjectAnimator
import android.app.Activity
import android.util.Log
import android.view.View
import android.view.animation.Animation
import com.viana.soundprogramming.StateMachine
import com.viana.soundprogramming.board.Board
import java.util.*

class Timeline(
        var board: Board,
        parent: Activity,
        timelineView: View,
        var count: Long = 0,
        var position: Float = 01F,
        val secondsToTraverseWidth: Double = 2.0
) : StateMachine.Listener {

    private val listeners = mutableListOf<Listener>()
    private var timer: Timer = Timer()
    private val insignificantMovement: Int = 15
    private val timelineAnimator = TimelineAnimator(parent, timelineView)

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
        Log.i("Timeline", "Timer scheduled")
        stopTimer()
        timer = Timer()
        val percentageToTraverse = (end - begin) / board.widthFloat
        val cycleInterval = ((secondsToTraverseWidth * percentageToTraverse / speedFactor) * 1000).toLong()
        if (cycleInterval > 0) {
            timelineAnimator.transition(position, end, cycleInterval)
            timer.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    position = begin
                    count++
                    listeners.forEach {
                        it.onHitStart()
                    }
                }
            }, 0, cycleInterval)
        }
    }

    private fun stopTimer() {
        timer.cancel()
        timer.purge()
    }

    fun addListener(listener: Listener) {
        listeners.add(listener)
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
        when (state) {
            StateMachine.State.PLAYING -> start()
            StateMachine.State.PAUSED -> stop()
            else -> {
            }
        }
    }
}

class TimelineAnimator(var parent: Activity, var timelineView: View) {

    private var animation: ObjectAnimator? = null

    init {
        this.timelineView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
    }

    fun transition(startX: Float, endX: Float, duration: Long) {
        parent.runOnUiThread({
            animation?.cancel()
            timelineView.x = startX
            animation = ObjectAnimator.ofFloat(timelineView, "translationX", endX)
            animation?.duration = duration
            animation?.repeatCount = Animation.INFINITE
            animation?.start()
        })
    }

}