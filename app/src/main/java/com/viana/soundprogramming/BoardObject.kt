package com.viana.soundprogramming

import android.graphics.Canvas

interface BoardObject {
    fun update()
    fun draw(canvas: Canvas)
}
