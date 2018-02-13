package com.viana.soundprogramming

import topcodes.TopCode

class BlocksReader : TopCodesChangedListener {
    private lateinit var topCodes: List<TopCode>

    override fun topCodesChanged(topCodes: List<TopCode>) {
        this.topCodes = topCodes
        readBlocks()
    }

    private fun readBlocks(): List<Sound> {
        return mutableListOf()
    }
}