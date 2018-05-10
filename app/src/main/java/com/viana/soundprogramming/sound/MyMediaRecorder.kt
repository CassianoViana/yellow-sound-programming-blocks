package com.viana.soundprogramming.sound

import android.media.MediaRecorder
import java.util.*

class MyMediaRecorder : Recorder {

    private var mediaRec: MediaRecorder? = MediaRecorder()
    override lateinit var listener: Recorder.Listener

    override fun recordSeconds(seconds: Int, code: Int) {
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
                    listener.onCodeRecorded(soundId)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
