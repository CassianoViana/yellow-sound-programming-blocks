package com.viana.soundprogramming.sound;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AudioSequencer {

    private AudioTrackPlayer player;
    private ByteArrayOutputStream musicStream;

    public AudioSequencer() {
        musicStream = new ByteArrayOutputStream();
        player = new AudioTrackPlayer();
    }

    public void clear() {
        try {
            musicStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void add(InputStream soundStream) {
        try {
            concatStream(soundStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void play() {
        try {
            player.start();
            player.playInputStream(new ByteArrayInputStream(musicStream.toByteArray()));
            player.stop();
            player.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
    }

    private void concatStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer)) > -1) {
            musicStream.write(buffer, 0, len);
        }
        inputStream.reset();
        musicStream.flush();
    }
}