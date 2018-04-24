package com.viana.soundprogramming.board

import android.graphics.Canvas
import com.viana.soundprogramming.timeline.Timeline

interface Board {

    var timeline: Timeline?
    var widthFloat: Float
    var heightFloat: Float
    fun updateAndDraw()
    fun draw(canvas: Canvas)
}
