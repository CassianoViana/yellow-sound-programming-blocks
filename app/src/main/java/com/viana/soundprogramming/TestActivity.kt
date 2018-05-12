package com.viana.soundprogramming

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.viana.soundprogramming.sound.*

class TestActivity : AppCompatActivity() {

    private val recorder: Recorder = MyAudioRecorder()
    private var soundId: Int = 0

    private var recordAudio = CyanogenAudioRecorder()
    private var audioTrackPlayer = AudioTrackPlayer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
    }

    fun play(view: View) {
        Thread({
            audioTrackPlayer.start()
            audioTrackPlayer.playWav(Environment.getExternalStorageDirectory().absolutePath + "/abacate.pcm")

            for (i in 1 until 10) {
                audioTrackPlayer.playInputStream(resources.openRawResource(R.raw.drum1))
                audioTrackPlayer.addInterval(1)
                audioTrackPlayer.playInputStream(resources.openRawResource(R.raw.drum2))
            }

            audioTrackPlayer.stop()
        }).start()
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