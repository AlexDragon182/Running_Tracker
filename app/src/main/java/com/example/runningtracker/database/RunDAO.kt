package com.example.runningtracker.database

import androidx.lifecycle.LiveData
import androidx.room.*
//Data Acces Objects , describes all possible actions we want to use in the database
@Dao
interface RunDAO {
    // insert the new run and provide the On conflict strategy
    // for when it need to insert a run the already exist the new one replace the old one
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRun(run: Run)//suspend fun so this can be executed in a coroutine

    //to delete runs
    @Delete
    suspend fun deleteRun(run: Run)

    //database query's that provides us runs that we want to get from our database
    //not a suspend fun , we want this to return a live data object, and this does not work in a coroutine
    //Sorted by Date
    @Query("SELECT * FROM running_table ORDER BY timestamp DESC")
    fun getAllRunsSortedByDate(): LiveData<List<Run>>

    //Sorted by time miliseconds
    @Query("SELECT * FROM running_table ORDER BY timeInMillis DESC")
    fun getAllRunsSortedByTimeInMillis(): LiveData<List<Run>>

    ////Sorted by calories burned
    @Query("SELECT * FROM running_table ORDER BY caloriesBurned DESC")
    fun getAllRunsSortedByCaloriesBurned(): LiveData<List<Run>>

    ////Sorted by Distance
    @Query("SELECT * FROM running_table ORDER BY distanceInMeters DESC")
    fun getAllRunsSortedByDistance(): LiveData<List<Run>>

    ////Sorted by AverageSpeed
    @Query("SELECT * FROM running_table ORDER BY avgSpeedInKMH DESC")
    fun getAllRunsSortedByAvgSpeed(): LiveData<List<Run>>

    //get Total time in miliseconds
    @Query("SELECT SUM(timeInMillis) FROM running_table")
    fun getTotalTimeInMillis(): LiveData<Long>

    //get Total distance in meters
    @Query("SELECT SUM(distanceInMeters) FROM running_table")
    fun getTotalDistance(): LiveData<Int>

    //get average speed
    @Query("SELECT AVG(avgSpeedInKMH) FROM running_table")
    fun getTotalAvgSpeed(): LiveData<Float>

    //get Total calories burned
    @Query("SELECT SUM(caloriesBurned) FROM running_table")
    fun getTotalCaloriesBurned(): LiveData<Long>

    // SUM and AVG are SQULITE functions
    //this functions return the data that is listed in the table.
}