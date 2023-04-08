package com.example.runningtracker.UI.ViewModels

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.runningtracker.Other.SortType
import com.example.runningtracker.Repositories.MainRepository
import com.example.runningtracker.database.Run
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

//collect the data from the repository and provide it for all those fragments that will need it
//we need the instance of the main repository

@HiltViewModel
class MainViewModel @Inject constructor(
    val mainrepository : MainRepository
): ViewModel(){

    private val runSortedByDate = mainrepository.getAllRunsSortedByDate()
    private val runSortedByDistance = mainrepository.getAllRunsSortedByDistance()
    private val runSortedByCaloriesBurned = mainrepository.getAllRunsSortedByCaloriesBurned()
    private val runSortedByTimeInMillis = mainrepository.getAllRunsSortedByTimeInMillis()
    private val runSortedByAvgSpeed = mainrepository.getAllRunsSortedByAvgSpeed()

    val runs = MediatorLiveData<List<Run>>()

    val sortType = SortType.DATE

    init {
        runs.addSource(runSortedByDate){ result ->
            if( sortType == SortType.DATE){
                result.let{runs.value = it}
            }
        }
        runs.addSource(runSortedByDistance){ result ->
            if( sortType == SortType.DISTANCE){
                result.let{runs.value = it}
            }
        }

        runs.addSource(runSortedByCaloriesBurned){ result ->
            if( sortType == SortType.CALORIES_BURNED {
                    result.let { runs.value = it }
                }
        }

        runs.addSource(runSortedByTimeInMillis){ result ->
            if( sortType == SortType.RUNNING_TIME {
                    result.let { runs.value = it }
                }
        }

        runs.addSource(runSortedByAvgSpeed){ result ->
            if( sortType == SortType.AVG_SPEED){
                result.let{runs.value = it}
            }
        }
    }

    fun sortRuns(sortType: SortType) = when(sortType){
        SortType.DATE -> runSortedByDate.value?.let { runs.value = it }
        SortType.RUNNING_TIME -> runSortedByTimeInMillis.value?.let { runs.value = it }
        SortType.AVG_SPEED -> runSortedByAvgSpeed.value?.let { runs.value = it }
        SortType.DISTANCE -> runSortedByDistance.value?.let { runs.value = it }
        SortType.CALORIES_BURNED -> runSortedByCaloriesBurned.value?.let { runs.value = it }
    }.also { this.sortType = sortType }

    fun insertRun(run : Run) = viewModelScope.launch {
        mainrepository.insertRun(run)
    }
}