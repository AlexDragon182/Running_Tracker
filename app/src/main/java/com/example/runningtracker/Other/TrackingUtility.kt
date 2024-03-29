package com.example.runningtracker.Other

import android.Manifest
import android.content.Context
import android.location.Location
import android.os.Build
import com.example.runningtracker.Service.Polyline
import pub.devrel.easypermissions.EasyPermissions
import java.util.concurrent.TimeUnit
//this will only have functions we dont need an instance of this class
// this is for requesting permitions
object TrackingUtility {

    fun hasLocationPermissions(context: Context): Boolean {//this function checks if the user granted permission
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {//checks if the device is running on android Q
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        } else {
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }

    fun calculatePolylineLength(polyline: Polyline):Float{
        var distance = 0f
        for(i in 0..polyline.size - 2) {
            val pos1 = polyline[i]
            val pos2 = polyline[i + 1]

            val result = FloatArray(1)
            Location.distanceBetween(
                pos1.latitude,
                pos2.longitude,

                pos1.longitude,
                pos2.latitude,
            result
            )
            distance+=result [0]
        }
        return distance
    }

    fun getFormattedStopWatchTime(ms: Long, includeMilis:Boolean=false): String {
        var miliseconds = ms
        val hours = TimeUnit.MILLISECONDS.toHours(miliseconds)
        miliseconds -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(miliseconds)
        miliseconds -=TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(miliseconds)
        if(!includeMilis) {
            return "${if(hours<10) "0" else ""}$hours:"+
            "${if(minutes<10)"0" else ""}$minutes:"+
            "${if(seconds<10)"0" else ""}$seconds:"
        }
        miliseconds-= TimeUnit.SECONDS.toMillis(seconds)
        miliseconds /=10


            return "${if(hours<10) "0" else ""}$hours:"+
                    "${if(minutes<10)"0" else ""}$minutes:"+
                    "${if(seconds<10)"0" else ""}$seconds:"
                    "${if(miliseconds<10)"0" else ""}$miliseconds:"
        }

    }

