package com.viana.soundprogramming

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.viana.soundprogramming.sound.MyAudioRecorder
import com.viana.soundprogramming.sound.CyanogenAudioRecorder
import com.viana.soundprogramming.sound.Recorder
import com.viana.soundprogramming.sound.SoundManager

class TestActivity : AppCompatActivity() {

    private val recorder: Recorder = MyAudioRecorder()
    private var soundId: Int = 0

    private var recordAudio = CyanogenAudioRecorder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
    }

    fun play(view: View) {
        val soundManager = SoundManager.instance
        val id1 = soundManager.load(R.raw.guittar1)
        val id2 = soundManager.load(R.raw.guittar2)
        val id3 = soundManager.load(R.raw.guittar3)
        soundManager.play(id1)
        soundManager.play(id2)
        soundManager.play(id3)
    }

    fun load(view: View) {
        soundId = SoundManager.instance.load(recorder.getRecordedFileName(777))
    }

    fun record(view: View?) {
        recordAudio = CyanogenAudioRecorder()
        val recordTargetPath = Environment.getExternalStorageDirectory().absolutePath + "/abacate"
        recordAudio.startRecording(recordTargetPath)
    }

    fun stop(view: View) {
        recordAudio.stop()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_RECORD_PERMISSION) {
            record(null)
        }
    }

    fun playRecord(view: View) {

    }
}