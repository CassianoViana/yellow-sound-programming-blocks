package com.viana.soundprogramming.speaker

import android.speech.tts.TextToSpeech
import com.viana.soundprogramming.appInstance
import java.util.*

class Speaker {

    var tts: TextToSpeech? = null

    init {
        tts = TextToSpeech(appInstance?.applicationContext, TextToSpeech.OnInitListener {
            tts?.language = Locale("pt", "BR")
        })
    }

    fun say(s: String) {
        Thread({
            tts?.speak(s, TextToSpeech.QUEUE_ADD, null, null)
        }).start()
    }
}
