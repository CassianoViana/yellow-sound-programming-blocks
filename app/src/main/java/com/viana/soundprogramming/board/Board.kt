package com.viana.soundprogramming.board

import android.graphics.Canvas
import com.viana.soundprogramming.blocks.Block
import com.viana.soundprogramming.timeline.Timeline

interface Board {

    var timeline: Timeline
    var widthFloat: Float
    var heightFloat: Float
    var blocks: List<Block>
    fun updateAndDraw()
    fun draw(canvas: Canvas)
}
