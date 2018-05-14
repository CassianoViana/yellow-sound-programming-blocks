package com.viana.soundprogramming.sound;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AudioSequencer {

    private List<InputStream> music;
    private AudioTrackPlayer player;

    public AudioSequencer() {
        music = new ArrayList<>();
        player = new AudioTrackPlayer();
    }

    public void add(InputStream soundStream) {
        this.music.add(soundStream);
    }

    public void play() {
        try {
            player.start();
            for (InputStream inputStream : music) {
                player.playInputStream(inputStream);
            }
            player.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}