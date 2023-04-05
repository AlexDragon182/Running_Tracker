package com.example.runningtracker.UI.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.room.util.query
import com.example.runningtracker.Other.Constants.ACTION_PAUSE_SERVICE
import com.example.runningtracker.Other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.runningtracker.Other.Constants.MAP_ZOOM
import com.example.runningtracker.Other.Constants.POLYLINE_COLOR
import com.example.runningtracker.Other.Constants.POLYLINE_WIDTH
import com.example.runningtracker.R
import com.example.runningtracker.Service.Polyline
import com.example.runningtracker.Service.TrackingService
import com.example.runningtracker.UI.ViewModels.MainViewModel
import com.example.runningtracker.databinding.FragmentTrackingBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.PolylineOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackingFragment:Fragment(R.layout.fragment_tracking) {
    private var _binding: FragmentTrackingBinding? = null
    private val binding get() = _binding!!
    private var map : GoogleMap? = null
    private val viewModel : MainViewModel by viewModels()
    private var isTracking = false
    private var pathPoints =  mutableListOf<Polyline>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTrackingBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mapView.onCreate(savedInstanceState)
        binding.btnToggleRun.setOnClickListener{toggleRun()}

        binding.mapView.getMapAsync{
            map = it
            addAllPolylines()
        }
        subscribeToObservers()
    }

    private fun subscribeToObservers(){
        TrackingService.isTracking.observe(viewLifecycleOwner, Observer { updateTracking(it) })
        TrackingService.pathPoints.observe(viewLifecycleOwner, Observer { pathPoints=it,addLatestPolyline(),moveCameraToUser()})
    }

    private fun toggleRun(){
        if(isTracking){
            sendCommandToService(ACTION_PAUSE_SERVICE)
        }else{
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        }
    }

    private fun moveCameraToUser(){
        if(pathPoints.isNotEmpty()&& pathPoints.last().isNotEmpty()){
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last().last(),
                    MAP_ZOOM
                )
            )
        }
    }

    private fun addAllPolylines(){
        for(polyline in pathPoints){
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(polyline)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun updateTracking(istracking : Boolean){
        this.isTracking = isTracking
        if(istracking) {
            binding.btnToggleRun.text = "start"
            binding.btnFinishRun.visibility = View.VISIBLE
        }else{
            binding.btnToggleRun.text = "stop"
            binding.btnFinishRun.visibility = View.GONE
        }
    }

    private fun addLatestPolyline(){
        if(pathPoints.isNotEmpty()&& pathPoints.last().size>1){
            val preLastLatLng = pathPoints.last()[pathPoints.last().size - 2]
            val lastLatLng = pathPoints.last().last()
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(preLastLatLng)
                .add(lastLatLng)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun sendCommandToService(action:String) =
        Intent(requireContext(),TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }

    override fun onStart() {
        super.onStart()
        binding.mapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView?.onResume()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView?.onStop()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView?.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView?.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }
}

