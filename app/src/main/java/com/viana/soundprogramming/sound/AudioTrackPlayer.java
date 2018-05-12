package com.viana.soundprogramming.sound;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.audiofx.PresetReverb;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by cassiano on 5/11/18.
 */

public class AudioTrackPlayer {
    private static final int HEADER_SIZE = 44;

    public void play(String filepath) {

        int minBufferSize = AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT);
        int bufferSize = 512;
        AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, minBufferSize, AudioTrack.MODE_STREAM);

        int i = 0;
        byte[] s = new byte[bufferSize];
        try {
            FileInputStream fin = new FileInputStream(filepath);
            DataInputStream dis = new DataInputStream(fin);
            ignoreHeaderWav(fin);
            audioTrack.play();

            PresetReverb reverb = new  PresetReverb(0, audioTrack.getAudioSessionId());
            reverb.setPreset( PresetReverb.PRESET_PLATE);
            reverb.setEnabled(true);
            audioTrack.attachAuxEffect(reverb.getId());
            audioTrack.setAuxEffectSendLevel(1);

            while ((i = dis.read(s, 0, bufferSize)) > -1) {
                audioTrack.write(s, 0, i);
            }
            audioTrack.stop();
            audioTrack.release();
            dis.close();
            fin.close();

        } catch (FileNotFoundException e) {
            // TODO
            e.printStackTrace();
        } catch (IOException e) {
            // TODO
            e.printStackTrace();
        }
    }

    private void ignoreHeaderWav(FileInputStream fin) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(HEADER_SIZE);
        fin.read(buffer.array(), buffer.arrayOffset(), buffer.capacity());
        buffer.rewind();
    }
}
