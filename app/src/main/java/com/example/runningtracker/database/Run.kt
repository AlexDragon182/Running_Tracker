package com.example.runningtracker.database

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
//an entitiy is nothing more than a table in our database
//describes a single run of our app
@Entity(tableName = "running_table") //this is an entity for our room data base (a table)
data class Run(
    var img: Bitmap? = null,//preview image
    var timestamp: Long = 0L,//the amount of milliseconds, Date converted to miliseconds, its more easy to sort than days
    var avgSpeedInKMH: Float = 0f,// average speed in kilometers
    var distanceInMeters: Int = 0, // distance in meters.
    var timeInMillis: Long = 0L,// describes how long our run is
    var caloriesBurned: Int = 0 // calories burnt
) {
    @PrimaryKey(autoGenerate = true)// a unique a identifier for each entry in our database table
    var id: Int? = null //this is not in the constructor because we want to be able to create this objects without the primary key,
    //we want room to handle this
}