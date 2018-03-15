package com.viana.soundprogramming

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.viana.soundprogramming.sound.SoundManager

class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
    }

    fun play(view: View) {
        val soundManager = SoundManager.instance
        val id1 = soundManager.load(R.raw.clap1)
        val id2 = soundManager.load(R.raw.clap2)
        val id3 = soundManager.load(R.raw.clap8)

        while (true) {
            Thread.sleep(400)
            soundManager.play(id1)

            Thread.sleep(400)
            soundManager.play(id2)

            Thread.sleep(400)
            soundManager.play(id3)
        }
    }
}