package com.viana.soundprogramming.blocks

import android.graphics.Rect

class ModuleBlock : Block() {

    private var targetBlocks: List<ControllableBlock> = mutableListOf()

    override fun buildSyntaxIntersectionRect(): Rect {
        var rect = Rect()
        topCode?.let {
            val diameter = it.diameter
            val left = (centerX - diameter).toInt()
            val right = (centerX + diameter).toInt()
            val bottom = (centerY + diameter * 4).toInt()
            rect = Rect(left, top, right, bottom)
        }
        return rect
    }

    fun disableSomeBlocks(blocks: List<ControllableBlock>) {
        filterTargetBlocks(blocks)
        targetBlocks.forEach { active = (it.board?.timeline?.countLoops ?: 0 % 2 == 0) }
    }

    private fun filterTargetBlocks(blocks: List<ControllableBlock>) {
        targetBlocks = blocks.filter { Rect.intersects(intersectionRect, it.rect) }
    }
}
