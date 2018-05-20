package com.viana.soundprogramming.sound;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class AudioMixerShort {

    public static final float SHORT = 32768.0f;
    private int totalShorts;
    private float seconds;
    private List<short[]> sounds;
    private float volumeFactor = 1f;
    private static final int SAMPLE_RATE = 44100;

    public AudioMixerShort() {
        sounds = new ArrayList<>();
    }

    public AudioMixerShort(long seconds, float volumeFactor) {
        this();
        this.setup(seconds, volumeFactor);
    }

    public void setup(float seconds, float volumeFactor) {
        this.seconds = seconds;
        this.volumeFactor = volumeFactor;
        sounds.clear();
        totalShorts = (int) (this.seconds * SAMPLE_RATE * 2);
    }

    public void addSound(float second, short[] sound) {
        Log.i("AudioMixer", "second: " + second);
        short[] expandedSound = new short[totalShorts];
        int i = 0;
        int isound = 0;
        while (i < totalShorts) {
            expandedSound[i] = 0;
            if (i >= second * ((float) totalShorts / seconds)) {
                if (isound < sound.length)
                    expandedSound[i] = sound[isound++];
            }
            i++;
        }
        sounds.add(expandedSound);
    }

    public short[] mixAddedSounds() {
        short mixedSamples[] = new short[totalShorts];
        int isample = 0;
        while (isample < totalShorts) {
            float mixedSample = 0;
            for (int isound = 0; isound < sounds.size(); isound++) {
                mixedSample += (float) sounds.get(isound)[isample] / SHORT;
            }
            mixedSample *= volumeFactor;
            // hard clipping
            if (mixedSample > 1.0f) mixedSample = 1;
            if (mixedSample < -1.0f) mixedSample = -1;

            mixedSamples[isample] = (short) (mixedSample * SHORT);
            isample++;
        }
        return mixedSamples;
    }

    /*public short[] makeChimeraAll(int offset) {
        //bigData and littleData are each short arrays, populated elsewhere
        int intBucket = 0;
        for (int i = offset; i < bigData.length; i++) {
            if (i < littleData.length) {
                intBucket = bigData[i] + littleData[i];
                if (intBucket > SIGNED_SHORT_MAX) {
                    intBucket = SIGNED_SHORT_MAX;
                } else if (intBucket < SIGNED_SHORT_MIN) {
                    intBucket = SIGNED_SHORT_MIN;
                }
                bigData[i] = (short) intBucket;
            } else {
                //leave bigData alone
            }
        }
        return bigData;
    }*/

}
