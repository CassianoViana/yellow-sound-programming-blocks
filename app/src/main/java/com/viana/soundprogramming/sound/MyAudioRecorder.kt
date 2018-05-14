package com.viana.soundprogramming.sound

import com.viana.soundprogramming.appInstance
import java.util.*


class MyAudioRecorder : Recorder {

    private var audioRecorder = CyanogenAudioRecorder()

    override lateinit var listener: Recorder.Listener

    override fun recordSeconds(seconds: Int, code: Int) {
        audioRecorder.startRecording(getFileNameWithoutExtension(code))
        stopAfterDelay(code, seconds)
    }

    private fun stopAfterDelay(code: Int, seconds: Int) {
        Timer().schedule(object : TimerTask() {
            override fun run() {
                audioRecorder.stop()
                val recordedFilePath = getRecordedFileName(code)
                val soundId = SoundManager.instance.load(recordedFilePath)
                listener.onCodeRecorded(soundId, recordedFilePath)
            }
        }, seconds * 1000L)
    }

    override fun getRecordedFileName(code: Int): String {
        return getFileNameWithoutExtension(code) + ".wav"
    }

    private fun getFileNameWithoutExtension(code: Int) =
            appInstance.filesDir.absolutePath + "/$code"
}
