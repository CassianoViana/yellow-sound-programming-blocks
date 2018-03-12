package com.viana.soundprogramming.board

import android.graphics.Canvas
import com.viana.soundprogramming.timeline.Timeline

interface Board {

    fun widthFloat(): Float
    fun heightFloat(): Float
    fun update()
    fun draw(canvas: Canvas?)
    fun timeline(): Timeline
}
