package com.viana.soundprogramming.blocks

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect

class LoopBlock : Block() {

    private val intersectionRectBuilder: IntersectionRectBuilder = IntersectionRectBuilderFixed()

    private val intersectionRect: Rect
        get() {
            return intersectionRectBuilder.build(this)
        }

    fun repeatBlocks(blocks: List<RepeatableBlock>): List<Block> {
        val coveredBlocks = blocks.filter { Rect.intersects(intersectionRect, it.rect) }
        var i = 0
        val repeatingBlocks = mutableListOf<Block>()
        val nearestOfLoopBlock: RepeatableBlock? = coveredBlocks.minBy { block -> distFromLoopBlock(block) }
        val fartherOfLoopBlock: RepeatableBlock? = coveredBlocks.maxBy { block -> distFromLoopBlock(block) }
        if (nearestOfLoopBlock != null && fartherOfLoopBlock != null) {
            val nearestDistFromLoopBlock = distFromLoopBlock(nearestOfLoopBlock)
            val x = nearestDistFromLoopBlock + Math.abs(fartherOfLoopBlock.centerX - nearestOfLoopBlock.centerX)
            while (++i < 3) {
                repeatingBlocks.addAll(coveredBlocks.map { repeatableBlock ->
                    repeatableBlock.copy().apply {
                        move(repeatableBlock.centerX + i * x, centerY)
                    }
                })
            }
        }
        return repeatingBlocks
    }

    private fun distFromLoopBlock(block: RepeatableBlock) =
            Math.abs(block.centerX - LoopBlock@ centerX)

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        val diameter = topCode?.diameter
        if (diameter != null) {
            intersectionRect.set(left, top, (left + diameter * 5).toInt(), (top + diameter * 3).toInt())
        }
        paint.color = Color.CYAN
        paint.alpha = 50
        canvas?.drawRect(intersectionRect, paint)
    }

}

interface IntersectionRectBuilder {
    fun build(loopBlock: LoopBlock): Rect
}

class IntersectionRectBuilderFixed : IntersectionRectBuilder {

    override fun build(loopBlock: LoopBlock): Rect {
        val width = 5
        val height = 3
        var rectIntersection: Rect? = null
        loopBlock.apply {
            topCode?.let {
                val diameter = it.diameter
                val top = (top - diameter * 1).toInt()
                val right = (left + diameter * width).toInt()
                val bottom = (top + diameter * height).toInt()
                rectIntersection = Rect(left, top, right, bottom)
            }
        }
        return rectIntersection ?: Rect(0, 0, 0, 0)
    }
}
