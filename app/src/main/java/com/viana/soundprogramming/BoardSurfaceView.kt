package com.viana.soundprogramming

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import topcodes.TopCode

class BoardSurfaceView
@JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : SurfaceView(context, attrs, defStyle), SurfaceHolder.Callback, TopCodesChangedListener, Board {

    private lateinit var mainThread: MainThread
    private var topCodes: List<TopCode>? = null
    private val timeline: Timeline
    private val paint: Paint
    private var canvas: Canvas? = null

    init {
        setBackgroundColor(Color.TRANSPARENT)
        holder.setFormat(PixelFormat.TRANSPARENT)
        holder.addCallback(this)
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        timeline = Timeline(this)
    }

    override fun heightFloat(): Float {
        return height.toFloat()
    }

    override fun widthFloat(): Float {
        return width.toFloat()
    }

    override fun topCodesChanged(topCodes: List<TopCode>) {
        this.topCodes = topCodes
    }

    override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
        mainThread = MainThread(surfaceHolder, this)
        mainThread.running = true
        mainThread.execute(null)
    }

    override fun surfaceChanged(surfaceHolder: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {
    }

    override fun surfaceDestroyed(p0: SurfaceHolder?) {
        mainThread.running = false
        mainThread.cancel(true)
    }

    override fun update() {
        timeline.update()
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        this.canvas = canvas
        clear()
        if (canvas != null) {
            drawTopCodes()
            timeline.draw(canvas)
        }
    }

    private fun clear() {
        canvas?.drawColor(0, PorterDuff.Mode.CLEAR)
    }

    private fun drawTopCodes() {
        topCodes?.forEach {
            it.draw(canvas)
        }
    }
}