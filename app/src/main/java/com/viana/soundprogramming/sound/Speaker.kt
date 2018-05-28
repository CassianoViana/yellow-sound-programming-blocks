package com.viana.soundprogramming.sound

import com.viana.soundprogramming.R
import java.util.*

class Speaker {

    private val phrases = mutableMapOf<Int, Int>()
    private val playing = mutableMapOf<Int, Boolean>()
    private val delays = mutableMapOf<Int, Long>()

    fun load() {
        add(R.raw.msg_olaaa_vamos_programar_bateria)
        add(R.raw.modo_solta_o_som, 5)
        add(R.raw.modo_gravar, 5)
        add(R.raw.modo_parar, 5)
        add(R.raw.modo_ajuda, 5)

        add(R.raw.gravacao_vc_selecionou_a_peca_corretamente, 4)
        add(R.raw.gravacao_muito_bem, 10)
        add(R.raw.gravacao_ahh_a_peca_nao_pode_ser_gravada, 10)

        add(R.raw.alerta_falta_uma_peca_repita, 20)
        add(R.raw.alerta_falta_uma_peca_se, 20)
        add(R.raw.alerta_tabuleiro_nao_esta_centralizado, 20)

        add(R.raw.ajuda_bumbo, 10)
        add(R.raw.ajuda_caixa, 10)
        add(R.raw.ajuda_chimbau, 10)
        add(R.raw.ajuda_surdo, 10)
        add(R.raw.ajuda_prato_ataque, 10)
        add(R.raw.ajuda_prato_de_conducao, 10)
        add(R.raw.ajuda_tom, 10)

        add(R.raw.ajuda_repita, 10)
        add(R.raw.ajuda_se, 10)

        add(R.raw.ajuda_numero_2, 4)
        add(R.raw.ajuda_numero_3, 4)
        add(R.raw.ajuda_numero_4, 4)
        add(R.raw.ajuda_numero_5, 4)

        add(R.raw.ajuda_circulo, 10)
        add(R.raw.ajuda_estrela, 10)
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