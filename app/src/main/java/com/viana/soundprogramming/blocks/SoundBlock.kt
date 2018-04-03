package com.viana.soundprogramming.blocks

import android.graphics.Canvas
import android.graphics.Color

open class SoundBlock(val soundId: Int) : Block() {

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        paint.color = Color.BLUE
        paint.alpha = 100
        canvas?.drawRect(rect, paint)
    }
}