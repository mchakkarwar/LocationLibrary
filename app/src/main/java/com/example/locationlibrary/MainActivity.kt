package com.example.locationlibrary

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.LocationCallbacks

class MainActivity : AppCompatActivity(), LocationCallbacks {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val locationClient = LocationClient(this, this, lifecycle)
        lifecycle.addObserver(locationClient)
    }

    override fun onAccessLocationDenied() {
    }

    override fun onAccessLocationGranted() {
    }

    override fun onLocationSettingDisabled() {
    }

    override fun onLocationSettingEnabled() {
    }

    override fun receiveLocation() {
    }
}