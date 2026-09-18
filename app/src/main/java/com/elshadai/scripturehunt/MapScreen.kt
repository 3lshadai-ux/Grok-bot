package com.elshadai.scripturehunt

import android.location.Location
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

private val Parchment = Color(0xFFFFF8E7)
private val Teal = Color(0xFF1A4D4A)
private val Gold = Color(0xFFC9A227)
private val Ink = Color(0xFF3D2914)
private val MutedInk = Color(0xFF6B5A45)

@Composable
fun MapsKeyMissingScreen(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize().background(Teal).padding(24.dp), contentAlignment = Alignment.Center) {
        Card(colors = CardDefaults.cardColors(Parchment), shape = RoundedCornerShape(20.dp)) {
            Column(Modifier.padding(24.dp)) {
                Text("Map key needed", style = MaterialTheme.typography.titleLarge, color = Ink, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                Text(
                    "Add a Google Maps SDK for Android API key so nearby scripture spawns can appear.",
                    color = Ink,
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    "1. Enable Maps SDK for Android in Google Cloud\n" +
                        "2. Copy local.properties.example → local.properties\n" +
                        "3. Set MAPS_API_KEY=your_key_here\n" +
                        "4. Rebuild the app",
                    color = MutedInk,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Composable
fun LocationDeniedScreen(onRequestPermission: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize().background(Teal).padding(24.dp), contentAlignment = Alignment.Center) {
        Card(colors = CardDefaults.cardColors(Parchment), shape = RoundedCornerShape(20.dp)) {
            Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.MyLocation, null, tint = Gold)
                Spacer(Modifier.height(12.dp))
                Text("Location needed", style = MaterialTheme.typography.titleLarge, color = Ink, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text(
                    "Scripture Hunt uses your place on the map to rest hidden words nearby.",
                    color = Ink,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(16.dp))
                Button(onRequestPermission, colors = ButtonDefaults.buttonColors(Gold, Ink)) {
                    Text("Allow location")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScriptureMapScreen(
    mapsApiKeyPresent: Boolean,
    permissionState: LocationPermissionState,
    playerLocation: Location?,
    spawns: List<ScriptureSpawn>,
    onRequestPermission: () -> Unit,
    onRevealInAr: (ScriptureSpawn) -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        !mapsApiKeyPresent -> MapsKeyMissingScreen(modifier)
        permissionState != LocationPermissionState.Granted -> LocationDeniedScreen(onRequestPermission, modifier)
        playerLocation == null -> Box(modifier.fillMaxSize().background(Teal), contentAlignment = Alignment.Center) {
            Text("Seeking your place on the map…", color = Parchment)
        }
        else -> MapWithSpawns(playerLocation, spawns, onRevealInAr, modifier)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MapWithSpawns(
    playerLocation: Location,
    spawns: List<ScriptureSpawn>,
    onRevealInAr: (ScriptureSpawn) -> Unit,
    modifier: Modifier = Modifier,
) {
    val playerLatLng = LatLng(playerLocation.latitude, playerLocation.longitude)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(playerLatLng, 16f)
    }
    var selected by remember { mutableStateOf<ScriptureSpawn?>(null) }
    var followed by remember { mutableStateOf(false) }

    LaunchedEffect(playerLatLng.latitude, playerLatLng.longitude) {
        if (!followed) {
            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(playerLatLng, 16f))
            followed = true
        }
    }

    val nearby = spawns
        .filter { !it.isClaimed() }
        .map { it to it.distanceMetersTo(playerLocation.latitude, playerLocation.longitude) }
        .filter { it.second <= SpawnGenerator.PROXIMITY_UNLOCK_M }
        .minByOrNull { it.second }
        ?.first

    val playerMarker = rememberMarkerState(position = playerLatLng)
    LaunchedEffect(playerLatLng.latitude, playerLatLng.longitude) {
        playerMarker.position = playerLatLng
    }

    Box(modifier.fillMaxSize()) {
        GoogleMap(Modifier.fillMaxSize(), cameraPositionState = cameraPositionState) {
            Marker(
                state = playerMarker,
                title = "You",
                snippet = "Seeker",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
            )
            spawns.forEach { spawn ->
                val claimed = spawn.isClaimed()
                val dist = spawn.distanceMetersTo(playerLocation.latitude, playerLocation.longitude)
                val near = !claimed && dist <= SpawnGenerator.PROXIMITY_UNLOCK_M
                val hue = when {
                    claimed -> BitmapDescriptorFactory.HUE_GREEN
                    near -> BitmapDescriptorFactory.HUE_YELLOW
                    else -> BitmapDescriptorFactory.HUE_ORANGE
                }
                val title = when {
                    claimed -> spawn.scripture?.reference ?: "Found word"
                    near -> "Scripture nearby"
                    else -> "Hidden word"
                }
                val state = rememberMarkerState(position = LatLng(spawn.latitude, spawn.longitude))
                Marker(
                    state = state,
                    title = title,
                    snippet = if (claimed) "Already gathered" else "${dist.toInt()} m away",
                    icon = BitmapDescriptorFactory.defaultMarker(hue),
                    onClick = {
                        selected = spawn
                        false
                    },
                )
            }
        }

        Surface(
            color = Color(0xCC1A4D4A),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.align(Alignment.TopCenter).padding(16.dp),
        ) {
            Text(
                if (nearby != null) "A scripture is near — walk closer and reveal it in AR"
                else "Hidden words rest nearby. Walk the map to draw near.",
                color = Parchment,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            )
        }

        nearby?.let { spawn ->
            val dist = spawn.distanceMetersTo(playerLocation.latitude, playerLocation.longitude)
            Card(
                modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp).fillMaxWidth(),
                colors = CardDefaults.cardColors(Parchment),
                elevation = CardDefaults.cardElevation(8.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Scripture nearby", fontWeight = FontWeight.Bold, color = Teal)
                    Text("About ${dist.toInt()} m away — a hidden word waits.", color = Ink)
                    Spacer(Modifier.height(10.dp))
                    Button(
                        { onRevealInAr(spawn) },
                        colors = ButtonDefaults.buttonColors(Gold, Ink),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(Icons.Default.Explore, null)
                        Spacer(Modifier.padding(4.dp))
                        Text("Reveal in AR")
                    }
                }
            }
        }

        selected?.let { spawn ->
            val dist = spawn.distanceMetersTo(playerLocation.latitude, playerLocation.longitude)
            val claimed = spawn.isClaimed()
            val near = !claimed && dist <= SpawnGenerator.PROXIMITY_UNLOCK_M
            ModalBottomSheet(
                onDismissRequest = { selected = null },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                containerColor = Parchment,
            ) {
                Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 32.dp)) {
                    Text(
                        when {
                            claimed -> "Word gathered"
                            near -> "Scripture nearby"
                            else -> "Hidden word"
                        },
                        style = MaterialTheme.typography.titleLarge,
                        color = Ink,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        when {
                            claimed -> spawn.scripture?.reference ?: "A verse you already carry"
                            near -> "You are close enough to uncover this verse in AR."
                            else -> "Draw nearer (${dist.toInt()} m). Proximity unlocks the AR hunt."
                        },
                        color = MutedInk,
                    )
                    if (claimed) {
                        spawn.scripture?.let { s ->
                            Spacer(Modifier.height(12.dp))
                            Text(s.reference, fontWeight = FontWeight.SemiBold, color = Ink)
                            Text(s.theme, color = Gold, style = MaterialTheme.typography.labelMedium)
                            Text(s.text, color = Ink, modifier = Modifier.padding(top = 6.dp))
                        }
                    }
                    if (near) {
                        Spacer(Modifier.height(16.dp))
                        Button(
                            {
                                selected = null
                                onRevealInAr(spawn)
                            },
                            colors = ButtonDefaults.buttonColors(Gold, Ink),
                            modifier = Modifier.fillMaxWidth(),
                        ) { Text("Reveal in AR") }
                    } else if (!claimed) {
                        Spacer(Modifier.height(16.dp))
                        OutlinedButton({ selected = null }, modifier = Modifier.fillMaxWidth()) {
                            Text("Keep walking")
                        }
                    }
                    TextButton({ selected = null }) { Text("Close") }
                }
            }
        }
    }
}
