package com.example.runningtracker.UI.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.runningtracker.R
import com.example.runningtracker.UI.ViewModels.MainViewModel
import com.example.runningtracker.databinding.FragmentRunBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint // to annotate view models with Dagger Hilt
class RunFragment : Fragment(R.layout.fragment_run) {

 private var _binding: FragmentRunBinding? = null
 private val binding get() = _binding!!
 private val viewmodel:MainViewModel by viewModels() // to get view model from dagger

 override fun onCreateView(
  inflater: LayoutInflater,
  container: ViewGroup?,
  savedInstanceState: Bundle?
 ): View? {
  _binding = FragmentRunBinding.inflate(inflater,container,false)
  return binding.root
 }

 override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
  super.onViewCreated(view, savedInstanceState)

  binding.fab.setOnClickListener{
   findNavController().navigate(R.id.action_runFragment_to_trackingFragment3)
  }
 }

}