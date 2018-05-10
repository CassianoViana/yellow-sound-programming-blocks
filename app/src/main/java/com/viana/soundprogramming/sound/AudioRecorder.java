package com.viana.soundprogramming.sound;

import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.IOException;

import omrecorder.*;
import omrecorder.Recorder;

public class AudioRecorder {

    private Recorder wavRecorder;

    public AudioRecorder() {
    }

    public void record() {
        wavRecorder = createWavRecorder(file());
        wavRecorder.startRecording();
    }

    public void record(File file) {
        wavRecorder = createWavRecorder(file);
        wavRecorder.startRecording();
    }

    public void stop() {
        try {
            wavRecorder.stopRecording();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    PullableSource.AutomaticGainControl mic2() {
        return new PullableSource.AutomaticGainControl(
                //new PullableSource.NoiseSuppressor(
                        new PullableSource.Default(
                                new AudioRecordConfig.Default(
                                        MediaRecorder.AudioSource.MIC, AudioFormat.ENCODING_PCM_16BIT,
                                        AudioFormat.CHANNEL_IN_STEREO, 44100
                                )
                        )
                //)
        );
    }

    omrecorder.Recorder createWavRecorder(File file) {
        return OmRecorder.wav(
                new PullTransport.Default(mic2(), new PullTransport.OnAudioChunkPulledListener() {
                    @Override
                    public void onAudioChunkPulled(AudioChunk audioChunk) {
                        animateVoice((float) (audioChunk.maxAmplitude() / 200.0));
                    }
                }), file);
    }

    private PullableSource mic() {
        return new PullableSource.Default(
                new AudioRecordConfig.Default(
                        MediaRecorder.AudioSource.MIC, AudioFormat.ENCODING_PCM_16BIT,
                        AudioFormat.CHANNEL_IN_STEREO, 44100
                )
        );
    }

    private void animateVoice(float v) {

    }

    @NonNull
    private File file() {
        return new File(Environment.getExternalStorageDirectory(), "demo.wav");
    }
}