package com.example.runningtracker.UI

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.runningtracker.Other.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.example.runningtracker.R
import com.example.runningtracker.database.RunDAO
import com.example.runningtracker.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint // to annotate view models with Dagger Hilt
class MainActivity : AppCompatActivity() {

    private val navController by lazy { (supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment).navController }


private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navigateTrackingFragmentIfNeeded(intent)

        setSupportActionBar(binding.toolbar)
        binding.bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener{_, destination, _ ->
            when(destination.id){
                R.id.settingsFragment,R.id.runFragment,R.id.statisticsFragment ->
                    binding.bottomNavigationView.visibility = View.VISIBLE
                else -> binding.bottomNavigationView.visibility = View.GONE

            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateTrackingFragmentIfNeeded(intent)
    }

    private fun navigateTrackingFragmentIfNeeded(intent: Intent?){
        if(intent?.action == ACTION_SHOW_TRACKING_FRAGMENT){
            binding.bottomNavigationView.findNavController().navigate(R.id.action_global_trackingFragment)
        }
    }
}