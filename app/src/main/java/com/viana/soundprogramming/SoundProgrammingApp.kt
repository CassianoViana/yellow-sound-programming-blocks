package com.viana.soundprogramming

import android.app.Application

var appInstance: SoundProgrammingApp? = null

class SoundProgrammingApp : Application() {

    override fun onCreate() {
        super.onCreate()
        appInstance = this
    }
}
