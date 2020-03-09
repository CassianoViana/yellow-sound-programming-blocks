package com.viana.soundprogramming.board

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.viana.soundprogramming.blocks.Block
import com.viana.soundprogramming.blocks.BlocksManager
import com.viana.soundprogramming.timeline.Timeline
import com.viana.soundprogramming.timeline.TimelineTimer

class BoardSurfaceView
@JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : SurfaceView(context, attrs, defStyle),
        SurfaceHolder.Callback,
        Board,
        BlocksManager.Listener {

    override lateinit var timeline: Timeline
    override var widthFloat = 0f
    override var heightFloat = 0f
    override var blocks: List<Block> = listOf()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        setBackgroundColor(Color.TRANSPARENT)
        holder.setFormat(PixelFormat.TRANSPARENT)
        holder.addCallback(this)
        heightFloat = height.toFloat()
        widthFloat = width.toFloat()
    }

    private var raiaIndex: Int = 0

    fun prepare() {
        timeline = Timeline(this)
        timeline.addListener(object : Timeline.Listener {
            override fun onHitStart(timelineTimer: TimelineTimer, index: Int) {
                raiaIndex = index
                updateAndDraw()
            }
        })
    }

    fun start() {
        timeline.scheduleTimer()
        timeline.start()
    }

    fun stop() {
        timeline.stop()
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
            super.draw(canvas)
            clear(canvas)
            drawBlocks(canvas, blocks)
            drawTimelineRange(canvas)
            drawRaia(canvas, raiaIndex)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun drawRaia(canvas: Canvas, raiaIndex: Int) {
        val raiaWidth = timeline.raiaWidth().toInt()
        val left = timeline.begin + raiaIndex * raiaWidth
        val right = left + raiaWidth
        val rect = Rect(left.toInt(), 0, right.toInt(), height)
        paint.color = Color.MAGENTA
        paint.alpha = 50
        canvas.drawRect(rect, paint)
    }

    private fun clear(canvas: Canvas) {
        canvas.drawColor(0, PorterDuff.Mode.CLEAR)
    }

    private fun drawBlocks(canvas: Canvas, blocks: List<Block>) {
        blocks.forEach { it.draw(canvas) }
    }

    private fun drawTimelineRange(canvas: Canvas) {
        paint.color = Color.RED
        paint.alpha = 30
        canvas.drawRect(Rect(timeline.begin.toInt(), 0, timeline.end.toInt(), height), paint)
    }

    override fun updateBlocksList(blocks: List<Block>) {
        this.blocks = blocks
        updateAndDraw()
    }
}