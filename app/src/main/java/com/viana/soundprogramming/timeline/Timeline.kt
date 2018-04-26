package com.viana.soundprogramming.timeline

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
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
        val secondsToTraverseWidth: Double = 2.0
) : StateMachine.Listener {

    private val listeners = mutableListOf<Listener>()
    private var timer: Timer = Timer()
    private val insignificantMovement: Int = 15
    private val timelineAnimator = TimelineAnimator(parent, timelineView)

    private fun changingOrStarting(field: Float, value: Float, insignificantMovement: Int = 0) = (Math.abs(value - field) > insignificantMovement) && value > 0

    var begin: Float = 0f
        set(value) {
            val changingOrStarting = changingOrStarting(field, value, insignificantMovement)
            val resetting = field != 0f && value == 0f
            if (changingOrStarting || resetting) {
                field = value
                scheduleTimer()
            }
        }

    var end: Float = 1280f
        set(value) {
            val changingOrStarting = changingOrStarting(field, value, insignificantMovement)
            val resetting = field != board.widthFloat && value == board.widthFloat
            if (changingOrStarting || resetting) {
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
                if (stopping) timelineAnimator.stop()
            }
        }

    fun scheduleTimer() {
        Log.i("Timeline", "Timer scheduled")
        stopTimer()
        timer = Timer()
        val percentageToTraverse = (end - begin) / board.widthFloat
        val cycleInterval = ((secondsToTraverseWidth * percentageToTraverse / speedFactor) * 1000).toLong()
        if (cycleInterval > 0) {
            //timelineAnimator.transition(position, end, cycleInterval)
            timer.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    timelineAnimator.transition2(begin, end, cycleInterval)
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

class TimelineAnimator(
        private var parent: Activity,
        private var timelineView: View
) {

    private var animation: ObjectAnimator? = null

    fun transition(startX: Float, endX: Float, duration: Long) {
        timelineView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        timelineView.x = startX
        animation = ObjectAnimator.ofFloat(timelineView, "translationX", endX)
        animation?.duration = duration
        animation?.repeatCount = Animation.INFINITE
        parent.runOnUiThread({
            timelineView.visibility = View.VISIBLE
            animation?.start()
        })
    }

    fun transition2(startX: Float, endX: Float, durationx: Long) {
        parent.runOnUiThread({
            timelineView.visibility = View.VISIBLE
            ValueAnimator.ofFloat(startX, endX).apply {
                duration = durationx
                addUpdateListener {
                    timelineView.x = it.animatedValue as Float
                }
                start()
            }
        })
    }

    fun stop() {
        parent.runOnUiThread({
            timelineView.visibility = View.GONE
            animation?.cancel()
        })
    }


}