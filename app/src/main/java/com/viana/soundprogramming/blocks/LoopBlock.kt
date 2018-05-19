package com.viana.soundprogramming.blocks

import android.graphics.Canvas
import android.graphics.Rect
import com.viana.soundprogramming.exceptions.LoopBlockNeedsParamError

class LoopBlock : Block() {

    private var targetBlocks: List<ControllableBlock> = mutableListOf()
    private var numberOfRepetitions: Byte = 0

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

    fun repeatBlocks(loopTargetBlocks: List<ControllableBlock>, loopParamBlocks: List<LoopParamBlock>): List<Block> {
        filterTargetBlocks(loopTargetBlocks)
        calculateNumberOfRepetitions(loopParamBlocks)
        return buildClones(loopTargetBlocks)
    }

    private fun filterTargetBlocks(blocks: List<ControllableBlock>) {
        targetBlocks = blocks.filter { Rect.intersects(intersectionRect, it.rect) }
    }

    private fun calculateNumberOfRepetitions(loopParamBlocks: List<LoopParamBlock>) {
        val paramBlock: LoopParamBlock? = loopParamBlocks.firstOrNull { Rect.intersects(intersectionRect, it.rect) }
        if (paramBlock != null) {
            this.numberOfRepetitions = paramBlock.numberOfRepetitions
        } else {
            this.numberOfRepetitions = 0
            if (targetBlocks.isNotEmpty())
                throw LoopBlockNeedsParamError()
        }
    }

    private fun buildClones(blocks: List<ControllableBlock>): MutableList<Block> {
        var i = 0
        val repeatingBlocks = mutableListOf<Block>()
        val x = diameter.toInt()
        while (++i < numberOfRepetitions) {
            repeatingBlocks.addAll(targetBlocks.map { repeatableBlock ->
                repeatableBlock.copy().apply {
                    isRepetitionBlock = true
                    move(repeatableBlock.centerX + i * x, centerY)
                }
            })
        }
        return repeatingBlocks
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
    }

}
