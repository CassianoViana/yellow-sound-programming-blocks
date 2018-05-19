package com.viana.soundprogramming

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.viana.soundprogramming.sound.*
import org.apache.commons.io.IOUtils
import java.io.ByteArrayInputStream
import java.io.InputStream

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
            val audioMixer = AudioMixer(10 * 1000, 44100 * 2, 1f)
            audioMixer.addSound(0, IOUtils.toByteArray(resources.openRawResource(R.raw.falta_informar_parametro_repita)))
            audioMixer.addSound(2000, IOUtils.toByteArray(resources.openRawResource(R.raw.drum1)))
            audioMixer.addSound(3000, IOUtils.toByteArray(resources.openRawResource(R.raw.chimbal)))
            audioMixer.addSound(3100, IOUtils.toByteArray(resources.openRawResource(R.raw.chimbal)))
            audioMixer.addSound(3500, IOUtils.toByteArray(resources.openRawResource(R.raw.chimbal)))

            val mixed: InputStream = ByteArrayInputStream(audioMixer.mixAddedSounds())

            audioTrackPlayer.start()
            audioTrackPlayer.playInputStream(mixed)
            /*audioTrackPlayer.stop()*/
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