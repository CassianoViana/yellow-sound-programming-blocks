package com.viana.soundprogramming.blocks

import com.viana.soundprogramming.board.Board

class SpeedBlock : Block() {

    fun calculateSpeed(board: Board) {
        val timeline = board.timeline

        var degree = this.degree
        degree += 180
        if (degree > 360)
            degree -= 360

        val speedFactor = Math.abs(degree) / 120
        timeline.speedFactor = 3 - speedFactor
    }

}
