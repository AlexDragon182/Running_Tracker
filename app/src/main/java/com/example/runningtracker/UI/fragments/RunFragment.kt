package com.example.runningtracker.UI.fragments

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.runningtracker.Other.Constants.REQUEST_CODE_LOCATION_PERMISSION
import com.example.runningtracker.Other.SortType
import com.example.runningtracker.Other.TrackingUtility
import com.example.runningtracker.R
import com.example.runningtracker.UI.ViewModels.MainViewModel
import com.example.runningtracker.adapters.RunAdapter
import com.example.runningtracker.databinding.FragmentRunBinding
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions


@AndroidEntryPoint // to annotate view models with Dagger Hilt

class RunFragment : Fragment(R.layout.fragment_run),EasyPermissions.PermissionCallbacks {//implement the Easy permission Interface to implement a permission callback to get the results and use a dialog to handle a permission

 private var _binding: FragmentRunBinding? = null
 private val binding get() = _binding!!
 private val viewmodel:MainViewModel by viewModels() // to get view model from dagger
 private lateinit var runAdapter: RunAdapter

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
  requestPersmissions()
  setuprecyclerView()

  when(viewmodel.sortType){
   SortType.DATE -> binding.spFilter.setSelection(0)
   SortType.RUNNING_TIME -> binding.spFilter.setSelection(1)
   SortType.DISTANCE -> binding.spFilter.setSelection(2)
   SortType.AVG_SPEED -> binding.spFilter.setSelection(3)
   SortType.CALORIES_BURNED -> binding.spFilter.setSelection(4)
  }

  binding.spFilter.onItemSelectedListener = object  : AdapterView.OnItemSelectedListener{
   override fun onItemSelected(adapterView : AdapterView<*>?, view: View?, position: Int, id: Long) {

    when(position) {
     0 -> viewmodel.sortRuns(SortType.DATE)
     1 -> viewmodel.sortRuns(SortType.RUNNING_TIME)
     2 -> viewmodel.sortRuns(SortType.DISTANCE)
     3 -> viewmodel.sortRuns(SortType.AVG_SPEED)
     4 -> viewmodel.sortRuns(SortType.CALORIES_BURNED)
    }

   }

   override fun onNothingSelected(parent: AdapterView<*>?) {

   }
  }

viewmodel.runs.observe(viewLifecycleOwner, Observer { runAdapter.sumbitList(it)})
  binding.fab.setOnClickListener{
   findNavController().navigate(R.id.action_runFragment_to_trackingFragment3)//this calls the direction in the nav graph to navigate.
  }
 }

 private fun setuprecyclerView() = binding.rvRuns.apply {
  runAdapter = RunAdapter()
  adapter = runAdapter
  layoutManager = LinearLayoutManager(requireContext())
 }

 private fun requestPersmissions () {// this function request permissions
  if(TrackingUtility.hasLocationPermissions(requireContext())){//if the user already checked those permissions, require context makes sure context is not equal to null
   return
  }
  if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){// if it didint accept the permissions before, checks if the device is running on android Q
   EasyPermissions.requestPermissions(//easy permissions request the permissions in the constructor
    this ,"You need to accept this permissions to use this app",//pass fragment, message in case of user denying permission, and permissions
    REQUEST_CODE_LOCATION_PERMISSION,
    Manifest.permission.ACCESS_COARSE_LOCATION,
    Manifest.permission.ACCESS_FINE_LOCATION,
   )
  }else {//if it is not running on android Q
   EasyPermissions.requestPermissions(
    this ,"You need to accept this permissions to use this app",
    REQUEST_CODE_LOCATION_PERMISSION,
    Manifest.permission.ACCESS_COARSE_LOCATION,
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
   )
  }
 }

 override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
  }
 override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
  if(EasyPermissions.somePermissionPermanentlyDenied(this,perms)) {//if a permition permanently dennied a permission , pass fragment and denied permissions , perms is a parameter of a function that is a list that contains the permissions denied
   AppSettingsDialog.Builder(this).build().show()//this is the dialog that lead the user to app Settings in case
  }else{
   requestPersmissions()//if he does not denied them permanently
  }
 }

 override fun onRequestPermissionsResult(//functions that handles permissions result in android library, whenever we request permissions
  requestCode: Int,
  permissions: Array<out String>,
  grantResults: IntArray
 ) {
  super.onRequestPermissionsResult(requestCode, permissions, grantResults)//this redirects the result of the android library to the easy permissions library
  EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this)

 }

 }



