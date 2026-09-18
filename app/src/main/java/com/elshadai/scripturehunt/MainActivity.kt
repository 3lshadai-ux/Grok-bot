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

    private val cameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            Toast.makeText(this, R.string.camera_rationale, Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            cameraPermission.launch(Manifest.permission.CAMERA)
        }
        setContent {
            ScriptureHuntTheme {
                ScriptureHuntApp(
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

    override fun onDestroy() {
        arSceneView = null
        revealHandler = null
        super.onDestroy()
    }
}
