package com.viana.soundprogramming.sound

import com.viana.soundprogramming.R

class Speaker {

    private val phrases = mutableMapOf<Int, Int>()

    fun load() {
        add(R.raw.vamos_programar)
        add(R.raw.a_musica_foi_interrompida)
        add(R.raw.a_musica_comecou)
        add(R.raw.modo_gravacao)
        add(R.raw.pronto_a_peca_foi_selecionada)
        add(R.raw.a_peca_foi_gravada)
        add(R.raw.essa_peca_nao_pode_ser_gravada)
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