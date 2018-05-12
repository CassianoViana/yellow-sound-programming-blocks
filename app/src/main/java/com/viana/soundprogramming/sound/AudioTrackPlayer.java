package com.viana.soundprogramming.sound;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

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
    private final byte[] audioData;

    public AudioTrackPlayer() {
        minBufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE_IN_HZ, CHANNEL_OUT_MONO, ENCODING_PCM_16_BIT);
        bufferSize = 512;
        audioData = new byte[bufferSize];
    }

    public void addInterval(int seconds) {
        int sizeInBytes = seconds * SAMPLE_RATE_IN_HZ;
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

    public void start() {
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE_IN_HZ, CHANNEL_OUT_MONO, ENCODING_PCM_16_BIT, minBufferSize, AudioTrack.MODE_STREAM);
        audioTrack.play();
        audioTrack.setVolume(1f);
    }

    public void stop() {
        audioTrack.stop();
        audioTrack.release();
    }

    private void discardWavHeader(InputStream fin) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(HEADER_SIZE);
        fin.read(buffer.array(), buffer.arrayOffset(), buffer.capacity());
        buffer.rewind();
    }
}
