package com.viana.soundprogramming

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.viana.soundprogramming.blocks.Block
import com.viana.soundprogramming.blocks.BlocksManager
import com.viana.soundprogramming.blocks.TopCodesReader
import com.viana.soundprogramming.camera.Camera
import com.viana.soundprogramming.camera.OnEachFrameListener
import com.viana.soundprogramming.camera.OnOpenCameraListener
import com.viana.soundprogramming.camera.ScreenUtil
import com.viana.soundprogramming.core.Music
import com.viana.soundprogramming.core.MusicBuilder
import com.viana.soundprogramming.core.MusicBuilderImpl
import com.viana.soundprogramming.sound.Speaker
import com.viana.soundprogramming.timeline.Timeline
import com.viana.soundprogramming.timeline.TimelineTimer
import com.viana.soundprogramming.vibration.ProgrammingVibrator
import kotlinx.android.synthetic.main.activity_sound_programming.*
import java.util.*


const val REQUEST_CODE_CAMERA_PERMISSION = 100
const val REQUEST_CODE_RECORD_PERMISSION = 200
const val REQUEST_CODE_WRITE_EXTERNAL_PERMISSION = 300
const val REQUEST_CODE_VIBRATE_PERMISSION = 400

class SoundProgrammingActivity : AppCompatActivity(), StateMachine.Listener {

    private lateinit var camera: Camera
    private val topCodesReader = TopCodesReader()
    private val blocksManager = BlocksManager()
    private val musicBuilder = MusicBuilderImpl()
    private val stateMachine = StateMachine()
    private var music:Music? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sound_programming)
        prepareCamera()
        prepareTopCodeListeners()
        prepareBlocksManagerListeners()
        Speaker.instance.load()
    }

    override fun onResume() {
        super.onResume()
        ScreenUtil.fullscreen(window)
        startAfterDelay(1000)
    }

    override fun onPause() {
        super.onPause()
        ScreenUtil.exitFullscreen(window)
        stopCamera()
    }

    fun onClickStartStop(view: View) {
        ProgrammingVibrator.vibrate(30)
        if (!camera.isCameraOpen) startCamera() else stopCamera()
    }

    fun onClickTest(view: View) {
        stopCamera()
        ProgrammingVibrator.vibrate(30)
        startActivity(Intent(this, TestActivity::class.java))
    }

    private fun startAfterDelay(delay: Long) {
        Timer().schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread({
                    startCamera()
                })
            }
        }, delay)
    }

    private fun prepareCamera() {
        boardSurfaceView.prepare(this, timelineView)
        camera = Camera(this, surfaceView)
        camera.onEachFrameListener = object : OnEachFrameListener {
            override fun newFrame(bitmap: Bitmap) {
                topCodesReader.read(bitmap)
            }
        }
        camera.onOpenCameraListener = object : OnOpenCameraListener {
            override fun cameraOpened() {
                Speaker.instance.say(R.raw.vamos_programar)
                boardSurfaceView.start()
            }
        }
    }

    private fun startCamera() {
        camera.openCamera()
    }

    private fun stopCamera() {
        boardSurfaceView.stop()
        camera.closeCamera()
    }

    private fun prepareTopCodeListeners() {
        topCodesReader.addListener(blocksManager)
    }

    private fun prepareBlocksManagerListeners() {
        blocksManager
                .addListener(boardSurfaceView)
                .addListener(stateMachine)
                .addListener(object : BlocksManager.Listener{
                    override fun updateBlocksList(blocks: List<Block>) {
                        musicBuilder.build(
                                blocks,
                                boardSurfaceView,
                                object : MusicBuilder.OnMusicReadyListener {
                                    override fun ready(music: Music) {
                                        this@SoundProgrammingActivity.music = music
                                    }
                                }
                        )
                    }
                })
        stateMachine
                .addListener(this)
                .addListener(boardSurfaceView.timeline)
        boardSurfaceView
                .timeline?.addListener(object : Timeline.Listener {
            override fun onHitStart(timelineTimer: TimelineTimer) {
                ProgrammingVibrator.vibrate(10)
                music?.play(timelineTimer)
            }
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_CAMERA_PERMISSION
                && grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            camera.openCamera()
    }

    override fun stateChanged(state: StateMachine.State) {
        when (state) {
            StateMachine.State.PLAYING -> {
                Speaker.instance.say(R.raw.a_musica_comecou)
                btnStartStop.background = ContextCompat
                        .getDrawable(this, R.drawable.button_stop)
                btnStartStop.setText(R.string.stop)
            }
            StateMachine.State.PAUSED -> {
                Speaker.instance.say(R.raw.a_musica_foi_interrompida)
                btnStartStop.background = ContextCompat
                        .getDrawable(this, R.drawable.button_start)
                btnStartStop.setText(R.string.start)
            }
            else -> {
            }
        }
    }
}