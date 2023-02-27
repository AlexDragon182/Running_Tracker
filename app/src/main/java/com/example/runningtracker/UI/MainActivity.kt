package com.example.runningtracker.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.runningtracker.R
import com.example.runningtracker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}