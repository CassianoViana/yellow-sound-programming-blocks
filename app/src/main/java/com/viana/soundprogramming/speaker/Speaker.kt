package com.viana.soundprogramming.speaker

import android.speech.tts.TextToSpeech
import com.viana.soundprogramming.R
import com.viana.soundprogramming.appInstance
import com.viana.soundprogramming.sound.SOUNDS_DIRECTORY
import com.viana.soundprogramming.sound.SoundManager
import java.util.*


class Speaker {

    private var tts: TextToSpeech? = null

    private var phrases = mutableMapOf<String, Int>()

    init {
        val context = appInstance?.applicationContext
        tts = TextToSpeech(context, TextToSpeech.OnInitListener {
            tts?.language = Locale("pt", "BR")
            tts?.setSpeechRate(1.3f)
            if (context != null) {
                savePhraseToSpeakLater(context.getString(R.string.paused))
                savePhraseToSpeakLater(context.getString(R.string.started))
            }
        })
    }

    private fun savePhraseToSpeakLater(phrase: String) {
        val phraseKey = getPhraseKey(phrase)

        val ttsOptions = HashMap<String, String>()
        val destinationFileName = getFileName(phraseKey)
        ttsOptions[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = phrase
        tts?.synthesizeToFile(phrase, ttsOptions, destinationFileName)

    }

    private fun getPhraseKey(phrase: String) = phrase.replace(" ", "_")

    private fun getFileName(phrase: String): String {
        return "$SOUNDS_DIRECTORY/${getPhraseKey(phrase)}.wav"
    }


    fun saySavedPhrase(phrase: String) {
        val fileName = getFileName(phrase)
        val soundId = SoundManager.instance.load(fileName)
        SoundManager.instance.play(soundId)
    }

    fun say(s: String) {
        tts?.speak(s, TextToSpeech.QUEUE_ADD, null, null)
    }
}
