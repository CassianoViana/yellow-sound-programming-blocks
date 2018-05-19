package com.viana.soundprogramming.sound;

import java.util.ArrayList;
import java.util.List;

public class AudioMixer {

    private final int totalSamples;
    private int musicSeconds;
    private List<byte[]> sounds;
    private float volumeReduction = 1f;

    public AudioMixer(int musicSeconds, int sampleRate, float volumeReduction) {
        this.musicSeconds = musicSeconds;
        this.volumeReduction = volumeReduction;
        sounds = new ArrayList<>();
        totalSamples = musicSeconds * sampleRate;
    }

    public void addSound(float second, byte[] sound) {
        byte[] expandedSound = new byte[totalSamples];
        int i = 0;
        int isound = 0;
        while (i < totalSamples) {
            expandedSound[i] = 0;
            if (i >= second * totalSamples / musicSeconds) {
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
            mixedSample *= volumeReduction;
            // hard clipping
            if (mixedSample > 1.0f) mixedSample = 1;
            if (mixedSample < -1.0f) mixedSample = -1;

            mixedSamples[isample] = (byte) (mixedSample * 128);
            isample++;
        }
        return mixedSamples;
    }
}
