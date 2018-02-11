package com.viana.soundprogramming

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View

class AutoFitSurfaceView : SurfaceView, SurfaceHolder.Callback {

    private var mRatioWidth = 0
    private var mRatioHeight = 0
    private var circle: Drawable
    private val TAG = "AFSurfaceView"

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
            : super(context, attrs, defStyle) {
        holder.addCallback(this)
        circle = context.getDrawable(R.drawable.circle)
    }

    override fun surfaceCreated(surfaceHolder: SurfaceHolder?) {
        drawMyStuff(surfaceHolder)
    }

    override fun surfaceChanged(surfaceHolder: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {
        Log.i(TAG, "surfaceChanged")
    }

    override fun surfaceDestroyed(surfaceHolder: SurfaceHolder?) {
        Log.i(TAG, "surfaceDestroyed")
    }

    private fun drawMyStuff(surfaceHolder: SurfaceHolder?) {
        surfaceHolder ?: return
        Thread(Runnable {
            synchronized(surfaceHolder, {
                val lockCanvas = surfaceHolder.lockCanvas()
                lockCanvas?.let {
                    paint(it)
                    surfaceHolder.unlockCanvasAndPost(it)
                }

            })
        }).start()
    }

    private fun paint(canvas: Canvas) {
        val r = (Math.random() % 255).toInt()
        val g = (Math.random() % 255).toInt()
        val b = (Math.random() % 255).toInt()
        canvas.drawRGB(r, g, b)
    }

    fun teste() {
        drawMyStuff(holder)
    }

    fun setAspectRatio(width: Int, height: Int) {
        if (width < 0 || height < 0) {
            throw IllegalArgumentException("Size cannot be negative.")
        }
        mRatioWidth = width
        mRatioHeight = height
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = View.MeasureSpec.getSize(widthMeasureSpec)
        val height = View.MeasureSpec.getSize(heightMeasureSpec)
        if (0 == mRatioWidth || 0 == mRatioHeight) {
            setMeasuredDimension(width, height)
        } else {
            if (width < height * mRatioWidth / mRatioHeight) {
                setMeasuredDimension(width, width * mRatioHeight / mRatioWidth)
            } else {
                setMeasuredDimension(height * mRatioWidth / mRatioHeight, height)
            }
        }
    }
}
