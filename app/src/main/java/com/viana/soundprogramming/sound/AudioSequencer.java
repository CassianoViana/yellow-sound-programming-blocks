package com.viana.soundprogramming.sound;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class AudioSequencer {

    private AudioTrackPlayer player;
    private AudioMixer audioMixer;

    public AudioSequencer() {
        audioMixer = new AudioMixer();
        player = new AudioTrackPlayer();
    }

    public void setup(int secondsToTraverseWidth) {
        audioMixer.setup(secondsToTraverseWidth, 44100 * 2, 1);
    }

    public void add(long millisecond, InputStream soundStream) {
        try {
            audioMixer.addSound((int) millisecond, IOUtils.toByteArray(soundStream));
            soundStream.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void play() {
        try {
            byte[] bytes = audioMixer.mixAddedSounds();
            player.start();
            player.playInputStream(new ByteArrayInputStream(bytes));
            player.stop();
            player.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
    }
}