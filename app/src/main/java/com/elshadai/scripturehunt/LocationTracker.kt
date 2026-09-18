package com.elshadai.scripturehunt

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

enum class LocationPermissionState {
    Unknown,
    Granted,
    Denied,
}

/**
 * Thin Fused Location Provider wrapper exposed as Compose-friendly state.
 */
class LocationTracker(private val context: Context) {

    var permissionState by mutableStateOf(LocationPermissionState.Unknown)
        private set

    var lastLocation by mutableStateOf<Location?>(null)
        private set

    var isUpdating by mutableStateOf(false)
        private set

    private val client = LocationServices.getFusedLocationProviderClient(context)

    private val callback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            result.lastLocation?.let { lastLocation = it }
        }
    }

    fun refreshPermissionState() {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        permissionState = if (fine == PackageManager.PERMISSION_GRANTED ||
            coarse == PackageManager.PERMISSION_GRANTED
        ) {
            LocationPermissionState.Granted
        } else {
            LocationPermissionState.Denied
        }
    }

    fun onPermissionResult(granted: Boolean) {
        permissionState = if (granted) LocationPermissionState.Granted else LocationPermissionState.Denied
        if (granted) startUpdates() else stopUpdates()
    }

    @SuppressLint("MissingPermission")
    fun startUpdates() {
        refreshPermissionState()
        if (permissionState != LocationPermissionState.Granted) return
        if (isUpdating) return

        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2_000L)
            .setMinUpdateIntervalMillis(1_000L)
            .setMinUpdateDistanceMeters(3f)
            .build()

        client.requestLocationUpdates(request, callback, Looper.getMainLooper())
        isUpdating = true
        client.lastLocation.addOnSuccessListener { loc ->
            if (loc != null) lastLocation = loc
        }
    }

    fun stopUpdates() {
        if (!isUpdating) return
        client.removeLocationUpdates(callback)
        isUpdating = false
    }
}
