package com.elshadai.scripturehunt

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import io.github.sceneview.ar.ARSceneView

class MainActivity : ComponentActivity() {

    private var arSceneView: ARSceneView? = null
    private var revealHandler: (() -> Unit)? = null
    private lateinit var locationTracker: LocationTracker

    private val cameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (!granted) {
            Toast.makeText(this, R.string.camera_rationale, Toast.LENGTH_LONG).show()
        }
    }

    private val locationPermission = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { result ->
        val granted = result[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        locationTracker.onPermissionResult(granted)
        if (!granted) {
            Toast.makeText(this, R.string.location_rationale, Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationTracker = LocationTracker(this)
        val mapsKeyPresent = BuildConfig.MAPS_API_KEY.isNotBlank()
        ensureCameraPermission()
        locationTracker.refreshPermissionState()
        if (locationTracker.permissionState == LocationPermissionState.Granted) {
            locationTracker.startUpdates()
        }

        setContent {
            ScriptureHuntTheme {
                ScriptureHuntApp(
                    mapsApiKeyPresent = mapsKeyPresent,
                    permissionState = locationTracker.permissionState,
                    playerLocation = locationTracker.lastLocation,
                    onRequestLocationPermission = { requestLocationPermission() },
                    onArViewReady = { view ->
                        arSceneView = view
                        view.planeRenderer.isEnabled = true
                        // Tap plane → reveal scripture card (UI), not a 3D cube.
                        view.onTapAr = { _, _ -> revealHandler?.invoke() }
                    },
                    onRevealHandlerReady = { handler -> revealHandler = handler },
                )
            }
        }
    }

    private fun ensureCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            cameraPermission.launch(Manifest.permission.CAMERA)
        }
    }

    private fun requestLocationPermission() {
        locationPermission.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ),
        )
    }

    override fun onResume() {
        super.onResume()
        locationTracker.refreshPermissionState()
        if (locationTracker.permissionState == LocationPermissionState.Granted) {
            locationTracker.startUpdates()
        }
    }

    override fun onPause() {
        locationTracker.stopUpdates()
        super.onPause()
    }

    override fun onDestroy() {
        locationTracker.stopUpdates()
        arSceneView = null
        revealHandler = null
        super.onDestroy()
    }
}
