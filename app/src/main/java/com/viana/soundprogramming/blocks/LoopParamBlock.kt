package com.viana.soundprogramming.blocks

import android.graphics.Rect
import com.viana.soundprogramming.exceptions.TwoNumberBlocksConcatenatedError

class LoopParamBlock(var numberOfRepetitions: Byte) : Block() {

    constructor() : this(1)

    override fun fillWithProperties(block: Block) {
        super.fillWithProperties(block)
        (block as LoopParamBlock).numberOfRepetitions = numberOfRepetitions
    }

    override fun buildSyntaxIntersectionRect(): Rect {
        var rect = Rect()
        topCode?.let {
            val diameter = it.diameter
            val left = (centerX - diameter).toInt()
            val right = (centerX + diameter).toInt()
            val bottom = (centerY + diameter * 2).toInt()
            rect = Rect(left, top, right, bottom)
        }
        return rect
    }

    override fun validate(blocks: List<Block>) {
        blocks.filterIsInstance(LoopParamBlock::class.java).filter { it != this }.forEach {
            if (intersectionRect.intersect(it.rect)) {
                throw TwoNumberBlocksConcatenatedError()
            }
        }
    }

}
