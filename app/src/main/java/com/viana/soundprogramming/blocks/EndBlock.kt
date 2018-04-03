package com.viana.soundprogramming.blocks

class EndBlock : Block() {

    override fun execute() {
        board?.timeline()?.end = centerX.toFloat()
    }

}
