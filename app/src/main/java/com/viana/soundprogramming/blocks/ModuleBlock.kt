package com.viana.soundprogramming.blocks

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect

class ModuleBlock(val module: Long) : Block() {

    val moduleRect = Rect()

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        val diameter = topCode?.diameter
        if (diameter != null) {
            moduleRect.set(left, top, (left + diameter * 5).toInt(), (top + diameter * 2).toInt())
        }
        paint.color = Color.CYAN
        paint.alpha = 50
        canvas?.drawRect(moduleRect, paint)
    }

    override fun intersects(block: Block): Boolean {
        return Rect.intersects(moduleRect, block.rect)
    }

}