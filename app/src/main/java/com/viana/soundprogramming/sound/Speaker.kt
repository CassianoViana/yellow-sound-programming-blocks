package com.viana.soundprogramming.sound

import android.media.MediaPlayer
import com.viana.soundprogramming.R
import com.viana.soundprogramming.appInstance
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

        add(R.raw.gravacao_muito_bem, 10)
        add(R.raw.gravando_em_321, 10)
        add(R.raw.gravacao_ahh_a_peca_nao_pode_ser_gravada, 10)

        add(R.raw.alerta_falta_parametro_repita, 20)
        add(R.raw.alerta_falta_parametro_se, 20)
        add(R.raw.alerta_tabuleiro_nao_esta_centralizado, 20)

        add(R.raw.ajuda_bumbo, 5)
        add(R.raw.ajuda_caixa, 5)
        add(R.raw.ajuda_chimbal, 5)
        add(R.raw.ajuda_surdo, 5)
        add(R.raw.ajuda_prato_ataque, 5)
        add(R.raw.ajuda_prato_conducao, 5)
        add(R.raw.ajuda_tom, 5)

        add(R.raw.ajuda_peca_repita, 5)
        add(R.raw.ajuda_se, 5)

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
        say(resId, delays[resId] ?: 5)
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
        res?.let { MyMediaPlayer.play(resId) }
    }
}

object MyMediaPlayer {
    private var mediaPlayer: MediaPlayer? = null
    fun play(resId: Int) {
        mediaPlayer?.reset()
        mediaPlayer?.stop()
        mediaPlayer = MediaPlayer.create(appInstance, resId)
        mediaPlayer?.start()
    }
}