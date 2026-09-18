package com.elshadai.scripturehunt

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ScriptureRevealCard(
    scripture: Scripture,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E7)),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Scripture revealed!",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0xFF1A4D4A),
                    fontWeight = FontWeight.Bold,
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF3D2914))
                }
            }
            Text(
                text = scripture.reference,
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFF3D2914),
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = scripture.theme,
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFFC9A227),
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp),
            )
            Text(
                text = "“${scripture.text}”",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF3D2914),
                fontStyle = FontStyle.Italic,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "KJV · public domain",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF6B5A45),
            )
        }
    }
}

@Composable
fun DiscoveredCollectionSheet(
    items: List<Scripture>,
    onClose: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 32.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Discovered Scriptures",
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFF3D2914),
                fontWeight = FontWeight.Bold,
            )
            TextButton(onClick = onClose) { Text("Close") }
        }
        Spacer(modifier = Modifier.height(8.dp))
        if (items.isEmpty()) {
            Text(
                text = "No verses found yet. Tap a detected plane in AR.",
                color = Color(0xFF6B5A45),
                modifier = Modifier.padding(vertical = 24.dp),
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(items, key = { it.id }) { scripture ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = scripture.reference,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF3D2914),
                            )
                            Text(
                                text = scripture.theme,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFC9A227),
                            )
                            Text(
                                text = scripture.text,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF3D2914),
                                modifier = Modifier.padding(top = 6.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}
