package com.viana.soundprogramming

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.viana.soundprogramming.blocks.BlocksManager
import com.viana.soundprogramming.blocks.TopCodesReader
import com.viana.soundprogramming.camera.Camera
import com.viana.soundprogramming.camera.CameraListener
import com.viana.soundprogramming.camera.ScreenUtil
import com.viana.soundprogramming.core.Music
import com.viana.soundprogramming.core.MusicBuilderImpl
import com.viana.soundprogramming.timeline.Timeline
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
    private var music: Music? = null

    private val cameraListener = object : CameraListener {
        override fun onEachFrame(bitmap: Bitmap) {
            topCodesReader.read(bitmap)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sound_programming)
        prepareCamera()
        prepareTopCodeListeners()
        prepareBlocksManagerListeners()
        Timer().schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread({
                    startCamera()
                })
            }
        }, 5000)
    }

    private fun prepareCamera() {
        camera = Camera(this, cameraListener, surfaceView)
    }

    private fun prepareTopCodeListeners() {
        topCodesReader.addListener(blocksManager)
    }

    private fun prepareBlocksManagerListeners() {
        blocksManager
                .addListener(boardSurfaceView)
                .addListener(stateMachine)
        stateMachine
                .addListener(this)
                .addListener(boardSurfaceView.timeline)
                .addListener(object : StateMachine.Listener{
                    override fun stateChanged(state: StateMachine.State) {
                        Log.i("teste", state.toString())
                    }
                })
        boardSurfaceView
                .timeline
                .addListener(object : Timeline.Listener {
                    override fun onHitStart() {
                        music = musicBuilder.build(blocksManager.blocks, boardSurfaceView)
                        /*ProgrammingVibrator.vibrate(10)*/
                        music?.play()
                    }
                })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_CAMERA_PERMISSION
                && grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            camera.openCamera()
    }

    override fun onResume() {
        super.onResume()
        ScreenUtil.fullscreen(window)
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

    private fun startCamera() {
        //ScreenUtil.fullscreen(window)
        boardSurfaceView.start()
        camera.openCamera()
    }

    private fun stopCamera() {
        //ScreenUtil.exitFullscreen(window)
        boardSurfaceView.stop()
        camera.closeCamera()
    }

    override fun stateChanged(state: StateMachine.State) {
        when (state) {
            StateMachine.State.PLAYING -> {
                btnStartStop.background = ContextCompat
                        .getDrawable(this, R.drawable.button_stop)
                btnStartStop.setText(R.string.stop)
            }
            StateMachine.State.PAUSED -> {
                btnStartStop.background = ContextCompat
                        .getDrawable(this, R.drawable.button_start)
                btnStartStop.setText(R.string.start)
            }
            else -> {
            }
        }
    }
}