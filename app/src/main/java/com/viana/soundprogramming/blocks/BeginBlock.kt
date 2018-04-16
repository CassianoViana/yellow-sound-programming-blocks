package com.viana.soundprogramming.blocks

class BeginBlock : Block() {

    override fun execute() {
        board?.timeline?.begin = centerX.toFloat()
    }

}
