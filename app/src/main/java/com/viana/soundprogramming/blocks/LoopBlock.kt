package com.viana.soundprogramming.blocks

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.util.Log
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
            val bottom = (centerY + diameter * 4).toInt()
            rect = Rect(left, top, right, bottom)
        }
        return rect
    }

    override fun draw(canvas: Canvas?) {
        bitmap?.let {
            canvas?.drawBitmap(bitmap, left.toFloat(), top.toFloat(), paint)
        }

        paint.color = Color.YELLOW
        paint.alpha = 50
        canvas?.drawRect(intersectionRect, paint)
    }

    fun repeatBlocks(loopTargetBlocks: List<ControllableBlock>,
                     loopParamBlocks: List<LoopParamBlock>): List<Block> {
        filterTargetBlocks(loopTargetBlocks)
        calculateNumberOfRepetitions(loopParamBlocks)
        return buildClones()
    }

    private fun filterTargetBlocks(blocks: List<ControllableBlock>) {
        targetBlocks = blocks.filter { Rect.intersects(intersectionRect, it.rect) }
    }

    private fun calculateNumberOfRepetitions(loopParamBlocks: List<LoopParamBlock>) {
        val paramBlock: LoopParamBlock? = loopParamBlocks.firstOrNull { Rect.intersects(intersectionRect, it.rect) }
        if (paramBlock != null) {
            this.numberOfRepetitions = paramBlock.numberOfRepetitions
            Log.i("LoopBlock", "repetitions= ${this.numberOfRepetitions}")
        } else {
            this.numberOfRepetitions = 0
            if (targetBlocks.isNotEmpty())
                throw LoopBlockNeedsParamError()
        }
    }

    private fun buildClones(): MutableList<Block> {
        var i = 1
        val repeatingBlocks = mutableListOf<Block>()
        val distToAdd = diameter * 2
        while (i < numberOfRepetitions) {
            repeatingBlocks.addAll(targetBlocks.map { repeatableBlock ->
                repeatableBlock.copy().apply {
                    isRepetitionBlock = true
                    move(repeatableBlock.centerX + i * distToAdd.toInt(), centerY)
                }
            })
            i++
        }
        return repeatingBlocks
    }

    fun moveFollowingToRight(blocks: List<Block>, loopBlock: LoopBlock, repeatedBlocks: List<Block>) {
        val lastRepeatedBlock = repeatedBlocks.maxBy { it.centerX }
        lastRepeatedBlock?.let {
            val diff = lastRepeatedBlock.centerX - loopBlock.centerX
            val following = blocks.filter {
                val gapToNotMoveFirstRepeatedBlock = 30//px
                it.centerX > (loopBlock.centerX) + gapToNotMoveFirstRepeatedBlock && !repeatedBlocks.contains(it)
            }
            following.forEach {
                if (it !is NotMovableBlock)
                    it.move(it.centerX + diff, it.centerY)
            }
        }
    }
}
