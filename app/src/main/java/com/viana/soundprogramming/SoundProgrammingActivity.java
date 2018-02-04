package com.viana.soundprogramming;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import topcodes.Scanner;
import topcodes.TopCode;

import static android.graphics.ImageFormat.YUV_420_888;


public class SoundProgrammingActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_CAMERA_PERMISSION = 100;
    private static final String TAG = "SoundActivity";
    private SurfaceView textureView;
    private ByteBuffer buffer;
    private CameraManager cameraManager;
    private Scanner scanner;

    private Handler backgroundHandler = new Handler() {

    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_programming);
        this.textureView = (SurfaceView) findViewById(R.id.textureView);
        scanner = new Scanner();
    }

    public void start(View view) {
        openCamera();
    }

    private Bitmap bitmap;
    Image image;

    private ImageReader.OnImageAvailableListener imageAvailableListener = new ImageReader.OnImageAvailableListener() {


        @Override
        public void onImageAvailable(final ImageReader reader) {
            try {
                backgroundHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        readBitmap(reader);
                        readTopcodes();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void openCamera() {
        boolean cameraNotPermitted = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED;
        if (cameraNotPermitted) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CODE_CAMERA_PERMISSION);
            return;
        }

        try {
            cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

            String facingBackCameraId = getFacingBackCameraId();
            if (facingBackCameraId != null) {
                cameraManager.openCamera(facingBackCameraId, new CameraDevice.StateCallback() {
                    @Override
                    public void onOpened(@NonNull CameraDevice camera) {
                        configureAndStart(camera);
                    }

                    private void configureAndStart(@NonNull CameraDevice camera) {
                        try {
                            Surface surface = textureView.getHolder().getSurface();

                            final ImageReader imageReader = ImageReader.newInstance(textureView.getWidth(), textureView.getHeight(), YUV_420_888, 2);
                            Surface imageReaderSurface = imageReader.getSurface();

                            final CaptureRequest.Builder captureRequestBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);

                            imageReader.setOnImageAvailableListener(imageAvailableListener, backgroundHandler);

                            captureRequestBuilder.addTarget(surface);
                            captureRequestBuilder.addTarget(imageReaderSurface);
                            final CaptureRequest captureRequest = captureRequestBuilder.build();

                            List<Surface> surfaces = new ArrayList<>();
                            surfaces.add(surface);
                            surfaces.add(imageReaderSurface);

                            camera.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {
                                @Override
                                public void onConfigured(@NonNull CameraCaptureSession session) {
                                    try {
                                        //session.stopRepeating()
                                        session.setRepeatingRequest(captureRequest, new CameraCaptureSession.CaptureCallback() {
                                            @Override
                                            public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
                                                super.onCaptureStarted(session, request, timestamp, frameNumber);
                                            }

                                            @Override
                                            public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
                                                super.onCaptureProgressed(session, request, partialResult);
                                            }

                                            @Override
                                            public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                                                super.onCaptureCompleted(session, request, result);
                                            }

                                            @Override
                                            public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
                                                super.onCaptureFailed(session, request, failure);
                                            }

                                            @Override
                                            public void onCaptureSequenceCompleted(@NonNull CameraCaptureSession session, int sequenceId, long frameNumber) {
                                                super.onCaptureSequenceCompleted(session, sequenceId, frameNumber);
                                            }

                                            @Override
                                            public void onCaptureSequenceAborted(@NonNull CameraCaptureSession session, int sequenceId) {
                                                super.onCaptureSequenceAborted(session, sequenceId);
                                            }

                                            @Override
                                            public void onCaptureBufferLost(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull Surface target, long frameNumber) {
                                                super.onCaptureBufferLost(session, request, target, frameNumber);
                                            }
                                        }, backgroundHandler);
                                    } catch (CameraAccessException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                                }
                            }, backgroundHandler);
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onDisconnected(@NonNull CameraDevice camera) {

                    }

                    @Override
                    public void onError(@NonNull CameraDevice camera, int error) {

                    }
                }, backgroundHandler);
            }


        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_CAMERA_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            }
        }
    }

    @Nullable
    private String getFacingBackCameraId() throws CameraAccessException {
        String facingBackCameraId = null;
        String[] cameraIdList = cameraManager.getCameraIdList();
        if (cameraIdList.length > 0) {

            for (String id : cameraIdList) {
                Integer facing = cameraManager.getCameraCharacteristics(id).get(CameraCharacteristics.LENS_FACING);
                if (facing == CameraCharacteristics.LENS_FACING_BACK) {
                    facingBackCameraId = id;
                }
            }

        }
        return facingBackCameraId;
    }

    private void readTopcodes() {
        Log.i(TAG, String.valueOf(textureView.isHardwareAccelerated()));
        if (bitmap != null) {
            List<TopCode> topCodes = scanner.scan(bitmap);
            if (!topCodes.isEmpty()) {
                Log.i("TopCodes", String.valueOf(topCodes.size()));
                Toast.makeText(this, String.valueOf("Read topcodes: " + topCodes.size()), Toast.LENGTH_LONG).show();
            }
        }
    }

    void readBitmap(ImageReader reader) {
        image = reader.acquireLatestImage();
        bitmap = BitmapReader.readImage(image, this);
        image.close();
        Log.i(TAG, String.valueOf(bitmap));
    }
}
