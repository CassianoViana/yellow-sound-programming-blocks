package com.viana.soundprogramming.blocks

import com.viana.soundprogramming.camera.TopCodesChangedListener

interface BlocksManager : TopCodesChangedListener {

    companion object {
        val instance = BlocksManagerImpl()
    }
}
