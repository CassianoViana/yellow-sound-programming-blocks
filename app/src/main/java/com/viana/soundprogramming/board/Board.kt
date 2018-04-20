package com.viana.soundprogramming.board

import android.graphics.Canvas
import android.os.Handler
import com.viana.soundprogramming.timeline.Timeline

interface Board {

    var timeline: Timeline
    var widthFloat: Float
    var heightFloat: Float
    var mHandler: Handler?
    fun update()
    fun draw(canvas: Canvas?)
}
