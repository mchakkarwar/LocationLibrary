package com.sample.locationlibrary

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import androidx.annotation.IntDef
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.sample.LocationCallbacks
import createLocationRequest
import createLocationSettingRequest
import isLocationPermissionEnabled
import requestToEnableLocationPermission
import showLocationSettingDialog
import toLocation

class LocationClient(
    private val locationCallbacks: LocationCallbacks,
    private val context: Context,
    val interval: Long = 10000,
    val fastestInterval: Long = 5000,
    @LocationType val locationType: Int = HIGH_ACCURACY
) :
    DefaultLifecycleObserver {

    companion object {
        const val REQUEST_CHECK_SETTINGS = 999

        @IntDef(HIGH_ACCURACY, BALANCED_POWER_ACCURACY, LOW_POWER, NO_POWER)
        @Retention(AnnotationRetention.SOURCE)
        annotation class LocationType

        const val HIGH_ACCURACY = LocationRequest.PRIORITY_HIGH_ACCURACY
        const val BALANCED_POWER_ACCURACY = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        const val LOW_POWER = LocationRequest.PRIORITY_LOW_POWER
        const val NO_POWER = LocationRequest.PRIORITY_NO_POWER
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)
            val currentLocation = p0.locations[0].toLocation()
            locationCallbacks.onLocationReceived(currentLocation)
        }
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        if (isLocationPermissionEnabled(context)) {
            showLocationSettingDialog(
                context = context,
                locationSettingsRequest = createLocationSettingRequest(
                    createLocationRequest(
                        interval = interval,
                        fastestInterval = fastestInterval,
                        priority = locationType
                    )
                ),
                userEnabledLocationSetting = { locationCallbacks.onLocationSettingEnabled() },
                userDeniedLocationSetting = { locationCallbacks.onLocationSettingDisabled() })
        } else {
            requestToEnableLocationPermission(
                context,
                permissionGrantedCallback = { locationCallbacks.onAccessLocationGranted() },
                permissionNotGrantedCallback = { locationCallbacks.onAccessLocationDenied() })
        }
    }

    @SuppressLint("MissingPermission")
    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        showLocationSettingDialog(
            context = context,
            locationSettingsRequest = createLocationSettingRequest(
                createLocationRequest(
                    interval = interval,
                    fastestInterval = fastestInterval,
                    priority = locationType
                )
            ),
            userEnabledLocationSetting = { locationCallbacks.onLocationSettingEnabled() },
            userDeniedLocationSetting = { locationCallbacks.onLocationSettingDisabled() })
        fusedLocationClient.requestLocationUpdates(
            createLocationRequest(
                interval = interval,
                fastestInterval = fastestInterval,
                priority = locationType
            ),
            locationCallback,
            Looper.getMainLooper()
        )
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

}