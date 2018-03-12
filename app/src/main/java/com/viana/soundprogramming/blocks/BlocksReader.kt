package com.viana.soundprogramming.blocks

import android.graphics.Bitmap
import com.viana.soundprogramming.camera.TopCodesChangedListener
import topcodes.Scanner
import topcodes.TopCode

class BlocksReader {

    private val topCodesScanner: Scanner = Scanner()
    var topCodesListeners = mutableListOf<TopCodesChangedListener>()
    private lateinit var topCodes: List<TopCode>

    fun readBlocks(bitmap: Bitmap) {
        topCodes = topCodesScanner.scan(bitmap)
        topCodesListeners.forEach {
            it.topCodesChanged(topCodes)
        }
    }
}