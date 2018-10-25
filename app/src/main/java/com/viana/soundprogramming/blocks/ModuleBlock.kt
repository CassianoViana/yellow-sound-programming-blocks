package com.viana.soundprogramming.blocks

import android.graphics.Rect

class ModuleBlock : Block() {

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

    fun affect(loopParamBlocks: List<LoopParamBlock>, repeatableBlocks: List<ControllableBlock>) {
        val loopParamsAffected = repeatableBlocks.filter { Rect.intersects(intersectionRect, it.rect) }

        loopParamBlocks.forEach { loopParamBlock ->
            loopParamsAffected.filter {
                Rect.intersects(loopParamBlock.intersectionRect, it.rect)
            }.forEach {
                it.playOnEachXLoops = loopParamBlock.numberOfRepetitions
            }
        }

    }
}
