package com.viana.soundprogramming.blocks

import com.viana.soundprogramming.board.Board

class SpeedBlock : Block() {

    fun calculateSpeed(board: Board?) {
        val timeline = board?.timeline

        var degree = this.degree
        degree += 180
        if (degree > 360)
            degree -= 360

        val notStopped = timeline?.speedFactor != 0f
        if (notStopped) {
            val speedFactor = Math.abs(degree) / 180
            var fl = 2f - speedFactor
            if (fl < 0.5) fl = 0.5f
            timeline?.speedFactor = fl
        }
    }
}
