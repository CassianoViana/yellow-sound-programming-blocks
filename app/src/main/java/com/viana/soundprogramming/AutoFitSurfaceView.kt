package com.viana.soundprogramming

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.AsyncTask
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
    private var worker: AsyncTask<SurfaceHolder, Int, Void>

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
            : super(context, attrs, defStyle) {
        holder.addCallback(this)
        circle = context.getDrawable(R.drawable.circle)
        worker = DesignerWorker()
    }

    private class DesignerWorker : AsyncTask<SurfaceHolder, Int, Void>() {
        override fun doInBackground(vararg p0: SurfaceHolder?): Void? {
            val surfaceHolder = p0[0]
            surfaceHolder?.let {
                val canvas = surfaceHolder.lockCanvas()
                paint(canvas)
                surfaceHolder.unlockCanvasAndPost(canvas)
            }
            return null
        }

        private fun paint(canvas: Canvas) {
            val r = (Math.random() % 255).toInt()
            val g = (Math.random() % 255).toInt()
            val b = (Math.random() % 255).toInt()
            canvas.drawRGB(r, g, b)
        }
    }

    override fun surfaceCreated(surfaceHolder: SurfaceHolder?) {
        drawMyStuff(surfaceHolder)
    }

    override fun surfaceChanged(surfaceHolder: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {
        Log.i(TAG, "surfaceChanged")
        drawMyStuff(surfaceHolder)
    }

    override fun surfaceDestroyed(surfaceHolder: SurfaceHolder?) {
        Log.i(TAG, "surfaceDestroyed")
        drawMyStuff(surfaceHolder)
    }

    private fun drawMyStuff(surfaceHolder: SurfaceHolder?) {
        if (worker.status != AsyncTask.Status.RUNNING)
            worker.execute(surfaceHolder)
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
