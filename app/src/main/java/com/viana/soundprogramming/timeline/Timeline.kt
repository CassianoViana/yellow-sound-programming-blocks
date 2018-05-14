package com.viana.soundprogramming.timeline

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.util.Log
import android.view.View
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
    var timer: TimelineTimer = TimelineTimer()
    private val timelineAnimator = TimelineAnimatorValueAnimator(parent, timelineView)

    var begin: Float = 0f
        set(value) {
            field = value
            scheduleTimer()
        }

    var end: Float = 1280f
        set(value) {
            field = value
            scheduleTimer()
        }

    var speedFactor: Float = 1.00F
        set(value) {
            field = value
            if (field == 0f) {
                stopTimer()
                timelineAnimator.stop()
            } else {
                scheduleTimer()
            }
        }

    fun scheduleTimer() {
        Log.i("Timeline", "Timer scheduled")
        stopTimer()
        timer = TimelineTimer()
        val percentageToTraverse = (end - begin) / board.widthFloat
        val cycleInterval = ((secondsToTraverseWidth * percentageToTraverse / speedFactor) * 1000).toLong()
        if (cycleInterval > 0) {
            timer.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    timelineAnimator.transition(begin, end, cycleInterval)
                    listeners.forEach {
                        it.onHitStart(timer)
                    }
                }
            }, 0, cycleInterval)
        }
    }

    private fun stopTimer() {
        timer.cancel()
    }

    fun addListener(listener: Listener) {
        listeners.add(listener)
    }

    interface Listener {
        fun onHitStart(timelineTimer: TimelineTimer)
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
            StateMachine.State.RECORDING -> stop()
            else -> {
            }
        }
    }
}

abstract class TimelineAnimator(
        var parent: Activity,
        var timelineView: View
) {

    var animation: ObjectAnimator? = null

    abstract fun transition(startX: Float, endX: Float, duration: Long);

    fun stop() {
        parent.runOnUiThread({
            timelineView.visibility = View.GONE
            animation?.cancel()
        })
    }
}

class TimelineAnimatorObjectAnimator(parent: Activity, timelineView: View) : TimelineAnimator(parent, timelineView) {
    override fun transition(startX: Float, endX: Float, duration: Long) {
        animation = ObjectAnimator.ofFloat(timelineView, "translationX", endX)
        animation?.duration = duration
        parent.runOnUiThread({
            timelineView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
            timelineView.x = startX
            timelineView.visibility = View.VISIBLE
            animation?.start()
        })
    }

}

class TimelineAnimatorValueAnimator(parent: Activity, timelineView: View) : TimelineAnimator(parent, timelineView) {
    override fun transition(startX: Float, endX: Float, duration: Long) {
        parent.runOnUiThread({
            timelineView.visibility = View.VISIBLE
            ValueAnimator.ofFloat(startX, endX).apply {
                ValueAnimator@ this.duration = duration
                addUpdateListener {
                    timelineView.x = it.animatedValue as Float
                }
                start()
            }
        })
    }
}

class TimelineTimer {
    var cancelled: Boolean = false
    private val timer: Timer = Timer()

    fun scheduleAtFixedRate(timerTask: TimerTask, i: Long, cycleInterval: Long) {
        timer.scheduleAtFixedRate(timerTask, i, cycleInterval)
    }

    fun cancel() {
        timer.cancel()
        timer.purge()
        this.cancelled = true
    }
}