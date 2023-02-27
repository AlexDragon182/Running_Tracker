package com.example.runningtracker.UI.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.runningtracker.R
import com.example.runningtracker.databinding.FragmentRunBinding


class RunFragment : Fragment(R.layout.fragment_run) {

 private var _binding: FragmentRunBinding? = null
 private val binding get() = _binding!!

 override fun onCreateView(
  inflater: LayoutInflater,
  container: ViewGroup?,
  savedInstanceState: Bundle?
 ): View? {
  _binding = FragmentRunBinding.inflate(inflater,container,false)
  return binding.root
 }

}