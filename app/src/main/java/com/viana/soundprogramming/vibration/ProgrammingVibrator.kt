package com.viana.soundprogramming.vibration

import android.content.Context
import android.os.Vibrator
import com.viana.soundprogramming.appInstance

object ProgrammingVibrator {
    private var vibrator: Vibrator? = null
    private fun prepare() {
        if (appInstance != null) {
            vibrator = appInstance!!.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }
    fun vibrate(time: Long) {
        /*prepare()
        vibrator?.vibrate(time)*/
    }
}