package com.viana.soundprogramming

import android.content.Context
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import topcodes.TopCode

class BoardSurfaceView : SurfaceView, SurfaceHolder.Callback {

    private val TAG: String = "BoardView"
    var topCodes: MutableList<TopCode>? = null

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
            : super(context, attrs, defStyle) {
        holder.addCallback(this)
    }

    private fun startPaint(surfaceHolder: SurfaceHolder?) {
        surfaceHolder ?: return
        Thread(Runnable {
            while (true) {
                paint(surfaceHolder)
                Thread.sleep(50)
            }
        }).start()
    }

    override fun surfaceChanged(surfaceHolder: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {

    }

    override fun surfaceDestroyed(p0: SurfaceHolder?) {

    }

    override fun surfaceCreated(surfaceHolder: SurfaceHolder?) {
        startPaint(surfaceHolder)
    }

    private fun paint(surfaceHolder: SurfaceHolder) {
        val canvas = surfaceHolder.lockCanvas()
        canvas.drawARGB(100,255,255,255)
        topCodes?.forEach {
            it.draw(canvas)
        }
        surfaceHolder.unlockCanvasAndPost(canvas)
    }

}
