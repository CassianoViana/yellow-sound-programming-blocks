package com.viana.soundprogramming.blocks

import android.graphics.Rect

class LoopParamBlock(var numberOfRepetitions: Byte) : Block() {

    constructor() : this(0)

    private var targetBlocks: List<ControllableBlock> = mutableListOf()

    override fun fillWithProperties(block: Block) {
        super.fillWithProperties(block)
        (block as LoopParamBlock).numberOfRepetitions = numberOfRepetitions
    }

    override fun buildSyntaxIntersectionRect(): Rect {
        var rect = Rect()
        if (degree > 90 && degree < 360) {
            topCode?.let {
                val diameter = it.diameter
                val left = (centerX - diameter).toInt()
                val right = (centerX + diameter).toInt()
                val top = (centerY - diameter * 3).toInt()
                rect = Rect(left, top, right, bottom)
            }
        } else {
            topCode?.let {
                val diameter = it.diameter
                val left = (centerX - diameter).toInt()
                val right = (centerX + diameter).toInt()
                val bottom = (centerY + diameter * 2).toInt()
                rect = Rect(left, top, right, bottom)
            }
        }
        return rect
    }

    fun repeatBlocks(loopTargetBlocks: List<ControllableBlock>): List<Block> {
        filterTargetBlocks(loopTargetBlocks)
        return buildClones()
    }

    private fun filterTargetBlocks(blocks: List<ControllableBlock>) {
        targetBlocks = blocks.filter { Rect.intersects(intersectionRect, it.rect) }
    }

    private fun buildClones(): MutableList<Block> {
        var i = 1
        val repeatingBlocks = mutableListOf<Block>()
        val distToAdd = diameter * 2.3
        while (i < numberOfRepetitions) {
            repeatingBlocks.addAll(targetBlocks.map { repeatableBlock ->
                repeatableBlock.copy().apply {
                    isRepetitionBlock = true
                    move((repeatableBlock.centerX + i * distToAdd).toInt(), centerY)
                }
            })
            i++
        }
        return repeatingBlocks
    }

    fun moveFollowingToRight(blocks: List<Block>, repeatedBlocks: List<Block>) {
        val lastRepeatedBlock = repeatedBlocks.maxBy { it.centerX }
        lastRepeatedBlock?.let {
            val diff = lastRepeatedBlock.centerX - centerX
            val following = blocks.filter {
                val gapToNotMoveFirstRepeatedBlock = 30//px
                it.centerX > (centerX) + gapToNotMoveFirstRepeatedBlock && !repeatedBlocks.contains(it)
            }
            following.forEach {
                if (it !is NotMovableBlock)
                    it.move(it.centerX + diff, it.centerY)
            }
        }
    }

}
