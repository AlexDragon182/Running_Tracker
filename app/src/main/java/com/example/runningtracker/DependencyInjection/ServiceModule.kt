package com.example.runningtracker.DependencyInjection

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.runningtracker.Other.Constants
import com.example.runningtracker.R
import com.example.runningtracker.UI.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
class ServiceModule {

    @ServiceScoped
    @Provides
    fun provideFusedLocationProviderClient(@ApplicationContext app: Context) = LocationServices.getFusedLocationProviderClient(app)

    @ServiceScoped
    @Provides
    fun provideMainActivityPendingIntent(@ApplicationContext app : Context) = PendingIntent.getActivity(app,0,
    Intent(app, MainActivity::class.java).also {//pending intent is used to send the user directly to the tracking fragment
        it.action = Constants.ACTION_SHOW_TRACKING_FRAGMENT
    },
    PendingIntent.FLAG_UPDATE_CURRENT
    )


    @ServiceScoped
    @Provides
    fun provideBaseNotificationBuilder(@ApplicationContext app:Context, pendingIntent: PendingIntent) = NotificationCompat.Builder(app,
        Constants.NOTIFICATION_CHANNEL_ID
    )
        .setAutoCancel(false)
        .setOngoing(true)
        .setSmallIcon(R.drawable.ic_directions_run_black_24dp)
        .setContentTitle("Running App")
        .setContentText("00:00:00")
        .setContentIntent(pendingIntent)//this is the intent that manages this notification



}