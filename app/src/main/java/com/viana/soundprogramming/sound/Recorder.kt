package com.viana.soundprogramming.sound

import android.media.MediaRecorder
import android.os.Environment
import java.io.File
import java.util.*

val SOUNDS_DIRECTORY = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).absolutePath + "/SoundProgramming"

fun getRecordedFileName(code: Int): String {
    val soundsDirectory = File(SOUNDS_DIRECTORY)
    if (!soundsDirectory.exists())
        soundsDirectory.mkdirs()
    return "$SOUNDS_DIRECTORY/$code.3gp"
}

class Recorder {

    private var mediaRec: MediaRecorder? = null
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
            val soundRecordedId = SoundManager.instance.load(getRecordedFileName(code))
            listener?.onCodeRecorded(soundRecordedId)
            mediaRec?.stop()
            mediaRec?.release()
            mediaRec = null
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
