package com.example.runningtracker.UI.ViewModels

import androidx.lifecycle.ViewModel
import com.example.runningtracker.Repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

//collect the data from the repository and provide it for all thos fragments that will need it
//we need the instance of the main repository

@HiltViewModel
class StatisticsViewmodel @Inject constructor(
    val mainrepository : MainRepository
): ViewModel(){

    val totalTimeRun = mainrepository.getTotalTimeInMillis()
    val totalDistance = mainrepository.getTotalDistance()
    val totalCaloriesBurned = mainrepository.getTotalCaloriesBurned()
    val totalAvgSpeed = mainrepository.getTotalAvgSpeed()

    val runSotredByDate =mainrepository.getAllRunsSortedByDate()

}