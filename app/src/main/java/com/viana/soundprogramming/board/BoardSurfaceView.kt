package com.viana.soundprogramming.board

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.viana.soundprogramming.MainThread
import com.viana.soundprogramming.blocks.Block
import com.viana.soundprogramming.blocks.BlocksManager
import com.viana.soundprogramming.timeline.CollisionDetector
import com.viana.soundprogramming.timeline.Timeline

class BoardSurfaceView
@JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : SurfaceView(context, attrs, defStyle), SurfaceHolder.Callback, Board, BlocksManager.Listener {

    override lateinit var timeline: Timeline
    private lateinit var mainThread: MainThread
    private val collisionDetector = CollisionDetector()
    private var canvas: Canvas? = null
    private var blocks = listOf<Block>()
    override var widthFloat = 0f
    override var heightFloat = 0f
    override var mHandler: Handler? = null

    init {
        setBackgroundColor(Color.TRANSPARENT)
        holder.setFormat(PixelFormat.TRANSPARENT)
        holder.addCallback(this)
        timeline = Timeline(this)
        heightFloat = height.toFloat()
        widthFloat = width.toFloat()
        prepareLooperListener()
    }

    private fun prepareLooperListener() {
        mHandler = Handler(Looper.getMainLooper()) {
            this.holder?.let { updateAndDraw(it) }
            true
        }
    }

    override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
        mainThread = MainThread(surfaceHolder, this)
        mainThread.running = true
        mainThread.execute(null)
    }

    override fun surfaceChanged(surfaceHolder: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {
        widthFloat = width.toFloat()
        heightFloat = height.toFloat()
    }

    override fun surfaceDestroyed(p0: SurfaceHolder?) {
        mainThread.running = false
        mainThread.cancel(true)
    }

    private fun updateAndDraw(surfaceHolder: SurfaceHolder) {
        val canvas = surfaceHolder.lockCanvas()
        try {
            canvas?.let {
                update()
                synchronized(surfaceHolder) {
                    draw(canvas)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            surfaceHolder.unlockCanvasAndPost(canvas)
        }
    }

    override fun update() {
        timeline.update()
        collisionDetector.detectCollision(timeline, blocks)
    }

    override fun draw(canvas: Canvas?) {
        try {
            super.draw(canvas)
            this.canvas = canvas
            clear()
            if (canvas != null) {
                drawBlocks()
                drawTimeline()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun clear() {
        canvas?.drawColor(0, PorterDuff.Mode.CLEAR)
    }

    private fun drawBlocks() {
        blocks.iterator().forEach { it.draw(canvas) }
    }

    private fun drawTimeline() {
        canvas?.let { timeline.draw(it) }
    }

    override fun updateBlocksList(blocks: List<Block>) {
        this.blocks = blocks
    }
}