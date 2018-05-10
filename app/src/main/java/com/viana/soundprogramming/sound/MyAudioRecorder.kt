package com.viana.soundprogramming.sound

import com.viana.soundprogramming.appInstance
import java.io.File
import java.util.*


class MyAudioRecorder : Recorder {

    private var audioRecorder = AudioRecorder()

    override lateinit var listener: Recorder.Listener

    override fun recordSeconds(seconds: Int, code: Int) {
        val soundFilePath = getRecordedFileName(code)
        audioRecorder.record(File(soundFilePath))
        Timer().schedule(object : TimerTask() {
            override fun run() {
                audioRecorder.stop()
                val soundId = SoundManager.instance.load(soundFilePath)
                listener.onCodeRecorded(soundId)
            }
        }, seconds * 1000L)
    }


    override fun getRecordedFileName(code: Int): String {
        return appInstance.filesDir.absolutePath + "/$code.wav"
    }
}
