package com.viana.soundprogramming.blocks

import android.graphics.Bitmap
import com.viana.soundprogramming.camera.TopCodesReaderListener
import topcodes.Scanner
import topcodes.TopCode

class TopCodesReader {

    private val topCodesScanner: Scanner = Scanner()
    private var listeners = mutableListOf<TopCodesReaderListener>()
    private lateinit var topCodes: List<TopCode>

    fun read(bitmap: Bitmap) {
        topCodes = topCodesScanner.scan(bitmap)
        listeners.forEach {
            it.topCodesChanged(topCodes)
        }
    }

    fun addListener(topCodesReaderListener: TopCodesReaderListener) {
        listeners.add(topCodesReaderListener)
    }
}