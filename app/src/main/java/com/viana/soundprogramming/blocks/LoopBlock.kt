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
        val copiedBlocks = mutableListOf<Block>()
        while (++i < 3) {
            copiedBlocks.addAll(coveredBlocks.map {
                val distFromThisBlock = Math.abs(it.centerX - LoopBlock@ centerX)
                it.copy().apply {
                    move(centerX + i * distFromThisBlock, centerY)
                }
            })
        }
        return copiedBlocks
    }

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
