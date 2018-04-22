package com.viana.soundprogramming.core

class MusicImpl : Music() {

    override fun play() {
        sounds.forEach{
            it.play()
        }
    }
}