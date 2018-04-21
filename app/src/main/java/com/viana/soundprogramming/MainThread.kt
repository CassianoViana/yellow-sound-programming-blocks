package com.viana.soundprogramming

import android.os.AsyncTask
import com.viana.soundprogramming.board.Board

const val MAX_FPS = 30
var averageFps: Double = 0.0

class MainThread(
        private val board: Board
) : AsyncTask<Void, Void, Void>() {

    private var totalTime: Long = 0
    private var startTime: Long = 0
    private var timeMillis: Long = 0
    private var waitTime: Long = 0
    private var frameCount = 0
    private val targetTime = 1000 / MAX_FPS

    var running = false

    override fun doInBackground(vararg p0: Void?): Void? {
        while (running) {
            startTime = System.nanoTime()
            board.updateAndDraw()
            timeMillis = (System.nanoTime() - startTime) / 1000000
            waitIfNeed()
            increaseTotalTimeAndFrameCount()
            if (frameCount >= MAX_FPS)
                resetCounters()
        }
        return null
    }

    private fun resetCounters() {
        averageFps = (1000 / (totalTime / frameCount / 1000000)).toDouble()
        frameCount = 0
        totalTime = 0
        System.out.println(averageFps)
    }

    private fun increaseTotalTimeAndFrameCount() {
        totalTime += System.nanoTime() - startTime
        frameCount++
    }

    private fun waitIfNeed() {
        waitTime = targetTime - timeMillis
        if (waitTime > 0)
            Thread.sleep(waitTime)
    }

}