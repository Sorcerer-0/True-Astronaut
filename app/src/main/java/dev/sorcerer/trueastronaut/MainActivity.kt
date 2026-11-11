package dev.sorcerer.trueastronaut

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BatteryChargingFull
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { TrueAstronautDashboard() }
    }
}

private data class FeatureGroup(
    val title: String,
    val summary: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TrueAstronautDashboard() {
    val groups = listOf(
        FeatureGroup("Status Bar", "Battery bar, clock, Dynamic Bar, lyrics", Icons.Outlined.BatteryChargingFull),
        FeatureGroup("Lockscreen", "Clock, media art and fingerprint effects", Icons.Outlined.Lock),
        FeatureGroup("Themes", "Edge light, charging animation, smart pixels", Icons.Outlined.ColorLens),
        FeatureGroup("Gestures", "Shake actions and sensor controls", Icons.Outlined.Bolt),
        FeatureGroup("System", "App spoofing and idle manager", Icons.Outlined.Settings),
        FeatureGroup("Configuration", "Profiles, backup and diagnostics", Icons.Outlined.Tune)
    )

    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text("TRUE ASTRONAUT")
                            Text("Singularity control center", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("ProjectInfinity-X · Android 16", style = MaterialTheme.typography.titleMedium)
                        Text("Infinix Hot 30 (X6831)", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "Runtime: awaiting LSPosed adapter verification",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                Text("FEATURE CATEGORIES", style = MaterialTheme.typography.labelLarge)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(bottom = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(groups) { group ->
                        Card {
                            Column(
                                Modifier.fillMaxWidth().padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(group.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Text(group.title, style = MaterialTheme.typography.titleSmall)
                                Text(group.summary, style = MaterialTheme.typography.bodySmall)
                                Text(
                                    "Backend pending",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
