package com.example.runningtracker.UI.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.runningtracker.Other.Constants.KEY_FIRST_TIME_TOGGLE
import com.example.runningtracker.Other.Constants.KEY_NAME
import com.example.runningtracker.Other.Constants.KEY_WEIGHT
import com.example.runningtracker.R
import com.example.runningtracker.UI.MainActivity
import com.example.runningtracker.databinding.ActivityMainBinding
import com.example.runningtracker.databinding.FragmentSetupBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SetupFragment :Fragment (R.layout.fragment_setup) {
    @Inject
    lateinit var sharedPref: SharedPreferences

    @set: Inject
    var isFirstAppOpen = true

    private var _binding: FragmentSetupBinding? = null
    private val binding get() = _binding!!
    //private var _binding2: ActivityMainBinding? = null
    //private val binding2 get() = _binding2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSetupBinding.inflate(inflater,container,false)
        //_binding2 = ActivityMainBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(!isFirstAppOpen) {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.setupFragment,true)
                .build()
            findNavController().navigate(R.id.action_setupFragment_to_runFragment2,savedInstanceState,navOptions)
        }

        binding.tvContinue.setOnClickListener{
            val sucess = writePersonalDataToSharedPref()
            if(sucess){
                findNavController().navigate(R.id.action_setupFragment_to_runFragment2)

            }else {
                Snackbar.make(requireView(),"Please enter all the fields", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun writePersonalDataToSharedPref() : Boolean {
        val name = binding.etName.toString()
        val weight = binding.etWeight.text.toString()
        if(name.isEmpty()|| weight.isEmpty()) {
            return false
        }
sharedPref.edit()
    .putString(KEY_NAME, name)
    .putFloat(KEY_WEIGHT, weight.toFloat())
    .putBoolean(KEY_FIRST_TIME_TOGGLE, false)
    .apply()
        val toolbarText = "Let's go, $name!"
        (requireActivity() as MainActivity).binding.tvToolbarTitle.text = toolbarText
        return true
    }
}