package com.viana.soundprogramming.core

import com.viana.soundprogramming.sound.Sound

class MusicSoundPool(musicBuilder: MusicBuilder) : Music(Sound.SoundPoolSoundBuilder(musicBuilder)) {
    override fun play(index: Int) {
        sounds.filter { it.index == index }.forEach { it.play() }
    }

    override fun prepare() {
    }
}
