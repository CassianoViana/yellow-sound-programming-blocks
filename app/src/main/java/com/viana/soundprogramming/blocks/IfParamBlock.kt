package com.viana.soundprogramming.blocks

import android.graphics.Rect


class IfParamBlock(var type: Type) : Block() {
    enum class Type {
        HAS_STAR, HAS_CIRCLE, HAS_SQUARE
    }

    constructor() : this(Type.HAS_CIRCLE)

    override fun fillWithProperties(block: Block) {
        super.fillWithProperties(block)
        (block as IfParamBlock).type = type
    }

    override fun buildSyntaxIntersectionRect(): Rect {
        var rect = Rect()
        topCode?.let {
            val diameter = it.diameter
            val left = (centerX - diameter).toInt()
            val right = (centerX + diameter).toInt()
            val bottom = (centerY + diameter * 3).toInt()
            rect = Rect(left, top, right, bottom)
        }
        return rect
    }

    fun filterTargets(ifTargetBlocks: List<ControllableBlock>): List<ControllableBlock> {
        return ifTargetBlocks.filter { Rect.intersects(intersectionRect, it.rect) }
    }
}