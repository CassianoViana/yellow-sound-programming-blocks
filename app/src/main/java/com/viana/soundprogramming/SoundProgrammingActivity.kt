package com.viana.soundprogramming

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageFormat.YUV_420_888
import android.hardware.camera2.*
import android.media.Image
import android.media.ImageReader
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Surface
import android.view.View
import kotlinx.android.synthetic.main.activity_sound_programming.*
import topcodes.Scanner



class SoundProgrammingActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_CODE_CAMERA_PERMISSION = 100
        const val TAG = "SoundProgramming"

        private val backgroundHandler = @SuppressLint("HandlerLeak")
        object : Handler() {

        }
    }

    private var cameraOpen = false
    private var cameraManager: CameraManager? = null
    private var scanner: Scanner? = null
    private var bitmap: Bitmap? = null
    private var image: Image? = null
    private var cameraDevice: CameraDevice? = null
    private var cameraSession: CameraCaptureSession? = null
    private var bitmapReader: BitmapReader? = null
    private var topCodesListeners = mutableListOf<TopCodesChangedListener>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sound_programming)
        scanner = Scanner()
        bitmapReader = BitmapReader(this)
        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        topCodesListeners.add(boardSurfaceView)
    }

    fun startStop(view: View) {
        if (!cameraOpen) {
            btnStartStop.setText(R.string.stop)
            ScreenUtil.fullscreen(window)
            openCamera()
        } else {
            btnStartStop.setText(R.string.start)
            ScreenUtil.exitFullscreen(window)
            closeCamera()
        }
        cameraOpen = !cameraOpen
    }

    private val imageAvailableListener = ImageReader.OnImageAvailableListener { reader ->
        try {
            backgroundHandler.post {
                readBitmap(reader)
                readTopcodes()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val facingBackCameraId: String?
        @Throws(CameraAccessException::class)
        get() {
            var facingBackCameraId: String? = null
            cameraManager?.let {
                val cameraIdList = it.cameraIdList
                if (cameraIdList.isNotEmpty()) {
                    for (id in cameraIdList) {
                        val facing = it.getCameraCharacteristics(id).get(CameraCharacteristics.LENS_FACING)
                        if (facing == CameraCharacteristics.LENS_FACING_BACK) {
                            facingBackCameraId = id
                        }
                    }
                }
            }
            return facingBackCameraId
        }


    private fun openCamera() {
        val cameraNotPermitted = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
        if (cameraNotPermitted) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_CODE_CAMERA_PERMISSION)
            return
        }

        try {
            val facingBackCameraId = facingBackCameraId ?: return
            cameraManager!!.openCamera(facingBackCameraId, object : CameraDevice.StateCallback() {
                override fun onOpened(camera: CameraDevice) {
                    cameraDevice = camera
                    startCameraSession(camera)
                }

                override fun onDisconnected(camera: CameraDevice) {
                    Log.e(TAG, "onDisconnected")
                }

                override fun onError(camera: CameraDevice, error: Int) {
                    Log.e(TAG, "onError")
                }

                private fun startCameraSession(camera: CameraDevice) = try {
                    val surface = surfaceView.holder.surface

                    val imageReader = ImageReader.newInstance(surfaceView!!.width, surfaceView!!.height, YUV_420_888, 1)
                    val imageReaderSurface = imageReader.surface

                    val captureRequestBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_RECORD)
                    imageReader.setOnImageAvailableListener(imageAvailableListener, backgroundHandler)
                    captureRequestBuilder.addTarget(surface)
                    captureRequestBuilder.addTarget(imageReaderSurface)
                    var captureRequest = captureRequestBuilder.build()

                    val surfaces = mutableListOf<Surface>()
                    surfaces.add(surface)
                    surfaces.add(imageReaderSurface)

                    camera.createCaptureSession(surfaces, object : CameraCaptureSession.StateCallback() {
                        override fun onConfigured(session: CameraCaptureSession) {
                            try {
                                cameraSession = session
                                session.setRepeatingRequest(captureRequest, object : CameraCaptureSession.CaptureCallback() {
                                    override fun onCaptureFailed(session: CameraCaptureSession?, request: CaptureRequest?, failure: CaptureFailure?) {
                                        Log.e(TAG, "onCaptureFailed")
                                    }

                                    override fun onCaptureSequenceAborted(session: CameraCaptureSession?, sequenceId: Int) {
                                        Log.e(TAG, "onCaptureSequenceAborted")
                                    }
                                }, backgroundHandler)
                            } catch (e: CameraAccessException) {
                                e.printStackTrace()
                            }
                        }

                        override fun onConfigureFailed(session: CameraCaptureSession) {
                            Log.e(TAG, "onConfigureFailed")
                        }
                    }, backgroundHandler)
                } catch (e: CameraAccessException) {
                    e.printStackTrace()
                }

            }, backgroundHandler)

        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun closeCamera() = try {
        cameraSession?.stopRepeating()
        cameraDevice?.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            }
        }
    }

    private fun readBitmap(reader: ImageReader) {
        image = reader.acquireLatestImage()
        image?.let {
            bitmap = bitmapReader?.readImage(it)
            it.close()
        }
    }

    private fun readTopcodes() {
        bitmap?.let {
            val topCodes = scanner!!.scan(it)
            topCodesListeners.forEach {
                it.topCodesChanged(topCodes)
            }
        }
    }
}

