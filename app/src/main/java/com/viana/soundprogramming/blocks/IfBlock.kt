package com.viana.soundprogramming.blocks

import android.graphics.Rect
import com.viana.soundprogramming.exceptions.IfBlockNeedsParamError

class IfBlock : Block() {

    override fun buildSyntaxIntersectionRect(): Rect {
        var rect = Rect()
        topCode?.let {
            val diameter = it.diameter
            val left = (centerX - diameter).toInt()
            val right = (centerX + diameter).toInt()
            val bottom = (centerY + diameter * 6).toInt()
            rect = Rect(left, top, right, bottom)
        }
        return rect
    }

    fun findFalseTestBlocks(ifTargetBlocks: List<ControllableBlock>, ifParamBlocks: List<IfParamBlock>, presenceBlocks: List<PresenceBlock>): List<Block> {
        filterTargetBlocks()
        val intersectedParams = ifParamBlocks.filter { Rect.intersects(intersectionRect, it.rect) }
        val intersectedSoundBlocks = ifTargetBlocks.filter { Rect.intersects(intersectionRect, it.rect) }
        if (intersectedParams.isEmpty() && intersectedSoundBlocks.isNotEmpty())
            throw IfBlockNeedsParamError()
        intersectedParams.forEach { param ->
            intersectedSoundBlocks.forEach { intersectedSoundBlock ->
                intersectedSoundBlock.conditionType = param.type
                intersectedSoundBlock.ifConditionSatisfied = presenceBlocks.map { it.type }.contains(param.type)
            }
        }
        return intersectedSoundBlocks.filter { !it.active }
    }

    private fun filterTargetBlocks() {

    }

}
