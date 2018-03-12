package com.viana.soundprogramming.board

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.viana.soundprogramming.timeline.CollisionDetector
import com.viana.soundprogramming.MainThread
import com.viana.soundprogramming.timeline.Timeline
import com.viana.soundprogramming.blocks.Block
import com.viana.soundprogramming.blocks.BlocksManager

class BoardSurfaceView
@JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : SurfaceView(context, attrs, defStyle), SurfaceHolder.Callback, Board {

    private lateinit var mainThread: MainThread

    private val paint: Paint
    private var canvas: Canvas? = null

    private val timeLine: Timeline
    override fun timeline() = this.timeLine

    private val collisionDetector = CollisionDetector.instance
    val blocksManager = BlocksManager.instance

    private val blocks: List<Block>
        get() = blocksManager.blocks

    init {
        setBackgroundColor(Color.TRANSPARENT)
        holder.setFormat(PixelFormat.TRANSPARENT)
        holder.addCallback(this)
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        timeLine = Timeline(this)
    }

    override fun heightFloat(): Float = height.toFloat()
    override fun widthFloat(): Float = width.toFloat()

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
        timeLine.update()
        collisionDetector.detectCollision(timeLine, blocks)
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        this.canvas = canvas
        clear()
        if (canvas != null) {
            drawBlocks()
            drawTimeline()
        }
    }

    private fun clear() {
        canvas?.drawColor(0, PorterDuff.Mode.CLEAR)
    }

    private fun drawBlocks() {
        blocks.forEach { it.draw(canvas) }
    }

    private fun drawTimeline() {
        canvas?.let { timeLine.draw(it) }
    }
}