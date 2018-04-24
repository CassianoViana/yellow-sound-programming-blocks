package com.viana.soundprogramming

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.viana.soundprogramming.sound.Recorder
import com.viana.soundprogramming.sound.SoundManager
import com.viana.soundprogramming.sound.getRecordedFileName
import com.viana.soundprogramming.util.managePermissionDirectory
import com.viana.soundprogramming.util.managePermissionSound

class TestActivity : AppCompatActivity() {

    val recorder = Recorder()
    var soundId: Int = 0

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
        soundId = SoundManager.instance.load(getRecordedFileName(777))
    }

    fun record(view: View?) {
        val code = 777
        if (managePermissionDirectory(this)) return
        if (managePermissionSound(this)) return
        recorder.listener = object : Recorder.Listener {
            override fun onCodeRecorded(producedSoundId: Int) {
                soundId = producedSoundId
            }
        }
        recorder.recordSeconds(3, code)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_RECORD_PERMISSION) {
            record(null)
        }
    }

    fun playRecord(view: View) {
        recorder.play(soundId)
    }
}