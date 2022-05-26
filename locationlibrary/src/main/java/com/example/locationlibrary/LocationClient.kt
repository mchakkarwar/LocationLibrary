package com.example.locationlibrary

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import android.widget.Toast
import androidx.lifecycle.*
import com.example.LocationCallbacks
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import createLocationRequest
import createLocationSettingRequest
import isLocationPermissionEnabled
import requestToEnableLocationPermission
import showLocationSettingDialog

class LocationClient(
    private val locationCallbacks: LocationCallbacks,
    private val context: Context,
    private val lifecycle: Lifecycle
) :
    DefaultLifecycleObserver {

    companion object {
        const val REQUEST_CHECK_SETTINGS = 999
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)
            locationCallbacks.receiveLocation()
            Toast.makeText(context, "$p0", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        if (isLocationPermissionEnabled(context)) {
            showLocationSettingDialog(
                context = context,
                locationSettingsRequest = createLocationSettingRequest(createLocationRequest()),
                userEnabledLocationSetting = {},
                userDeniedLocationSetting = {})
        } else {
            requestToEnableLocationPermission(
                context,
                permissionGrantedCallback = {},
                permissionNotGrantedCallback = {})
        }
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        showLocationSettingDialog(
            context = context,
            locationSettingsRequest = createLocationSettingRequest(createLocationRequest()),
            userEnabledLocationSetting = {},
            userDeniedLocationSetting = {})
    }

    @SuppressLint("MissingPermission")
    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        fusedLocationClient.requestLocationUpdates(
            createLocationRequest(),
            locationCallback,
            Looper.getMainLooper()
        )
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

}