package com.viana.soundprogramming

import android.graphics.Canvas

interface Board {

    fun widthFloat(): Float
    fun heightFloat(): Float
    fun update()
    fun draw(canvas: Canvas?)
}
