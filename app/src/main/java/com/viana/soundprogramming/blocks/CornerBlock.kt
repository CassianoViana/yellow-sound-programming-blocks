package com.viana.soundprogramming.blocks

import android.graphics.Rect

class CornerBlock : Block(), NotMovableBlock {

    var positions = mutableListOf<Corner>()

    fun setPositions(vararg positions: Corner): CornerBlock {
        this.positions = positions.toMutableList()
        return this
    }

    enum class Corner {
        TOP, BOTTOM, LEFT, RIGHT
    }

    override fun buildSyntaxIntersectionRect(): Rect {
        var rect = Rect()
        /*topCode?.let {
            val diameter = it.diameter
            val left = (centerX - diameter).toInt()
            val right = (centerX + diameter * 23).toInt()
            val bottom = (centerY + diameter * 11).toInt()
            rect = Rect(left, top, right, bottom)
        }*/
        return rect
    }

    override fun copy(): Block {
        val block = CornerBlock()
        fillWithProperties(block)
        return block
    }

    override fun fillWithProperties(block: Block) {
        (block as CornerBlock).positions = positions
        super.fillWithProperties(block)
    }
}
