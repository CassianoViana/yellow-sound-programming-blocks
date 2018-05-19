package com.viana.soundprogramming.board

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import com.viana.soundprogramming.blocks.Block
import com.viana.soundprogramming.blocks.BlocksManager
import com.viana.soundprogramming.timeline.Timeline

class BoardSurfaceView
@JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : SurfaceView(context, attrs, defStyle),
        SurfaceHolder.Callback,
        Board,
        BlocksManager.Listener {

    override var timeline: Timeline? = null
    override var widthFloat = 0f
    override var heightFloat = 0f
    override var blocks: List<Block> = listOf()

    init {
        setBackgroundColor(Color.TRANSPARENT)
        holder.setFormat(PixelFormat.TRANSPARENT)
        holder.addCallback(this)
        heightFloat = height.toFloat()
        widthFloat = width.toFloat()
    }

    fun prepare(parent: Activity, timelineView: View) {
        timeline = Timeline(this, parent, timelineView)
    }

    fun start() {
        timeline?.scheduleTimer()
        timeline?.start()
    }

    fun stop() {
        timeline?.stop()
    }

    override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
    }

    override fun surfaceChanged(surfaceHolder: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {
        widthFloat = width.toFloat()
        heightFloat = height.toFloat()
    }

    override fun surfaceDestroyed(p0: SurfaceHolder?) {
    }

    override fun updateAndDraw() {
        val canvas = holder.lockCanvas()
        try {
            draw(canvas)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            holder.unlockCanvasAndPost(canvas)
        }
    }

    override fun draw(canvas: Canvas) {
        try {
            Log.i("Board", "draw")
            super.draw(canvas)
            clear(canvas)
            drawBlocks(canvas, blocks)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun clear(canvas: Canvas) {
        canvas.drawColor(0, PorterDuff.Mode.CLEAR)
    }

    private fun drawBlocks(canvas: Canvas, blocks: List<Block>) {
        blocks.forEach { it.draw(canvas) }
    }

    override fun updateBlocksList(blocks: List<Block>) {
        this.blocks = blocks
        updateAndDraw()
    }
}