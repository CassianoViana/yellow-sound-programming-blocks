package com.viana.soundprogramming.blocks

import android.graphics.Rect

class CornerBlock : Block(), NotMovableBlock {

    enum class Type {
        TOP_LEFT, TOP_RIGHT
    }

    override fun buildSyntaxIntersectionRect(): Rect {
        var rect = Rect()
        topCode?.let {
            val diameter = it.diameter
            val left = (centerX - diameter).toInt()
            val right = (centerX + diameter * 23).toInt()
            val bottom = (centerY + diameter * 11).toInt()
            rect = Rect(left, top, right, bottom)
        }
        return rect
    }
}
