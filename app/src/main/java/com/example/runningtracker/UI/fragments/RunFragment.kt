package com.example.runningtracker.UI.fragments

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.runningtracker.Other.Constants.REQUEST_CODE_LOCATION_PERMISSION
import com.example.runningtracker.Other.TrackingUtility
import com.example.runningtracker.R
import com.example.runningtracker.UI.ViewModels.MainViewModel
import com.example.runningtracker.databinding.FragmentRunBinding
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions


@AndroidEntryPoint // to annotate view models with Dagger Hilt

class RunFragment : Fragment(R.layout.fragment_run),EasyPermissions.PermissionCallbacks {

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
  requestPersmissions()
  super.onViewCreated(view, savedInstanceState)

  binding.fab.setOnClickListener{
   findNavController().navigate(R.id.action_runFragment_to_trackingFragment3)
  }
 }

 private fun requestPersmissions () {
  if(TrackingUtility.hasLocationPermissions(requireContext())){
   return
  }
  if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
   EasyPermissions.requestPermissions(
    this ,"You need to accept this permissions to use this app",
    REQUEST_CODE_LOCATION_PERMISSION,
    Manifest.permission.ACCESS_COARSE_LOCATION,
    Manifest.permission.ACCESS_FINE_LOCATION,
   )
  }else {
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
  if(EasyPermissions.somePermissionPermanentlyDenied(this,perms)) {
   AppSettingsDialog.Builder(this).build().show()
  }else{
    requestPersmissions()
   }
  }
 override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {

 }

 override fun onRequestPermissionsResult(
  requestCode: Int,
  permissions: Array<out String>,
  grantResults: IntArray
 ) {
  super.onRequestPermissionsResult(requestCode, permissions, grantResults)
  EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this)

 }

 }



