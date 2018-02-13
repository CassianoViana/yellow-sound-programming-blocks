package com.viana.soundprogramming

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.view.Surface
import android.view.SurfaceView

class Camera {

    private val TAG: String = "Camera"

    var backgroundHandler = Handler(Handler.Callback {
        Log.i(TAG, it.toString())
        true
    })

    val onImageAvailable = ImageReader.OnImageAvailableListener { readBitmap(it) }

    var isCameraOpen = false
    private var cameraManager: CameraManager? = null
    private var cameraDevice: CameraDevice? = null
    private var cameraSession: CameraCaptureSession? = null
    private lateinit var cameraListener: CameraListener
    private lateinit var context: Context
    private lateinit var surfaceView: SurfaceView
    private lateinit var bitmapReader: BitmapReader

    private fun readBitmap(reader: ImageReader) {
        val image = reader.acquireLatestImage()
        image?.let {
            val bitmap = bitmapReader.readImage(it)
            it.close()
            cameraListener.onEachFrame(bitmap)
        }
    }

    fun prepare(context: Context, cameraListener: CameraListener, surfaceView: SurfaceView) {
        this.context = context
        this.bitmapReader = BitmapReader(context)
        this.cameraListener = cameraListener
        this.surfaceView = surfaceView
        cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    fun openCamera() {
        val cameraNotPermitted = ActivityCompat
                .checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
        if (cameraNotPermitted) {
            ActivityCompat.requestPermissions(context as Activity,
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_CODE_CAMERA_PERMISSION)
            return
        }

        try {
            val facingBackCameraId = getFacingBackCameraId() ?: return
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
                    val imageReader = ImageReader.newInstance(surfaceView.width, surfaceView.height,
                            ImageFormat.YUV_420_888, 2)
                    imageReader.setOnImageAvailableListener(onImageAvailable, backgroundHandler)

                    val surface = surfaceView.holder.surface
                    val imageReaderSurface = imageReader.surface
                    val surfaces = mutableListOf<Surface>()
                    surfaces.add(surface)
                    surfaces.add(imageReaderSurface)

                    val captureRequestBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_RECORD)
                    surfaces.forEach { captureRequestBuilder.addTarget(it) }

                    var captureRequest = captureRequestBuilder.build()

                    camera.createCaptureSession(surfaces, object : CameraCaptureSession.StateCallback() {
                        override fun onConfigured(session: CameraCaptureSession) {
                            try {
                                cameraSession = session
                                session.setRepeatingRequest(captureRequest,
                                        object : CameraCaptureSession.CaptureCallback() {}, backgroundHandler)
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

    fun closeCamera() = try {
        cameraSession?.stopRepeating()
        cameraDevice?.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }

    @Throws(CameraAccessException::class)
    private fun getFacingBackCameraId(): String? {
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

    interface CameraListener {
        fun onEachFrame(bitmap: Bitmap)
    }
}