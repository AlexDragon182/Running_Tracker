package com.example.runningtracker.DependencyInjection

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.runningtracker.Other.Constants.RUNNING_DATABASE_NAME
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
}