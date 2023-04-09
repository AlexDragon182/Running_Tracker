package com.example.runningtracker.UI.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.runningtracker.Other.CustomMarkerView
import com.example.runningtracker.Other.TrackingUtility
import com.example.runningtracker.R
import com.example.runningtracker.UI.ViewModels.MainViewModel
import com.example.runningtracker.UI.ViewModels.StatisticsViewmodel
import com.example.runningtracker.databinding.FragmentStatisticsBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Math.round


@AndroidEntryPoint // to annotate view models with Dagger Hilt
class StatisticsFragment:Fragment(R.layout.fragment_statistics) {
    private var _binding : FragmentStatisticsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StatisticsViewmodel by viewModels() // to get view model from dagger


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStatisticsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()
        setupBarChart()
    }
private fun setupBarChart(){
    binding.barChart.xAxis.apply {
        position = XAxis.XAxisPosition.BOTTOM
        setDrawLabels(false)
        axisLineColor = Color.WHITE
        textColor = Color.WHITE
        setDrawGridLines(false)
    }
    binding.barChart.axisLeft.apply {
        axisLineColor = Color.WHITE
        textColor = Color.WHITE
        setDrawGridLines(false)
    }
    binding.barChart.axisRight.apply {
        axisLineColor = Color.WHITE
        textColor = Color.WHITE
        setDrawGridLines(false)
    }
    binding.barChart.apply {
        description.text = "Avg Speed Over Time"
        legend.isEnabled = false

    }
}

    private fun subscribeToObservers(){
        viewModel.totalTimeRun.observe(viewLifecycleOwner, Observer {
            it?.let {
                val totalTimeTun = TrackingUtility.getFormattedStopWatchTime(it)
                binding.tvTotalTime.text = totalTimeTun
            }
        })
        viewModel.totalDistance.observe(viewLifecycleOwner, Observer {
            it?.let {
                val km = it / 1000f
                val totalDistance = round(km*10f)/10f
                val totalDistanceString = "${totalDistance}km"
                binding.tvTotalDistance.text = totalDistanceString
            }
        })
        viewModel.totalAvgSpeed.observe(viewLifecycleOwner, Observer{
            it?.let{
                val avgSpeed = round(it*10f)/10f
                val avgSpeedString = "${avgSpeed}km/h"
                binding.tvAverageSpeed.text = avgSpeedString
            }
        })
        viewModel.totalCaloriesBurned.observe(viewLifecycleOwner, Observer{
            it?.let{
                val totalCalories = "${it}kcal"
                binding.tvTotalCalories.text =totalCalories
            }
        })

        viewModel.runSotredByDate.observe(viewLifecycleOwner, Observer {
            it?.let{
                val allAvgSpeeds = it.indices.map { i-> BarEntry(i.toFloat(), it[i].avgSpeedInKMH) }
                val barDataSet = BarDataSet(allAvgSpeeds, "Avg Speed Over Time").apply {
                    valueTextColor = Color.WHITE
                    color = ContextCompat.getColor(requireContext(),R.color.colorAccent)
                }
                binding.barChart.data = BarData(barDataSet)
                binding.barChart.marker = CustomMarkerView(it.reversed(), requireContext(), R.layout.marker_view)
                binding.barChart.invalidate()
            }
        })
        }

}