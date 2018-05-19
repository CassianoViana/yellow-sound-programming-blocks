package com.viana.soundprogramming.sound;

import java.util.ArrayList;
import java.util.List;

public class AudioMixer {

    private int totalSamples;
    private long musicMilliseconds;
    private List<byte[]> sounds;
    private float volumeFactor = 1f;

    public AudioMixer() {
        sounds = new ArrayList<>();
    }

    public AudioMixer(long musicMilliseconds, int sampleRate, float volumeFactor) {
        this();
        this.setup(musicMilliseconds, sampleRate, volumeFactor);
    }

    public void setup(long musicMilliseconds, int sampleRate, float volumeFactor) {
        this.musicMilliseconds = musicMilliseconds;
        this.volumeFactor = volumeFactor;
        sounds.clear();
        totalSamples = (int) ((musicMilliseconds / 1000) * sampleRate);
    }

    public void addSound(long momentInMillis, byte[] sound) {
        byte[] expandedSound = new byte[totalSamples];
        int i = 0;
        int isound = 0;
        while (i < totalSamples) {
            expandedSound[i] = 0;
            if (i >= (momentInMillis * totalSamples) / (musicMilliseconds / 1000)) {
                if (isound < sound.length)
                    expandedSound[i] = sound[isound++];
            }
            i++;
        }
        sounds.add(expandedSound);
    }

    public byte[] mixAddedSounds() {
        byte mixedSamples[] = new byte[totalSamples];
        int isample = 0;
        while (isample < totalSamples) {
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
