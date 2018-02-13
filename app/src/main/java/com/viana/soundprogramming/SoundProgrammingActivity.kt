package com.viana.soundprogramming

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_sound_programming.*
import topcodes.Scanner

const val REQUEST_CODE_CAMERA_PERMISSION = 100

class SoundProgrammingActivity : AppCompatActivity() {

    private val camera: Camera = Camera()
    private val topCodesScanner: Scanner = Scanner()
    private var topCodesListeners = mutableListOf<TopCodesChangedListener>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sound_programming)
        topCodesListeners.add(boardSurfaceView)
        camera.prepare(context = this,
                surfaceView = surfaceView,
                cameraListener = object : Camera.CameraListener {
                    override fun onEachFrame(bitmap: Bitmap) {
                        readTopCodes(bitmap)
                    }
                })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_CAMERA_PERMISSION
                && grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            camera.openCamera()
    }

    fun onClickStartStop(view: View) {
        if (camera.isCameraOpen) {
            btnStartStop.setText(R.string.stop)
            ScreenUtil.fullscreen(window)
            camera.openCamera()
        } else {
            btnStartStop.setText(R.string.start)
            ScreenUtil.exitFullscreen(window)
            camera.closeCamera()
        }
        camera.isCameraOpen = !camera.isCameraOpen
    }

    private fun readTopCodes(bitmap: Bitmap) {
        val topCodes = topCodesScanner.scan(bitmap)
        topCodesListeners.forEach {
            it.topCodesChanged(topCodes)
        }
    }
}
