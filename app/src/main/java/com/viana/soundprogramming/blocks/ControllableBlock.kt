package com.viana.soundprogramming.blocks

abstract class ControllableBlock : Block() {
    abstract var playOnEachXLoops: Byte
    var conditionType: PresenceBlock.Type? = null
    var ifConditionSatisfied: Boolean = true
}

