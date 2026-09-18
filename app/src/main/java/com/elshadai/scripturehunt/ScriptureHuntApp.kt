package com.elshadai.scripturehunt

import android.location.Location
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import io.github.sceneview.ar.ARSceneView

enum class HuntTab { Ar, Map }

/** Backward-compatible AR-only entry before location wiring. */
@Composable
fun ScriptureHuntApp(
    onArViewReady: (ARSceneView) -> Unit,
    onRevealHandlerReady: ((() -> Unit) -> Unit),
) {
    ScriptureHuntApp(
        mapsApiKeyPresent = false,
        permissionState = LocationPermissionState.Unknown,
        playerLocation = null,
        onRequestLocationPermission = {},
        onArViewReady = onArViewReady,
        onRevealHandlerReady = onRevealHandlerReady,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScriptureHuntApp(
    mapsApiKeyPresent: Boolean,
    permissionState: LocationPermissionState,
    playerLocation: Location?,
    onRequestLocationPermission: () -> Unit,
    onArViewReady: (ARSceneView) -> Unit,
    onRevealHandlerReady: ((() -> Unit) -> Unit),
) {
    var tab by remember { mutableStateOf(HuntTab.Ar) }
    var revealed by remember { mutableStateOf<Scripture?>(null) }
    var showCollection by remember { mutableStateOf(false) }
    var planeHint by remember { mutableStateOf(true) }
    var activeSpawn by remember { mutableStateOf<ScriptureSpawn?>(null) }
    val discovered = DiscoveredScripturesStore.discovered
    val discoveredCount = discovered.size

    val cellKey = playerLocation?.let {
        SpawnGenerator.cellId(it.latitude, it.longitude)
    }
    val spawns = remember(cellKey) {
        val loc = playerLocation ?: return@remember emptyList()
        SpawnGenerator.generateAround(loc.latitude, loc.longitude)
    }

    SideEffect {
        onRevealHandlerReady {
            val targeted = activeSpawn
            if (targeted != null) {
                val scripture = targeted.scripture
                if (scripture != null) {
                    DiscoveredScripturesStore.discover(scripture)
                    revealed = scripture
                    planeHint = false
                    activeSpawn = null
                }
                return@onRevealHandlerReady
            }
            val next = DiscoveredScripturesStore.nextHidden()
            if (next == null) {
                revealed = ScriptureCatalog.all.random()
            } else {
                DiscoveredScripturesStore.discover(next)
                revealed = next
                planeHint = false
            }
        }
    }

    Scaffold(
        containerColor = Color(0xFF1A4D4A),
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF143F3D)) {
                NavigationBarItem(
                    selected = tab == HuntTab.Ar,
                    onClick = { tab = HuntTab.Ar },
                    icon = { Icon(Icons.Default.ViewInAr, contentDescription = "AR hunt") },
                    label = { Text("AR") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFC9A227),
                        selectedTextColor = Color(0xFFC9A227),
                        unselectedIconColor = Color(0xFFFFF8E7),
                        unselectedTextColor = Color(0xFFFFF8E7),
                        indicatorColor = Color(0xFF1A4D4A),
                    ),
                )
                NavigationBarItem(
                    selected = tab == HuntTab.Map,
                    onClick = { tab = HuntTab.Map },
                    icon = { Icon(Icons.Default.Map, contentDescription = "Map") },
                    label = { Text("Map") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFC9A227),
                        selectedTextColor = Color(0xFFC9A227),
                        unselectedIconColor = Color(0xFFFFF8E7),
                        unselectedTextColor = Color(0xFFFFF8E7),
                        indicatorColor = Color(0xFF1A4D4A),
                    ),
                )
            }
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            when (tab) {
                HuntTab.Map -> ScriptureMapScreen(
                    mapsApiKeyPresent = mapsApiKeyPresent,
                    permissionState = permissionState,
                    playerLocation = playerLocation,
                    spawns = spawns,
                    onRequestPermission = onRequestLocationPermission,
                    onRevealInAr = { spawn ->
                        if (!spawn.isClaimed()) {
                            activeSpawn = spawn
                            tab = HuntTab.Ar
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                )
                HuntTab.Ar -> ArHuntScreen(
                    activeSpawn = activeSpawn,
                    planeHint = planeHint,
                    revealed = revealed,
                    discoveredCount = discoveredCount,
                    onArViewReady = onArViewReady,
                    onShowCollection = { showCollection = true },
                    onDismissReveal = { revealed = null },
                    onClearActiveSpawn = { activeSpawn = null },
                )
            }

            if (showCollection) {
                ModalBottomSheet(
                    onDismissRequest = { showCollection = false },
                    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                    containerColor = Color(0xFFFFF8E7),
                ) {
                    DiscoveredCollectionSheet(
                        items = discovered.toList(),
                        onClose = { showCollection = false },
                    )
                }
            }
        }
    }
}

@Composable
private fun ArHuntScreen(
    activeSpawn: ScriptureSpawn?,
    planeHint: Boolean,
    revealed: Scripture?,
    discoveredCount: Int,
    onArViewReady: (ARSceneView) -> Unit,
    onShowCollection: () -> Unit,
    onDismissReveal: () -> Unit,
    onClearActiveSpawn: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx -> ARSceneView(ctx).also(onArViewReady) },
            modifier = Modifier.fillMaxSize(),
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Surface(color = Color(0xCC1A4D4A), shape = RoundedCornerShape(16.dp)) {
                Text(
                    text = when {
                        activeSpawn != null ->
                            "A map scripture awaits — find a flat surface and tap the plane"
                        planeHint ->
                            "Move slowly — find a flat surface where a verse may be hidden"
                        else ->
                            "Tap another plane to uncover more scripture"
                    },
                    color = Color(0xFFFFF8E7),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                )
            }
            if (activeSpawn != null) {
                Surface(
                    color = Color(0xE6C9A227),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(top = 8.dp),
                ) {
                    Text(
                        text = "Hunting a nearby spawn · tap Cancel to free-roam",
                        color = Color(0xFF3D2914),
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = onShowCollection,
            containerColor = Color(0xFFC9A227),
            contentColor = Color(0xFF3D2914),
            modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp),
        ) {
            BadgedBox(
                badge = {
                    if (discoveredCount > 0) Badge { Text("$discoveredCount") }
                },
            ) {
                Icon(Icons.Default.AutoStories, contentDescription = "My collection")
            }
        }

        if (activeSpawn != null) {
            FloatingActionButton(
                onClick = onClearActiveSpawn,
                containerColor = Color(0xFFFFF8E7),
                contentColor = Color(0xFF3D2914),
                modifier = Modifier.align(Alignment.BottomStart).padding(20.dp),
            ) {
                Text("✕", style = MaterialTheme.typography.titleMedium)
            }
        }

        revealed?.let { scripture ->
            ScriptureRevealCard(
                scripture = scripture,
                onDismiss = onDismissReveal,
                modifier = Modifier.align(Alignment.Center).padding(24.dp),
            )
        }
    }
}
