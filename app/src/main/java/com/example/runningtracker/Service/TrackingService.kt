package com.example.runningtracker.Service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.PendingIntent.writePendingIntentOrNullToParcel
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationRequest
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.runningtracker.Other.Constants.ACTION_PAUSE_SERVICE
import com.example.runningtracker.Other.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.example.runningtracker.Other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.runningtracker.Other.Constants.ACTION_STOP_SERVICE
import com.example.runningtracker.Other.Constants.FASTEST_LOCATION_INTERVAL
import com.example.runningtracker.Other.Constants.LOCATION_UPDATE_INTERVAL
import com.example.runningtracker.Other.Constants.NOTIFICATION_CHANNEL_ID
import com.example.runningtracker.Other.Constants.NOTIFICATION_CHANNEL_NAME
import com.example.runningtracker.Other.Constants.NOTIFICATION_ID
import com.example.runningtracker.Other.Constants.TIMER_UPDATE_INTERVAL
import com.example.runningtracker.Other.TrackingUtility
import com.example.runningtracker.R
import com.example.runningtracker.UI.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject
//this is for handling the tracking service
typealias Polyline = MutableList<LatLng>//differet name of a complex type, mutable list of lat longs
typealias Polylines = MutableList<Polyline>//getthing the latlngs together

@AndroidEntryPoint
class TrackingService : LifecycleService() {//inherit from Lyfecycle Service because we need to observe live data objects

    var isFirstRun = true
    var serviceKilled = false
    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient //Returns a single location fix representing the best estimate of the current location of the device
    private val timeRunInSeconds = MutableLiveData<Long>()
    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder
    lateinit var curNotificationBuilder: NotificationCompat.Builder

    // static objets are part of a class but no method is need it to use them. constants or methods
    //no toma en cuenta las variables funeral de companion object.
    companion object {
        val timeRunInMillis = MutableLiveData<Long>()//
        val isTracking = MutableLiveData<Boolean>()//
        val pathPoints = MutableLiveData<Polylines>()//holds track locations of all runs

    }
// because they dosent  hold value this method porst initial values
    private fun postInitValues(){
        isTracking.postValue(false)//
        pathPoints.postValue(mutableListOf())//
        timeRunInSeconds.postValue(0L)//
        timeRunInMillis.postValue(0L)//
    }

    override fun onCreate() {
        super.onCreate()
        curNotificationBuilder = baseNotificationBuilder

        postInitValues()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        isTracking.observe(this, Observer{
            updateLocationTracking(it)//
            updateNotificationtrackingState(it)
        })
    }

