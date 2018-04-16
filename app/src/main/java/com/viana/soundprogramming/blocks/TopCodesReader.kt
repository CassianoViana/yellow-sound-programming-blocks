package com.viana.soundprogramming.blocks

import android.graphics.Bitmap
import topcodes.Scanner
import topcodes.TopCode

class TopCodesReader {

    private val topCodesScanner: Scanner = Scanner()
    private var listeners = mutableListOf<Listener>()
    private lateinit var topCodes: List<TopCode>

    fun read(bitmap: Bitmap) {
        topCodes = topCodesScanner.scan(bitmap)
        listeners.forEach {
            it.topCodesChanged(topCodes)
        }
    }

    fun addListener(listener: Listener) {
        listeners.add(listener)
    }

    interface Listener {
        fun topCodesChanged(topCodes: List<TopCode>)
    }
}