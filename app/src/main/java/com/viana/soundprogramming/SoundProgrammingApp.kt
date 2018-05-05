package com.viana.soundprogramming

import android.app.Application

lateinit var appInstance: SoundProgrammingApp

class SoundProgrammingApp : Application() {

    override fun onCreate() {
        super.onCreate()
        appInstance = this
    }
}
