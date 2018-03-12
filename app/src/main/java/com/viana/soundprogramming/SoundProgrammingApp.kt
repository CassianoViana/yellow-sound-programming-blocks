package com.viana.soundprogramming

import android.app.Application

class SoundProgrammingApp: Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: SoundProgrammingApp
            private set
    }
}
