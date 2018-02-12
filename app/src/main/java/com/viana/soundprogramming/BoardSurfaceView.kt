package com.viana.soundprogramming

import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import topcodes.TopCode

class BoardSurfaceView : SurfaceView, SurfaceHolder.Callback, TopCodesChangedListener {

    private var topCodes: List<TopCode>? = null
    var surfaceHolder: SurfaceHolder? = null

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
            : super(context, attrs, defStyle) {
        setBackgroundColor(Color.TRANSPARENT)
        holder.setFormat(PixelFormat.TRANSPARENT)
        holder.addCallback(this)
    }

    override fun topCodesChanged(topCodes: List<TopCode>) {
        this.topCodes = topCodes
        paint()
    }

    override fun surfaceChanged(surfaceHolder: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {
        this.surfaceHolder = surfaceHolder
    }

    override fun surfaceDestroyed(p0: SurfaceHolder?) {

    }

    override fun surfaceCreated(surfaceHolder: SurfaceHolder?) {
        this.surfaceHolder = surfaceHolder
    }

    private fun paint() {
        val canvas = surfaceHolder?.lockCanvas()
        canvas?.drawColor(0, PorterDuff.Mode.CLEAR)
        topCodes?.forEach {
            it.draw(canvas)
        }
        surfaceHolder?.unlockCanvasAndPost(canvas)
    }

}

