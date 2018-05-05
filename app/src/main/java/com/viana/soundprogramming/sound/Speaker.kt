package com.viana.soundprogramming.sound

import com.viana.soundprogramming.R

class Speaker {

    private val phrases = mutableMapOf<Int, Int>()

    fun load() {
        add(R.raw.vamos_programar)
        add(R.raw.a_musica_foi_interrompida)
        add(R.raw.a_musica_comecou)
        add(R.raw.modo_gravacao)
        add(R.raw.gravando_em_3_2_1)
        add(R.raw.peca_gravada_com_sucesso)
    }

    companion object {
        val instance: Speaker = Speaker()
    }

    fun add(resId: Int) {
        phrases[resId] = SoundManager.instance.load(resId)
    }

    fun say(resId: Int) {
        val res = phrases[resId]
        res?.let { SoundManager.instance.play(it) }

    }
}