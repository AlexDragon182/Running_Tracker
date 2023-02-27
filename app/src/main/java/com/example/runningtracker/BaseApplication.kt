package com.example.runningtracker

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

//the 1st thing you should do when using dagger hilt is tell our app tha it should use dagger hilt as dependency injection tool
//compile time injected - when it launches our app it is already clear which dependencies will be injected into which classes
@HiltAndroidApp
class BaseApplication: Application() {
    //module - a manual of how to inject things

    //setting timber
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())//enables debug loging with the timber library
    }

}