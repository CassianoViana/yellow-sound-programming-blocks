package com.viana.soundprogramming

import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.viana.soundprogramming.blocks.Block
import com.viana.soundprogramming.blocks.BlocksLibrary
import com.viana.soundprogramming.board.Board
import com.viana.soundprogramming.core.Music
import com.viana.soundprogramming.core.MusicBuilder
import com.viana.soundprogramming.core.MusicBuilderImpl
import com.viana.soundprogramming.exceptions.SoundProgrammingError
import com.viana.soundprogramming.sound.*
import com.viana.soundprogramming.timeline.Timeline
import com.viana.soundprogramming.util.readShorts
import topcodes.TopCode

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
            //playShorts()
            //playMixe()
            playMusic()
        }).start()
    }

    private fun playShorts() {
        val inputStream = resources.openRawResource(R.raw.chimbal)

        val shorts = readShorts(inputStream)

        audioTrackPlayer.start()
        audioTrackPlayer.playShortSamples(shorts)
        audioTrackPlayer.stop()
        audioTrackPlayer.release()
    }

    private fun playMixe() {
        val audioMixer = AudioMixerShort(10, 1f)
        audioMixer.addSound(3.000f, readShorts(resources.openRawResource(R.raw.chimbal)))
        audioMixer.addSound(3.100f, readShorts(resources.openRawResource(R.raw.chimbal)))
        audioMixer.addSound(3.500f, readShorts(resources.openRawResource(R.raw.chimbal)))

        audioTrackPlayer.start()
        audioTrackPlayer.playShortSamples(audioMixer.mixAddedSounds())
    }

    fun playMusic() {

        val blocksLibrary = BlocksLibrary()

        val a = TopCode(185)
        a.setLocation(165f, 50f)
        val soundBlockA = blocksLibrary.getTopCodeBlock(a)

        val b = TopCode(173)
        b.setLocation(87f, 50f)
        val soundBlockB = blocksLibrary.getTopCodeBlock(b)

        val c = TopCode(179)
        c.setLocation(323f, 50f)
        val soundBlockC = blocksLibrary.getTopCodeBlock(c)

        val blocks = listOfNotNull(soundBlockA, soundBlockB, soundBlockC)

        val board = object : Board {
            override lateinit var timeline: Timeline
            override var widthFloat: Float = 1000f
            override var heightFloat: Float = 200f
            override var blocks: List<Block> = blocks
            override fun updateAndDraw() {}
            override fun draw(canvas: Canvas) {}
        }

        val timeline = Timeline(board)
        board.timeline = timeline
        timeline.board = board

        val musicBuilder = MusicBuilderImpl()
        musicBuilder.build(blocks, board, object : MusicBuilder.OnMusicReadyListener {
            override fun ready(music: Music) {
                //playMixe()
                music.play(0)
            }
            override fun error(e: SoundProgrammingError) {
            }
        })
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