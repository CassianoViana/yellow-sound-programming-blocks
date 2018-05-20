package com.viana.soundprogramming.sound;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class AudioMixer {

    private int totalBytes;
    private float seconds;
    private List<byte[]> sounds;
    private float volumeFactor = 1f;
    private static final int SAMPLE_RATE = 44100;

    public AudioMixer() {
        sounds = new ArrayList<>();
    }

    public AudioMixer(long seconds, float volumeFactor) {
        this();
        this.setup(seconds, volumeFactor);
    }

    public void setup(float seconds, float volumeFactor) {
        this.seconds = seconds;
        this.volumeFactor = volumeFactor;
        sounds.clear();
        totalBytes = (int) (this.seconds * SAMPLE_RATE * 2);
    }

    public void addSound(float second, byte[] sound) {
        Log.i("AudioMixer", "second: " + second);
        byte[] expandedSound = new byte[totalBytes];
        int i = 0;
        int isound = 0;
        while (i < totalBytes) {
            expandedSound[i] = 0;
            if (i >= second * ((float) totalBytes / seconds)) {
                if (isound < sound.length)
                    expandedSound[i] = sound[isound++];
            }
            i++;
        }
        sounds.add(expandedSound);
    }

    public byte[] mixAddedSounds() {
        byte mixedSamples[] = new byte[totalBytes];
        int isample = 0;
        while (isample < totalBytes) {
            float mixedSample = 0;
            for (int isound = 0; isound < sounds.size(); isound++) {
                mixedSample += (float) sounds.get(isound)[isample] / 128;
            }
            mixedSample *= volumeFactor;
            // hard clipping
            if (mixedSample > 1.0f) mixedSample = 1;
            if (mixedSample < -1.0f) mixedSample = -1;

            mixedSamples[isample] = (byte) (mixedSample * 128);
            isample++;
        }
        return mixedSamples;
    }
}
