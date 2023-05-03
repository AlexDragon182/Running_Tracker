package com.example.runningtracker.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(//this tells room this is our database
    entities = [Run::class],//this is a table in our database
    version = 1 //version in our database
)
@TypeConverters(Converter::class)
abstract class RunningDatabase : RoomDatabase() {

    abstract fun getRunDao(): RunDAO //calls the RunDAO object
}