package com.viana.soundprogramming

import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.PorterDuff
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
        setBackgroundColor(Color.TRANSPARENT)
        holder.setFormat(PixelFormat.TRANSPARENT)
        holder.addCallback(this)
    }

    fun startPaint() {
        paint(holder)
    }

    override fun surfaceChanged(surfaceHolder: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {

    }

    override fun surfaceDestroyed(p0: SurfaceHolder?) {

    }

    override fun surfaceCreated(surfaceHolder: SurfaceHolder?) {
    }

    private fun paint(surfaceHolder: SurfaceHolder) {
        val canvas = surfaceHolder.lockCanvas()
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        topCodes?.forEach {
            it.draw(canvas)
        }
        surfaceHolder.unlockCanvasAndPost(canvas)
    }

}
