package com.viana.soundprogramming.sound

import com.viana.soundprogramming.R
import java.util.*

class Speaker {

    private val phrases = mutableMapOf<Int, Int>()
    private val playing = mutableMapOf<Int, Boolean>()
    private val delays = mutableMapOf<Int, Long>()

    fun load() {
        add(R.raw.vamos_programar)
        add(R.raw.a_musica_foi_interrompida)
        add(R.raw.a_musica_comecou)
        add(R.raw.modo_gravacao, 5)
        add(R.raw.pronto_a_peca_foi_selecionada)
        add(R.raw.a_peca_foi_gravada)
        add(R.raw.essa_peca_nao_pode_ser_gravada)
        add(R.raw.falta_informar_parametro_repita, 20)
        add(R.raw.blocos_de_numero_seguidos, 20)
    }

    companion object {
        val instance: Speaker = Speaker()
    }

    fun add(resId: Int, delay: Long = 2) {
        phrases[resId] = SoundManager.instance.load(resId)
        delays[resId] = delay
    }

    fun say(resId: Int) {
        say(resId, delays[resId]!!)
    }

    private fun say(resId: Int, time: Long) {
        if (playing[resId] == true)
            return
        playing[resId] = true
        Timer().schedule(object : TimerTask() {
            override fun run() {
                playing[resId] = false
            }
        }, 1000L * time)
        val res = phrases[resId]
        res?.let { SoundManager.instance.play(it) }
    }
}