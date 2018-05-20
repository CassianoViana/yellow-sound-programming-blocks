package com.viana.soundprogramming;

import com.viana.soundprogramming.sound.AudioMixer;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void mix_isCorrect() throws Exception {

        AudioMixer audioMixer = new AudioMixer(1000, 1);
        audioMixer.addSound(0, new byte[]{127, 127, 127, 127});
        audioMixer.addSound(500, new byte[]{127, 127, 127, 127});
        audioMixer.addSound(800, new byte[]{127, 127});
        byte[] mixed = audioMixer.mixAddedSounds();

        assertEquals("127,127,127,127,0,127,127,127,127,127,", bytesToStr(mixed));

    }

    @Test
    public void mix3_isCorrect() throws Exception {
        AudioMixer audioMixer = new AudioMixer(1000, 1);
        audioMixer.addSound(0, new byte[]{127, 127, 127, 127});
        byte[] mixed = audioMixer.mixAddedSounds();
        assertEquals("127,127,127,127,0,0,0,0,0,0,", bytesToStr(mixed));
    }

    @Test
    public void mix2_isCorrect() throws Exception {

        AudioMixer audioMixer = new AudioMixer(1000, 1);
        audioMixer.addSound(0, new byte[]{1, 1, 1, 1});
        audioMixer.addSound(500, new byte[]{0, 1, 1, 1});
        audioMixer.addSound(500, new byte[]{10, 1, 1, 1});
        byte[] mixed = audioMixer.mixAddedSounds();

        assertEquals("1111051110", bytesToStr(mixed));

    }

    private String bytesToStr(byte[] bytes) {
        StringBuilder s = new StringBuilder();
        for (byte aByte : bytes) {
            s.append(aByte);
            s.append(',');
        }
        return s.toString();
    }
}