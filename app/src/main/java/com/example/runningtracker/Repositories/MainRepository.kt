package com.example.runningtracker.Repositories

import com.example.runningtracker.database.Run
import com.example.runningtracker.database.RunDAO
import javax.inject.Inject

//collects data from all of our data sources,for View Model to use
//provide the functions in our database so we can use the functions on our view model later on

class MainRepository @Inject constructor(//Injects the constructor so it calls the parameters
    val runDao: RunDAO // parameter RunDAO
)  {// call functions of runDAO and provide them for view Models.
    suspend fun insertRun(run: Run) = runDao.insertRun(run)

    suspend fun deleteRun(run: Run) = runDao.deleteRun(run)

    fun getAllRunsSortedByDate() = runDao.getAllRunsSortedByDate()//not suspend function because live data is asincronously by default

    fun getAllRunsSortedByTimeInMillis() = runDao.getAllRunsSortedByTimeInMillis()

    fun getAllRunsSortedByDistance() = runDao.getAllRunsSortedByDistance()

    fun getAllRunsSortedByCaloriesBurned() = runDao.getAllRunsSortedByCaloriesBurned()

    fun getAllRunsSortedByAvgSpeed() = runDao.getAllRunsSortedByAvgSpeed()

    fun getTotalDistance() = runDao.getTotalDistance()

    fun getTotalTimeInMillis() = runDao.getTotalTimeInMillis()

    fun getTotalAvgSpeed() = runDao.getTotalAvgSpeed()

    fun getTotalCaloriesBurned() = runDao.getTotalCaloriesBurned()
}