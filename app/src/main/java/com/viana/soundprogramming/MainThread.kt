package com.viana.soundprogramming

import android.graphics.Canvas
import android.os.AsyncTask
import android.view.SurfaceHolder
import com.viana.soundprogramming.board.Board

const val MAX_FPS = 30
var averageFps: Double = 0.0

class MainThread(
        private val surfaceHolder: SurfaceHolder,
        private val board: Board
) : AsyncTask<Void, Void, Void>() {

    private var totalTime: Long = 0
    private var startTime: Long = 0
    private var timeMillis: Long = 0
    private var waitTime: Long = 0
    private var frameCount = 0
    private val targetTime = 1000 / MAX_FPS

    var running = false

    companion object {
        var canvas: Canvas? = null
    }

    override fun doInBackground(vararg p0: Void?): Void? {
        while (running) {
            startTime = System.nanoTime()
            canvas = null
            try {
                updateAndDraw()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                unlockCanvas()
            }
            timeMillis = (System.nanoTime() - startTime) / 1000000
            waitIfNeed()
            increaseTotalTimeAndFrameCount()
            if (frameCount >= MAX_FPS)
                resetCounters()
        }
        return null
    }

    private fun updateAndDraw() {
        canvas = surfaceHolder.lockCanvas()
        synchronized(surfaceHolder) {
            board.update()
            board.draw(canvas)
        }
    }

    private fun unlockCanvas() {
        if (canvas != null)
            surfaceHolder.unlockCanvasAndPost(canvas)
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