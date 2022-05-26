package com.sample.locationlibrary

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sample.LocationCallbacks
import com.sample.UsersLocation

class MainActivity : AppCompatActivity(), LocationCallbacks {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val locationClient = LocationClient(this, this, 100, 100)
        lifecycle.addObserver(locationClient)
    }

    override fun onAccessLocationDenied() {
    }

    override fun onLocationReceived(location: UsersLocation) {
        Toast.makeText(this, location.toString(), Toast.LENGTH_SHORT).show()
    }

    override fun onAccessLocationGranted() {
    }

    override fun onLocationSettingDisabled() {
    }

    override fun onLocationSettingEnabled() {
    }

}