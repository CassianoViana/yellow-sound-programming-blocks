package com.viana.soundprogramming

import android.Manifest
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
import android.view.Surface
import android.view.View
import kotlinx.android.synthetic.main.activity_sound_programming.*
import topcodes.Scanner

class SoundProgrammingActivity : AppCompatActivity() {

    companion object {
        val REQUEST_CODE_CAMERA_PERMISSION = 100
        private val backgroundHandler = object : Handler() {

        }
    }

    private var cameraManager: CameraManager? = null
    private var scanner: Scanner? = null
    private var bitmap: Bitmap? = null
    private var image: Image? = null
    private var bitmapReader:BitmapReader? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sound_programming)
        scanner = Scanner()
    }

    fun start(view: View) {
        bitmapReader = BitmapReader(this)
        openCamera()
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
            cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager

            val facingBackCameraId = facingBackCameraId
            if (facingBackCameraId != null) {
                cameraManager!!.openCamera(facingBackCameraId, object : CameraDevice.StateCallback() {
                    override fun onOpened(camera: CameraDevice) {
                        configureAndStart(camera)
                    }

                    private fun configureAndStart(camera: CameraDevice) = try {
                        val surface = textureView!!.holder.surface

                        val imageReader = ImageReader.newInstance(textureView!!.width, textureView!!.height, YUV_420_888, 2)
                        val imageReaderSurface = imageReader.surface

                        val captureRequestBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_RECORD)

                        imageReader.setOnImageAvailableListener(imageAvailableListener, backgroundHandler)

                        captureRequestBuilder.addTarget(surface)
                        captureRequestBuilder.addTarget(imageReaderSurface)
                        val captureRequest = captureRequestBuilder.build()

                        val surfaces = mutableListOf<Surface>()
                        surfaces.add(surface)
                        surfaces.add(imageReaderSurface)

                        camera.createCaptureSession(surfaces, object : CameraCaptureSession.StateCallback() {
                            override fun onConfigured(session: CameraCaptureSession) {
                                try {
                                    //session.stopRepeating()
                                    session.setRepeatingRequest(captureRequest, object : CameraCaptureSession.CaptureCallback() {
                                    }, backgroundHandler)
                                } catch (e: CameraAccessException) {
                                    e.printStackTrace()
                                }
                            }

                            override fun onConfigureFailed(session: CameraCaptureSession) {

                            }
                        }, backgroundHandler)
                    } catch (e: CameraAccessException) {
                        e.printStackTrace()
                    }

                    override fun onDisconnected(camera: CameraDevice) {

                    }

                    override fun onError(camera: CameraDevice, error: Int) {

                    }
                }, backgroundHandler)
            }


        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            }
        }
    }

    private fun readTopcodes() {
        if (bitmap != null) {
            val topCodes = scanner!!.scan(bitmap)
            if (!topCodes.isEmpty()) {
                logText!!.text = ("Read topcodes: " + topCodes.size)
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
}
