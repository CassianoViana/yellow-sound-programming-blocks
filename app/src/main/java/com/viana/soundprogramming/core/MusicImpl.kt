package com.viana.soundprogramming.core

class MusicImpl : Music() {

    override fun clear() {
        sounds.clear()
    }

    override fun play() {
        sounds.forEach{
            it.play()
        }
    }

    override fun pause() {
        
    }
}