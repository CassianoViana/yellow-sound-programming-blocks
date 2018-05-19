package com.viana.soundprogramming.blocks

import android.graphics.Rect

class IfBlock : Block() {

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

    fun muteFalseTestBlocks(ifTargetBlocks: List<ControllableBlock>, ifParamBlocks: List<IfParamBlock>, presenceBlocks: List<PresenceBlock>) {
        filterTargetBlocks(ifTargetBlocks)
        val intersectedParams = ifParamBlocks.filter { Rect.intersects(intersectionRect, it.rect) }
        val intersectedSoundBlocks = ifTargetBlocks.filter { Rect.intersects(intersectionRect, it.rect) }

        if (intersectedParams.any { it.type == IfParamBlock.Type.HAS_CIRCLE }) {
            intersectedSoundBlocks.forEach {
                it.active = presenceBlocks.any { it.type == PresenceBlock.Type.CIRCLE }
            }
        }
        if (intersectedParams.any { it.type == IfParamBlock.Type.HAS_SQUARE }) {
            intersectedSoundBlocks.forEach {
                it.active = presenceBlocks.any { it.type == PresenceBlock.Type.SQUARE }
            }
        }
        if (intersectedParams.any { it.type == IfParamBlock.Type.HAS_STAR }) {
            intersectedSoundBlocks.forEach {
                it.active = presenceBlocks.any { it.type == PresenceBlock.Type.STAR }
            }
        }
    }

    private fun filterTargetBlocks(ifTargetBlocks: List<ControllableBlock>) {

    }

}