    private fun killService(){
        serviceKilled = true
        isFirstRun = true
        pauseService()
        postInitValues()
        stopForeground(true)
        stopSelf()

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {//this function gets called whenever we get send a command to our service

        intent?.let{//gets called when we sent an intent so check if intent is null
            when(it.action){// this is the service side of our comunication
                ACTION_START_OR_RESUME_SERVICE -> {//type of intent
                    if(isFirstRun){
                        startTimer()
                        isFirstRun = false
                    }else{
                        Timber.d("Resuming Service...")
                    }
                }
                ACTION_PAUSE_SERVICE -> {
                    Timber.d("Paused Service")
                    pauseService()
                }
                ACTION_STOP_SERVICE -> {
                    Timber.d("Started or Resume Service")
                    killService()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    var isTimerEnabled = false
    private var lapTime = 0L
    private var timeRun = 0L
    private var timeStarted =  0L
    private var lastSecondTimestamp = 0L

    private fun startTimer(){
        addEmptyPolyline()
        isTracking.postValue(true)
        timeStarted = System.currentTimeMillis()
        isTimerEnabled =  true
        CoroutineScope(Dispatchers.Main).launch {
            while (isTracking.value!!){
                //time difference between now and timeStarted
                lapTime = System.currentTimeMillis() - timeStarted
                timeRunInMillis.postValue(timeRun + lapTime)
                if(timeRunInMillis.value!!>= lastSecondTimestamp + 1000L){
                    timeRunInSeconds.postValue(timeRunInSeconds.value!!+1)
                    lastSecondTimestamp +=1000L
                }
                delay(TIMER_UPDATE_INTERVAL)
            }
            timeRun +=lapTime
        }
    }

    private fun pauseService(){
        isTracking.postValue(false)
        isTimerEnabled = false
    }

    private fun updateNotificationtrackingState(isTracking: Boolean) {
        val notificationActionText = if (isTracking) "Pause" else "Resume"
        val pendingIntent = if (isTracking) {
            val pauseIntent = Intent(this, TrackingService::class.java).apply {
                action = ACTION_PAUSE_SERVICE
            }
            PendingIntent.getService(this, 1, pauseIntent, FLAG_UPDATE_CURRENT)

        } else {
            val resumeIntent = Intent(this, TrackingService::class.java).apply {
                action = ACTION_START_OR_RESUME_SERVICE
            }
            PendingIntent.getService(this, 2, resumeIntent, FLAG_UPDATE_CURRENT)
        }
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        curNotificationBuilder.javaClass.getDeclaredField("mAction").apply {
            isAccessible = true
            set(curNotificationBuilder, ArrayList<NotificationCompat.Action>())
        }
        if (!serviceKilled){
            curNotificationBuilder = baseNotificationBuilder
                .addAction(R.drawable.ic_pause_black_24dp, notificationActionText, pendingIntent)
        notificationManager.notify(NOTIFICATION_ID, curNotificationBuilder.build())
    }


    }

    @SuppressLint("MissingPermission")
    private fun updateLocationTracking (isTracking : Boolean){//if it is tracking youl want to resume updates and stop them when its not
        if(isTracking){// if it is tracking equal to true
            if(TrackingUtility.hasLocationPermissions(this)){//if tracking utility has location permissions
                val request = com.google.android.gms.location.LocationRequest().apply {//request location request to check updates
                    interval = LOCATION_UPDATE_INTERVAL //
                    fastestInterval = FASTEST_LOCATION_INTERVAL //
                    priority = PRIORITY_HIGH_ACCURACY //
                }
                fusedLocationProviderClient.requestLocationUpdates(//warning suppressed because permission is asked by another thing
                    request,
                    locationCallback,
                    Looper.getMainLooper() //
                )
            }else{
                fusedLocationProviderClient.removeLocationUpdates(locationCallback) //if it stops , remove those locations
            }
        }
    }
//define location callback
    val locationCallback = object  : LocationCallback() {//fused location provider client to request location updates, and deliver them to us
        override fun onLocationResult(p0: LocationResult) {//ctrl + o
            super.onLocationResult(p0)
            if(isTracking.value!!){// if it is tracking
                p0?.locations?.let { locations ->//hold locations //
                    for (location in locations){// for all of location in locations
                     addPathPoint(location)// whenever we retrieve a location add the location to the last polyline
                        Timber.d("NEW LOCATION LOCATION : ${location.latitude}, ${location.longitude}")
                    }
                }
            }
        }
    }

    private fun addPathPoint(location:Location) {//this adds a coordinate , to the last polyline of our polyline list.
        location?.let{//if location is not equal to null
            val pos = LatLng(location.latitude,location.longitude)// convert location to a latLng with the two parameters.
            pathPoints.value?.apply {// add this position , to the last polyline of polyline list
                last().add(pos)//get last polyline
                pathPoints.postValue(this)//post value
            }
        }
    }

    private fun addEmptyPolyline() = pathPoints.value?.apply{//this pass no coordinates for when the run is paused.
        add(mutableListOf())//add empthy list
        pathPoints.postValue(this)//this refers to the polyline object and post nothing as new value
    }?: pathPoints.postValue(mutableListOf(mutableListOf()))// in case it is null,post the first empty polly line

    private fun startForegroundService() {// function for starting a foreground service
        addEmptyPolyline()// so it adds the first empty polyline
        startTimer()
        isTracking.postValue(true)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) // get a reference to notification manager
                as NotificationManager// cast it as notification manager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//if we are in android Oreo or later
            createNotificationChannel(notificationManager)//create notification channel with the manager
        }

        startForeground(NOTIFICATION_ID,baseNotificationBuilder.build())// this tells the service that is a foreground service

        timeRunInSeconds.observe(this, Observer {
            if (!serviceKilled){
                val notification = curNotificationBuilder// the actual notification
                    .setContentText(TrackingUtility.getFormattedStopWatchTime(it * 1000L))
                notificationManager.notify(NOTIFICATION_ID, notification.build())
            }

        })


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager){//create the notification channel to make service foreground
        val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME,IMPORTANCE_LOW)//this pass the parameters to the channel
        notificationManager.createNotificationChannel(channel)//creates notification channel with the parameters
        //importance low so the timer dosnt come with a sound
    }
}