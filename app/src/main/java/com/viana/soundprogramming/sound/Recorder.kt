package com.viana.soundprogramming.sound

import com.viana.soundprogramming.appInstance

interface Recorder {

    var listener: Listener
    fun recordSeconds(seconds: Int, code: Int)

    interface Listener {
        fun onCodeRecorded(producedSoundId: Int)
    }

    fun getRecordedFileName(code: Int): String {
        return appInstance.filesDir.absolutePath + "/$code.3gp"
    }

}
