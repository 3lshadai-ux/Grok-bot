package com.elshadai.scripturehunt

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScriptureHuntApp(
    onArViewReady: (ARSceneView) -> Unit,
    onRevealHandlerReady: ((() -> Unit) -> Unit),
) {
    var revealed by remember { mutableStateOf<Scripture?>(null) }
    var showCollection by remember { mutableStateOf(false) }
    var planeHint by remember { mutableStateOf(true) }
    val discovered = DiscoveredScripturesStore.discovered

    SideEffect {
        onRevealHandlerReady {
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
                    text = if (planeHint) {
                        "Move slowly — find a flat surface where a verse may be hidden"
                    } else {
                        "Tap another plane to uncover more scripture"
                    },
                    color = Color(0xFFFFF8E7),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                )
            }
        }

        FloatingActionButton(
            onClick = { showCollection = true },
            containerColor = Color(0xFFC9A227),
            contentColor = Color(0xFF3D2914),
            modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp),
        ) {
            BadgedBox(
                badge = {
                    if (discovered.isNotEmpty()) Badge { Text("${discovered.size}") }
                },
            ) {
                Icon(Icons.Default.AutoStories, contentDescription = "My collection")
            }
        }

        revealed?.let { scripture ->
            ScriptureRevealCard(
                scripture = scripture,
                onDismiss = { revealed = null },
                modifier = Modifier.align(Alignment.Center).padding(24.dp),
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
