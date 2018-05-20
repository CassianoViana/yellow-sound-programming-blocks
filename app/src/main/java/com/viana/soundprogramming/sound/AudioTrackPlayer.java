package com.viana.soundprogramming.sound;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import org.jetbrains.annotations.Nullable;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class AudioTrackPlayer {
    private static final int HEADER_SIZE = 44;
    private static final int SAMPLE_RATE_IN_HZ = 44100;
    private static final int CHANNEL_OUT_MONO = AudioFormat.CHANNEL_OUT_MONO;
    private static final int ENCODING_PCM_16_BIT = AudioFormat.ENCODING_PCM_16BIT;
    private final int minBufferSize;
    private final int bufferSize;
    private AudioTrack audioTrack;
    private byte[] audioData;
    private float speedFactor = 1f;

    public AudioTrackPlayer() {
        minBufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE_IN_HZ, CHANNEL_OUT_MONO, ENCODING_PCM_16_BIT);
        bufferSize = 512;
        audioData = new byte[bufferSize];
    }

    public void addInterval(int seconds) {
        int sizeInBytes = seconds * SAMPLE_RATE_IN_HZ * 2;
        byte nothing[] = new byte[sizeInBytes];
        audioTrack.write(nothing, 0, sizeInBytes);
    }

    public void playWav(String filepath) {
        try {
            FileInputStream fin = new FileInputStream(filepath);
            playInputStream(fin);
            fin.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playInputStream(InputStream fin) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(fin);
        discardWavHeader(fin);
        int i;
        while ((i = bis.read(audioData, 0, bufferSize)) > -1) {
            audioTrack.write(audioData, 0, i);
        }
        bis.close();
    }

    public void playShortSamples(@Nullable short[] shorts) {
        audioTrack.write(shorts, 0, shorts.length);
    }

    public void playByteSamples(@Nullable byte[] bytes) {
        audioTrack.write(bytes, 44, bytes.length - 44);
    }

    public void onReachEnd(@Nullable short[] samples, final OnReachEndListener onReachEndListener) {

        float seconds = (float) samples.length / SAMPLE_RATE_IN_HZ;
        int frames = (int) (SAMPLE_RATE_IN_HZ * (seconds - 0.3));

        audioTrack.setNotificationMarkerPosition(frames);
        //audioTrack.setPositionNotificationPeriod(frames);
        audioTrack.setPlaybackPositionUpdateListener(new AudioTrack.OnPlaybackPositionUpdateListener() {

            @Override
            public void onMarkerReached(AudioTrack audioTrack) {
                onReachEndListener.reachedTheEnd();
            }


            @Override
            public void onPeriodicNotification(AudioTrack audioTrack) {

            }
        });
    }

    public void start() {
        if (audioTrack == null) {
            audioTrack = new AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    SAMPLE_RATE_IN_HZ, CHANNEL_OUT_MONO,
                    ENCODING_PCM_16_BIT,
                    minBufferSize,
                    AudioTrack.MODE_STREAM);
            audioTrack.play();
            audioTrack.setVolume(1f);
            audioTrack.setPlaybackRate((int) (44100 * speedFactor));
        }
    }

    public void stop() {
        if (audioTrack != null)
            if (audioTrack.getState() != AudioTrack.STATE_UNINITIALIZED)
                if (audioTrack.getPlayState() != AudioTrack.PLAYSTATE_STOPPED) {
                    audioTrack.stop();
                }
    }

    public void stopImmediately() {
        if (audioTrack != null)
            if (audioTrack.getState() != AudioTrack.STATE_UNINITIALIZED)
                if (audioTrack.getPlayState() != AudioTrack.PLAYSTATE_PAUSED) {
                    audioTrack.pause();
                    audioTrack.flush();
                }
    }

    public void release() {
        if (audioTrack != null) {
            audioTrack.release();
            audioTrack = null;
        }
    }

    private void discardWavHeader(InputStream fin) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(HEADER_SIZE);
        fin.read(buffer.array(), buffer.arrayOffset(), buffer.capacity());
        buffer.rewind();
    }


    public void setSpeedFactor(float speedFactor) {
        this.speedFactor = speedFactor;
    }

    public int getSessionId() {
        return audioTrack.getAudioSessionId();
    }


    public interface OnReachEndListener {
        void reachedTheEnd();
    }
}



