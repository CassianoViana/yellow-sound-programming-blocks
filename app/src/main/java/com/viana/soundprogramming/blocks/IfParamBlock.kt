package com.viana.soundprogramming.blocks

import android.graphics.Rect


class IfParamBlock(var type: PresenceBlock.Type) : Block() {

    constructor() : this(PresenceBlock.Type.CIRCLE)

    override fun fillWithProperties(block: Block) {
        super.fillWithProperties(block)
        (block as IfParamBlock).type = type
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
                val bottom = (centerY + diameter * 3).toInt()
                rect = Rect(left, top, right, bottom)
            }
        }
        return rect
    }

    fun computeIfBlocks(ifTargetBlocks: List<ControllableBlock>, presenceBlocks: List<PresenceBlock>): List<Block> {
        val intersectedSoundBlocks = ifTargetBlocks.filter { Rect.intersects(intersectionRect, it.rect) }
        intersectedSoundBlocks.forEach { intersectedSoundBlock ->
            intersectedSoundBlock.conditionType = type
            intersectedSoundBlock.ifConditionSatisfied = presenceBlocks.map { it.type }.contains(type)
        }
        return intersectedSoundBlocks.filter { !it.active }
    }
}
