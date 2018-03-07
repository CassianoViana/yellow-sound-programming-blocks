package com.viana.soundprogramming

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_sound_programming.*
import topcodes.TopCode

const val REQUEST_CODE_CAMERA_PERMISSION = 100

class SoundProgrammingActivity : AppCompatActivity() {

    private val blocksReader: BlocksReader = BlocksReader()
    private lateinit var camera: Camera

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sound_programming)
        prepareTopCodeListeners()
        prepareCamera()
    }

    private fun prepareTopCodeListeners() {
        blocksReader.topCodesListeners.add(boardSurfaceView)
        blocksReader.topCodesListeners.add(object : TopCodesChangedListener {
            override fun topCodesChanged(topCodes: List<TopCode>) {
                logText.text = String.format("%d", topCodes.size)
            }
        })
    }

    private fun prepareCamera() {
        val cameraListener: CameraListener = object : CameraListener {
            override fun onEachFrame(bitmap: Bitmap) {
                blocksReader.readBlocks(bitmap)
            }
        }
        camera = Camera(this, cameraListener, surfaceView)
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
        if (camera.isCameraOpen) {
            stopCamera()
        } else {
            startCamera()
        }
    }

    private fun startCamera() {
        btnStartStop.setText(R.string.stop)
        ScreenUtil.fullscreen(window)
        camera.openCamera()
    }

    private fun stopCamera() {
        btnStartStop.setText(R.string.start)
        ScreenUtil.exitFullscreen(window)
        camera.closeCamera()
    }
}
