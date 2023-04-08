package com.example.runningtracker.DependencyInjection

import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import com.example.runningtracker.Other.Constants.KEY_FIRST_TIME_TOGGLE
import com.example.runningtracker.Other.Constants.KEY_NAME
import com.example.runningtracker.Other.Constants.KEY_WEIGHT
import com.example.runningtracker.Other.Constants.RUNNING_DATABASE_NAME
import com.example.runningtracker.Other.Constants.SHARED_PREFERENCES_NAME
import com.example.runningtracker.database.RunningDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.internal.managers.ApplicationComponentManager
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

//module - a manual of how to inject things
//a manual is a function we will simply create an object that we want to be able to inject and return it

@Module// defines it as a module
@InstallIn(SingletonComponent::class) // this is for determine when the objects inside our dagger are created and when they are destroyed
object AppModule {

    @Singleton //scope - each class in our class that running database will get same instance and not multiple instances
    @Provides // tell dagger that the result of this function can be used to provide other dependencies , and can be used to be injected into our classes
    fun provideRunningDatabase(@ApplicationContext app: Context) = Room.databaseBuilder(app,RunningDatabase::class.java,RUNNING_DATABASE_NAME).build()

    @Singleton
    @Provides
    fun provideRunDao(db:RunningDatabase) = db.getRunDao()

    @Singleton
    @Provides
    fun provideSharedPrferences(@ApplicationContext app:Context) =
        app.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideName(sharedPreferences: SharedPreferences) = sharedPreferences.getString(KEY_NAME,"")?:""

    @Singleton
    @Provides
    fun provideWeight(sharedPreferences: SharedPreferences) = sharedPreferences.getFloat(KEY_WEIGHT,80f)?:""

    @Singleton
    @Provides
    fun provideFirstTimeToggle(sharedPreferences: SharedPreferences) = sharedPreferences.getBoolean(KEY_FIRST_TIME_TOGGLE,true)
}