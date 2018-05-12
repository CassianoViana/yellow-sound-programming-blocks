package com.viana.soundprogramming.sound;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.content.ContentValues.TAG;

public class CyanogenAudioRecorder {

    public static String EXTENSION = ".pcm";
    private boolean isRecording = true;
    private String recordingFilePath;

    private static final int SAMPLING_RATE = 44100;
    private static final int CHANNEL_IN = AudioFormat.CHANNEL_IN_DEFAULT;
    private static final int FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLING_RATE,
            CHANNEL_IN, FORMAT);

    private AudioRecord mRecord;
    private byte[] mData;

    public void startRecording(String recordingFilePath) {
        this.recordingFilePath = recordingFilePath + EXTENSION;
        try {
            mRecord = new AudioRecord(MediaRecorder.AudioSource.DEFAULT,
                    SAMPLING_RATE, CHANNEL_IN, FORMAT, BUFFER_SIZE);
            mData = new byte[BUFFER_SIZE];
            mRecord.startRecording();
            this.isRecording = true;
            startRecordingThread();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void startRecordingThread() {
        Thread mRecordThread = new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedOutputStream out = null;
                try {
                    out = new BufferedOutputStream(new FileOutputStream(recordingFilePath));
                    while (isRecording) {
                        int mStatus = mRecord.read(mData, 0, mData.length);
                        if (mStatus == AudioRecord.ERROR_INVALID_OPERATION ||
                                mStatus == AudioRecord.ERROR_BAD_VALUE) {
                            Log.e(TAG, "Error reading audio record data");
                            return;
                        }
                        out.write(mData, 0, BUFFER_SIZE);
                    }
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                } finally {
                    closeQuietly(out);
                }
            }
        });
        mRecordThread.start();
    }

    private void closeQuietly(Closeable out) {
        try {
            if (out != null)
                out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        isRecording = false;
        mRecord.stop();
        mRecord.release();
        String mOutFilePath = recordingFilePath.replace(EXTENSION, PcmWavConverter.WAV_EXTENSION);
        PcmWavConverter.convertToWave(mOutFilePath, BUFFER_SIZE);
    }
}