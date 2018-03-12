package com.viana.soundprogramming.blocks

import android.graphics.Canvas
import android.util.Log
import topcodes.TopCode

open class Block() {
    var left: Int = 0
    var top: Int = 0
    var right: Int = 0
    var bottom: Int = 0

    var topCode: TopCode? = null
        set(topCode) {
            field = topCode
            topCode?.let {
                val radius = it.diameter / 2
                left = (it.centerX - radius).toInt()
                top = (it.centerY - radius).toInt()
                right = (it.centerX + radius).toInt()
                bottom = (it.centerY + radius).toInt()
            }
        }

    open fun execute() {
        Log.i("Block", "execute")
    }

    fun draw(canvas: Canvas?) {
        topCode?.draw(canvas)
    }
}
