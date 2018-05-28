package com.viana.soundprogramming.blocks

class PresenceBlock(var type: Type) : Block(), NotMovableBlock {

    constructor() : this(Type.CIRCLE)

    override fun fillWithProperties(block: Block) {
        super.fillWithProperties(block)
        (block as PresenceBlock).type = type
    }

    enum class Type {
        STAR, CIRCLE, SQUARE
    }

}
