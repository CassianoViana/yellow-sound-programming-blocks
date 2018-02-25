package com.viana.soundprogramming

import android.graphics.Bitmap
import topcodes.Scanner
import topcodes.TopCode

class BlocksReader {

    private val topCodesScanner: Scanner = Scanner()
    var topCodesListeners = mutableListOf<TopCodesChangedListener>()
    private lateinit var topCodes: List<TopCode>

    fun readBlocks(bitmap: Bitmap): List<Sound> {
        topCodes = topCodesScanner.scan(bitmap)
        topCodesListeners.forEach {
            it.topCodesChanged(topCodes)
        }
        return mutableListOf()
    }
}