package com.viana.soundprogramming.board

import android.graphics.Canvas

interface BoardObject {
    fun update()
    fun draw(canvas: Canvas)
}
