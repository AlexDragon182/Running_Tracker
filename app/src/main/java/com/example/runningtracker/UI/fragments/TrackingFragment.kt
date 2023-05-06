package com.example.runningtracker.UI.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.room.util.query
import com.example.runningtracker.Other.Constants.ACTION_PAUSE_SERVICE
import com.example.runningtracker.Other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.runningtracker.Other.Constants.ACTION_STOP_SERVICE
import com.example.runningtracker.Other.Constants.MAP_ZOOM
import com.example.runningtracker.Other.Constants.POLYLINE_COLOR
import com.example.runningtracker.Other.Constants.POLYLINE_WIDTH
import com.example.runningtracker.Other.TrackingUtility
import com.example.runningtracker.R
import com.example.runningtracker.Service.Polyline
import com.example.runningtracker.Service.TrackingService
import com.example.runningtracker.UI.MainActivity
import com.example.runningtracker.UI.ViewModels.MainViewModel
import com.example.runningtracker.database.Run
import com.example.runningtracker.databinding.FragmentTrackingBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import javax.inject.Inject
import kotlin.math.round
//in case of using a map view we need to handle the sates of lyfe cycle
//using a map fragment inside another fragment is terrible design
// a foreground service comes with a notification, the user is actively aware of the service running
//services can be killed by the android system, so foreground service treats it as an activity
const val CANCEL_TRACKING_DIALOG_TAG = "Dialog has been cancelled"

@Suppress("UNUSED_EXPRESSION")
@AndroidEntryPoint
class TrackingFragment:Fragment(R.layout.fragment_tracking) {
    private var _binding: FragmentTrackingBinding? = null
    private val binding get() = _binding!!
    private var map : GoogleMap? = null //instantiates google map, this is of the map object, and its google Type
    private val viewModel : MainViewModel by viewModels()
    private var isTracking = false
    private var pathPoints =  mutableListOf<Polyline>()
    private var curTimeInMillis = 0L
    private var menu : Menu? = null

    @set:Inject
    private var weight = 80f



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        _binding = FragmentTrackingBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mapView.onCreate(savedInstanceState)//manage the life cycle of our map view
        binding.btnToggleRun.setOnClickListener{
            toggleRun()}

        if(savedInstanceState != null) {
            val cancelTrackingDialog = parentFragmentManager.findFragmentByTag(
                CANCEL_TRACKING_DIALOG_TAG) as CancelTrackingDialog?
            cancelTrackingDialog?.setYesListemer { stopRun() }
        }

        binding.btnFinishRun.setOnClickListener{
            zoomToSeemWholeTrack()
            endRunAndSaveToDb()}


        binding.mapView.getMapAsync{// it puts the map in the MapView
            map = it
            addAllPolylines()
        }
        subscribeToObservers()
    }

    private fun subscribeToObservers(){
        TrackingService.isTracking.observe(viewLifecycleOwner, Observer { updateTracking(it) })
        TrackingService.pathPoints.observe(viewLifecycleOwner, Observer { pathPoints=it
            addLatestPolyline()
            moveCameraToUser()})
        TrackingService.timeRunInMillis.observe(viewLifecycleOwner, Observer {
            curTimeInMillis = it
            val formattedTime = TrackingUtility.getFormattedStopWatchTime(curTimeInMillis,true)
            binding.tvTimer.text = formattedTime
        })
    }

    private fun toggleRun(){
        if(isTracking){
            menu?.getItem(0)?.isVisible = true
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

    private fun zoomToSeemWholeTrack() {
        val bounds = LatLngBounds.Builder()
        for(polyline in pathPoints){
            for(pos in polyline) {
                bounds.include(pos)
            }
        }
        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                binding.mapView.width,
                binding.mapView.height,
                (binding.mapView.height * 0.05f).toInt()
            )
        )
    }

    private fun endRunAndSaveToDb() {
        map?.snapshot { bmp ->
            var distanceInMeters = 0
            for (polyline in pathPoints) {
                distanceInMeters += TrackingUtility.calculatePolylineLength(polyline).toInt()
            }
            val avgSpeed = round((distanceInMeters / 1000f) / (curTimeInMillis / 1000f / 60 / 60) *10 ) / 10f
            val dateTimestamp = Calendar.getInstance().timeInMillis
            val caloriesBurned = ((distanceInMeters / 1000f)* weight).toInt()
            val run = Run(bmp,dateTimestamp,avgSpeed,distanceInMeters,curTimeInMillis,caloriesBurned)
            viewModel.insertRun(run)
            Snackbar.make(
                requireActivity().findViewById(android.R.id.content), //.getRootView()
                "Run saved successfully",
                Snackbar.LENGTH_LONG
            ).show()
            stopRun()
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_tracking_menu,menu)
        this.menu = menu
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if(curTimeInMillis > 0L){
            this.menu?.getItem(0)?.isVisible = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.miCancelTracking -> {
                showCancelTrackingDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

 private fun showCancelTrackingDialog(){

     CancelTrackingDialog().apply {
         setYesListemer {
             stopRun()
         }
     }.show(parentFragmentManager,CANCEL_TRACKING_DIALOG_TAG)
 }

    private fun stopRun () {
        binding.tvTimer.text = "00:00:00:00"
        sendCommandToService(ACTION_STOP_SERVICE)
        findNavController().navigate(R.id.action_trackingFragment_to_runFragment3)
    }

    private fun updateTracking(istracking : Boolean){
        this.isTracking = isTracking
        if(istracking && curTimeInMillis> 0L) {
            binding.btnToggleRun.text = "start"
            binding.btnFinishRun.visibility = View.VISIBLE
        }else if(istracking){
            binding.btnToggleRun.text = "stop"
            menu?.getItem(0)?.isVisible = true
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

    private fun sendCommandToService(action:String) =//sends intent to our service with commands attached
        Intent(requireContext(),TrackingService::class.java).also {//Context, Service class we want to send the command to
            it.action = action// it . action to the action that we pass a parameter
            requireContext().startService(it)//delivers the intent to our service and it reacts to the command
        }
//all of this are here for managint the life cycle of the map view
    //the map views gets destroyed in onStop
    //in on destroyed it is already destroyed

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

    override fun onSaveInstanceState(outState: Bundle) {// this is important because we get our map asyncronously
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }
}

