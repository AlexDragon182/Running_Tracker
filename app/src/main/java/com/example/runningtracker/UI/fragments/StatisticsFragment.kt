package com.example.runningtracker.UI.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.runningtracker.R
import com.example.runningtracker.UI.ViewModels.MainViewModel
import com.example.runningtracker.UI.ViewModels.StatisticsViewmodel
import com.example.runningtracker.databinding.FragmentStatisticsBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint // to annotate view models with Dagger Hilt
class StatisticsFragment:Fragment(R.layout.fragment_statistics) {
    private var _binding : FragmentStatisticsBinding? = null
    private val binding get() = _binding!!
    private val viewmodel: StatisticsViewmodel by viewModels() // to get view model from dagger


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStatisticsBinding.inflate(inflater,container,false)
        return binding.root
    }
}