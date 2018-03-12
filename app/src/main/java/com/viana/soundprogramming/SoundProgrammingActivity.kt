package com.viana.soundprogramming

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.viana.soundprogramming.blocks.BlocksReader
import com.viana.soundprogramming.camera.Camera
import com.viana.soundprogramming.camera.CameraListener
import com.viana.soundprogramming.camera.ScreenUtil
import com.viana.soundprogramming.camera.TopCodesChangedListener
import kotlinx.android.synthetic.main.activity_sound_programming.*
import topcodes.TopCode

const val REQUEST_CODE_CAMERA_PERMISSION = 100

class SoundProgrammingActivity : AppCompatActivity() {

    private lateinit var camera: Camera
    private val blocksReader: BlocksReader = BlocksReader()

    private val cameraListener = object : CameraListener {
        override fun onEachFrame(bitmap: Bitmap) {
            blocksReader.readBlocks(bitmap)
        }
    }

    private val topCodesChangedLogListener = object : TopCodesChangedListener {
        override fun topCodesChanged(topCodes: List<TopCode>) {
            logText.text = String.format("%d", topCodes.size)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sound_programming)
        prepareTopCodeListeners()
        prepareCamera()
    }

    private fun prepareTopCodeListeners() {
        blocksReader.topCodesListeners.add(boardSurfaceView.blocksManager)
        blocksReader.topCodesListeners.add(topCodesChangedLogListener)
    }

    private fun prepareCamera() {
        val cameraListener: CameraListener = cameraListener
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
        if (!camera.isCameraOpen) startCamera() else stopCamera()
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