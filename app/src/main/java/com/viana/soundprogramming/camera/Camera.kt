package com.viana.soundprogramming.camera

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
import com.viana.soundprogramming.REQUEST_CODE_CAMERA_PERMISSION

class Camera(
        private var context: Context,
        private var cameraListener: CameraListener,
        private var surfaceView: SurfaceView,
        private val TAG: String = "Camera"
) {

    private var backgroundHandler = Handler(Handler.Callback {
        Log.i(TAG, it.toString())
        true
    })
    var isCameraOpen = false

    private lateinit var cameraDevice: CameraDevice
    private lateinit var imageReader: ImageReader
    private lateinit var cameraSession: CameraCaptureSession
    private lateinit var captureRequest: CaptureRequest
    private val cameraManager: CameraManager = context
            .getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private val bitmapReader: BitmapReader = BitmapReader(context)

    private val onImageAvailable = ImageReader
            .OnImageAvailableListener { readBitmap(it) }

    private val surfaces: MutableList<Surface> = mutableListOf()

    private fun readBitmap(reader: ImageReader) {
        val image = reader.acquireLatestImage()
        image?.let {
            val bitmap = bitmapReader.readImage(it)
            it.close()
            cameraListener.onEachFrame(bitmap)
        }
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
            cameraManager.openCamera(facingBackCameraId, object : CameraDevice.StateCallback() {
                override fun onOpened(camera: CameraDevice) {
                    cameraDevice = camera
                    startCameraSession(camera)
                    isCameraOpen = true
                }

                override fun onDisconnected(camera: CameraDevice) {
                    Log.e(TAG, "onDisconnected")
                }

                override fun onError(camera: CameraDevice, error: Int) {
                    Log.e(TAG, "onError")
                }

                private fun startCameraSession(camera: CameraDevice) = try {
                    prepareImageReader()
                    createTargetSurfaces()
                    createCaptureRequest()
                    camera.createCaptureSession(surfaces,
                            object : CameraCaptureSession.StateCallback() {
                                override fun onConfigured(session: CameraCaptureSession) {
                                    cameraSession = session
                                    startRepeatingSessionRequestToCamera()
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

    private fun createTargetSurfaces(){
        surfaces.clear()
        val surface = surfaceView.holder.surface
        val imageReaderSurface = imageReader.surface
        surfaces.add(surface)
        imageReaderSurface?.let { surfaces.add(it) }
    }

    private fun createCaptureRequest() {
        cameraDevice.let {
            val captureRequestBuilder = it.createCaptureRequest(CameraDevice.TEMPLATE_RECORD)
            surfaces.forEach { captureRequestBuilder.addTarget(it) }
            captureRequest = captureRequestBuilder.build()
        }
    }

    private fun startRepeatingSessionRequestToCamera() {
        try {
            cameraSession.setRepeatingRequest(captureRequest,
                    object : CameraCaptureSession.CaptureCallback() {}, backgroundHandler)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun prepareImageReader() {
        imageReader = ImageReader.newInstance(surfaceView.width, surfaceView.height,
                ImageFormat.YUV_420_888, 2)
        imageReader.setOnImageAvailableListener(onImageAvailable, backgroundHandler)
    }

    fun closeCamera() = try {
        imageReader.close()
        cameraSession.abortCaptures()
        cameraSession.stopRepeating()
        cameraDevice.close()
        isCameraOpen = false
    } catch (e: Exception) {
        e.printStackTrace()
    }

    @Throws(CameraAccessException::class)
    private fun getFacingBackCameraId(): String? {
        var facingBackCameraId: String? = null
        cameraManager.let {
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

}

interface CameraListener {
    fun onEachFrame(bitmap: Bitmap)
}