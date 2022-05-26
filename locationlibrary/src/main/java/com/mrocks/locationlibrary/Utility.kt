import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.mrocks.UsersLocation
import com.mrocks.LocationCallbacks
import com.mrocks.locationlibrary.LocationClient.Companion.REQUEST_CHECK_SETTINGS
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import com.mrocks.locationlibrary.LocationClient
import java.util.Date

fun isLocationPermissionEnabled(context: Context): Boolean {
    return !(ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) != PackageManager.PERMISSION_GRANTED)
}

fun requestToEnableLocationPermission(
    context: Context,
    permissionGrantedCallback: () -> Unit,
    permissionNotGrantedCallback: () -> Unit,
) {
    val requestPermissionLauncher =
        (context as ComponentActivity).registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                permissionGrantedCallback.invoke()
            } else {
                permissionNotGrantedCallback.invoke()
            }
        }
    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
}

fun createLocationRequest(
    interval: Long = 10000,
    fastestInterval: Long = 5000,
    priority: Int = LocationRequest.PRIORITY_HIGH_ACCURACY
): LocationRequest {
    val locationRequest = LocationRequest.create().apply {
        this.interval = interval
        this.fastestInterval = fastestInterval
        this.priority = priority
    }
    return locationRequest
}

fun createLocationSettingRequest(locationRequest: LocationRequest): LocationSettingsRequest {
    return LocationSettingsRequest.Builder()
        .addLocationRequest(locationRequest).build()
}

fun showLocationSettingDialog(
    context: Context,
    locationSettingsRequest: LocationSettingsRequest,
    userEnabledLocationSetting: () -> Unit,
    userDeniedLocationSetting: () -> Unit
) {
    val client: SettingsClient = LocationServices.getSettingsClient(context)
    val task: Task<LocationSettingsResponse> = client.checkLocationSettings(locationSettingsRequest)
    task.addOnSuccessListener {
        userEnabledLocationSetting.invoke()
    }.addOnCanceledListener {
        userDeniedLocationSetting.invoke()
    }
    task.addOnFailureListener { exception ->
        if (exception is ResolvableApiException) {
            // UsersLocation settings are not satisfied, but this can be fixed
            // by showing the user a dialog.
            try {
                // Show the dialog by calling startResolutionForResult(),
                // and check the result in onActivityResult().
                exception.startResolutionForResult(
                    context as Activity,
                    REQUEST_CHECK_SETTINGS
                )
            } catch (sendEx: IntentSender.SendIntentException) {
                // Ignore the error.
            }
        }
    }
}

@SuppressLint("MissingPermission")
fun getCurrentLocation(
    context: Context, locationCallbacks: LocationCallbacks, interval: Long = 10000,
    fastestInterval: Long = 5000,
    @LocationClient.Companion.LocationType locationType: Int = LocationClient.HIGH_ACCURACY
) {
    val cts = CancellationTokenSource()
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
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
            userEnabledLocationSetting = {
                fusedLocationClient.getCurrentLocation(
                    LocationRequest.PRIORITY_HIGH_ACCURACY,
                    cts.token
                ).addOnSuccessListener { currentLocation ->
                    locationCallbacks.onLocationReceived(currentLocation.toLocation())
                    Toast.makeText(context, "$currentLocation", Toast.LENGTH_SHORT)
                        .show()
                }.addOnFailureListener { e ->
                    Toast.makeText(context, "Unable to get location.", Toast.LENGTH_SHORT)
                        .show()
                }
            },
            userDeniedLocationSetting = {})

    } else {
        requestToEnableLocationPermission(
            context,
            permissionGrantedCallback = { locationCallbacks.onAccessLocationGranted() },
            permissionNotGrantedCallback = { locationCallbacks.onAccessLocationDenied() })
    }
}

fun Location.toLocation() = UsersLocation(
    latitude,
    longitude,
    elapsedRealtimeNanos,
    Date(time),
    speed,
    bearing,
    altitude,
    accuracy,
    provider,
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        bearingAccuracyDegrees
    } else {
        null
    },
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        speedAccuracyMetersPerSecond
    } else {
        null
    },
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        verticalAccuracyMeters
    } else {
        null
    }
)
