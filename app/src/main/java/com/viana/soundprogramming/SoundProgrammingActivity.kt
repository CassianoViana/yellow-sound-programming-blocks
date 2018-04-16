package com.viana.soundprogramming

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.viana.soundprogramming.blocks.*
import com.viana.soundprogramming.camera.Camera
import com.viana.soundprogramming.camera.CameraListener
import com.viana.soundprogramming.camera.ScreenUtil
import com.viana.soundprogramming.core.Music
import com.viana.soundprogramming.core.MusicBuilderImpl
import com.viana.soundprogramming.timeline.Timeline
import com.viana.soundprogramming.vibration.ProgrammingVibrator
import kotlinx.android.synthetic.main.activity_sound_programming.*


const val REQUEST_CODE_CAMERA_PERMISSION = 100
const val REQUEST_CODE_RECORD_PERMISSION = 200
const val REQUEST_CODE_WRITE_EXTERNAL_PERMISSION = 300
const val REQUEST_CODE_VIBRATE_PERMISSION = 400

class SoundProgrammingActivity : AppCompatActivity() {

    private lateinit var camera: Camera
    private val topCodesReader = TopCodesReader()
    private val blocksManager = BlocksManager()
    private val musicBuilder = MusicBuilderImpl()
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
    }

    private fun prepareCamera() {
        val cameraListener: CameraListener = cameraListener
        camera = Camera(this, cameraListener, surfaceView)
    }

    private fun prepareTopCodeListeners() {
        topCodesReader.addListener(blocksManager)
    }

    private fun prepareBlocksManagerListeners() {
        blocksManager.addListener(boardSurfaceView)
        blocksManager.addListener(object : BlocksManager.Listener {
            override fun updateBlocksList(blocks: List<Block>) {
                music = musicBuilder.build(blocks, boardSurfaceView)
            }

            override fun beginBlockEntered(block: BeginBlock) {
                Log.i("TESTE", "START Block entered")
            }

        })
        boardSurfaceView
                .timeline
                .addListener(object : Timeline.Listener {
                    override fun onHitStart() {
                        ProgrammingVibrator.vibrate(10)
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
        if (!camera.isCameraOpen) start() else stop()
    }

    fun onClickTest(view: View) {
        ProgrammingVibrator.vibrate(30)
        startActivity(Intent(this, TestActivity::class.java))
    }

    private fun start() {
        btnStartStop.background = ContextCompat
                .getDrawable(this, R.drawable.button_stop)
        btnStartStop.setText(R.string.stop)
        ScreenUtil.fullscreen(window)
        camera.openCamera()
        boardSurfaceView.timeline.start()
    }

    private fun stop() {
        btnStartStop.background = ContextCompat
                .getDrawable(this, R.drawable.button_start)
        btnStartStop.setText(R.string.start)
        ScreenUtil.exitFullscreen(window)
        camera.closeCamera()
        boardSurfaceView.timeline.stop()
    }
}