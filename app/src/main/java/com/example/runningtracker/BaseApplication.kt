package com.example.runningtracker

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

//the 1st thing you should do when using dagger hilt is tell our app tha it should use dagger hilt as dependency injection tool
// the second thing is to create a module
//compile time injected - when it launches our app it is already clear which dependencies will be injected into which classes
@HiltAndroidApp// mark this application as injectable with dagger Hilt
class BaseApplication: Application() {
    //module - a manual of how to inject things

    //setting timber
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())//enables debug logging with the timber library
    }

}