package com.viana.soundprogramming.sound

import android.media.MediaRecorder
import com.viana.soundprogramming.appInstance
import java.util.*

fun getRecordedFileName(code: Int): String {
    return appInstance.filesDir.absolutePath + "/$code.3gp"
}

class Recorder {

    private var mediaRec: MediaRecorder? = MediaRecorder()
    var listener: Listener? = null

    fun recordSeconds(seconds: Int, code: Int) {
        mediaRec = MediaRecorder()
        mediaRec?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRec?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mediaRec?.setOutputFile(getRecordedFileName(code))
        mediaRec?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        try {
            mediaRec?.prepare()
            mediaRec?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        stopAfterSeconds(seconds, code)
    }

    private fun stopAfterSeconds(seconds: Int, code: Int) {
        Timer().schedule(object : TimerTask() {
            override fun run() {
                stopRecording(code)
            }
        }, (seconds * 1000).toLong())
    }

    private fun stopRecording(code: Int) {
        try {
            mediaRec?.stop()
            mediaRec?.release()
            mediaRec = null
            SoundManager.instance.load(getRecordedFileName(code), object : SoundManager.OnLoadListener {
                override fun loaded(soundId: Int) {
                    listener?.onCodeRecorded(soundId)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun play(soundId: Int) {
        SoundManager.instance.play(soundId)
    }

    interface Listener {
        fun onCodeRecorded(producedSoundId: Int)
    }
}
