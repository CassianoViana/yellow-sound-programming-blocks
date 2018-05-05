package com.viana.soundprogramming.blocks

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import com.viana.soundprogramming.board.Board
import topcodes.TopCode

open class Block {
    var code: Int = 0
    var left: Int = 0
    var top: Int = 0
    var right: Int = 0
    var bottom: Int = 0
    var centerX: Int = 0
    var centerY: Int = 0
    var diameter: Float = 0f
    var board: Board? = null
    val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    var active: Boolean = true
    var rect = Rect()
    var degree: Float = 0f

    var topCode: TopCode? = null
        set(topCode) {
            field = topCode
            topCode?.let {
                val radius = it.diameter / 2
                left = (it.centerX - radius).toInt()
                top = (it.centerY - radius).toInt()
                right = (it.centerX + radius).toInt()
                bottom = (it.centerY + radius).toInt()
                centerX = it.centerX.toInt()
                centerY = it.centerY.toInt()
                diameter = it.diameter
                code = it.code
                degree = Math.toDegrees(Math.abs(it.orientation.toDouble())).toFloat()
                rect = Rect(left, top, right, bottom)
            }
        }

    open fun execute() {
    }

    open fun draw(canvas: Canvas?) {
        topCode?.draw(canvas)
        paint.color = Color.WHITE
        paint.textSize = 20f
        canvas?.drawText(this.toString(), left.toFloat(), top.toFloat(), paint)
    }

    open fun intersects(block: Block): Boolean {
        return false
    }

    fun centerPoint(): Rect {
        val dist = 1
        return Rect(centerX - dist, centerY - dist, centerX + dist, centerY + dist)
    }

    override fun toString(): String {
        return "Block(centerX=$centerX, centerY=$centerY, active=$active, degree=$degree)"
    }

    open fun copy(): Block {
        val block = Block()
        fillWithProperties(block)
        return block
    }

    open fun fillWithProperties(block: Block) {
        block.board = board
        block.topCode = topCode?.copy()
    }

    fun move(toX: Int, toY: Int) {
        this.apply {
            centerX = toX
            centerY = toY
            topCode?.apply {
                x = toX.toFloat()
                y = toY.toFloat()
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Block

        if (code != other.code) return false

        return true
    }

    override fun hashCode(): Int {
        return code
    }

}
