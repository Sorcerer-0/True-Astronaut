package dev.sorcerer.trueastronaut

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    var refreshToken by mutableIntStateOf(0)
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { TrueAstronautDashboard(refreshToken = refreshToken) }
    }

    override fun onResume() {
        super.onResume()
        refreshToken++
    }
}

private data class NativeToggle(
    val key: String,
    val title: String,
    val description: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TrueAstronautDashboard(refreshToken: Int) {
    val context = LocalContext.current
    var canWrite by remember { mutableStateOf(NativeSystemSettings.canWrite(context)) }
    var values by remember { mutableStateOf<Map<String, Boolean?>>(emptyMap()) }
    var lastResult by remember { mutableStateOf<String?>(null) }

    val batteryToggles = listOf(
        NativeToggle(NativeSystemSettings.BATTERY_BAR_ENABLED, "Enable Battery Bar", "Show the ROM-native battery bar in the status bar."),
        NativeToggle(NativeSystemSettings.BATTERY_BAR_ANIMATE, "Animation", "Enable the native battery bar animation."),
        NativeToggle(NativeSystemSettings.BATTERY_BAR_CHARGING_COLOR_ENABLED, "Charging color", "Use the configured charging color while charging."),
        NativeToggle(NativeSystemSettings.BATTERY_BAR_BLEND_COLOR, "Blend colors", "Enable the native color-blending mode."),
        NativeToggle(NativeSystemSettings.BATTERY_BAR_BLEND_COLOR_REVERSE, "Reverse blend", "Reverse the native blend direction.")
    )
    val clockToggles = listOf(
        NativeToggle(NativeSystemSettings.CLOCK_BOLD, "Bold clock", "Use the ROM's bold status-bar clock style."),
        NativeToggle(NativeSystemSettings.CLOCK_SECONDS, "Show seconds", "Display seconds in the status-bar clock.")
    )

    fun reload() {
        canWrite = NativeSystemSettings.canWrite(context)
        values = (batteryToggles + clockToggles).associate {
            it.key to NativeSystemSettings.readBoolean(context, it.key)
        }
    }

    LaunchedEffect(refreshToken) { reload() }

    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text("TRUE ASTRONAUT")
                            Text("ProjectInfinity-X native controls", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Infinix Hot 30 (X6831)", style = MaterialTheme.typography.titleMedium)
                        Text("Android 16 · ROM-native Settings.System backend", style = MaterialTheme.typography.bodySmall)
                        Text(
                            if (canWrite) "System settings access: granted" else "System settings access: required",
                            color = if (canWrite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelMedium
                        )
                        if (!canWrite) {
                            Button(onClick = {
                                context.startActivity(
                                    Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).apply {
                                        data = Uri.parse("package:${context.packageName}")
                                    }
                                )
                            }) {
                                Text("Grant system settings access")
                            }
                        }
                    }
                }

                FeatureSection(
                    title = "Battery Bar",
                    subtitle = "Uses ProjectInfinity-X's built-in BatteryBar implementation.",
                    toggles = batteryToggles,
                    values = values,
                    enabled = canWrite,
                    onToggle = { key, value ->
                        val ok = NativeSystemSettings.writeBoolean(context, key, value)
                        lastResult = if (ok) "Saved: $key = ${if (value) 1 else 0}" else "Write failed: $key"
                        reload()
                    }
                )

                FeatureSection(
                    title = "Status Bar Clock",
                    subtitle = "Uses the ROM's existing Clock implementation.",
                    toggles = clockToggles,
                    values = values,
                    enabled = canWrite,
                    onToggle = { key, value ->
                        val ok = NativeSystemSettings.writeBoolean(context, key, value)
                        lastResult = if (ok) "Saved: $key = ${if (value) 1 else 0}" else "Write failed: $key"
                        reload()
                    }
                )

                if (lastResult != null) {
                    Text(lastResult!!, style = MaterialTheme.typography.bodySmall)
                }

                Text(
                    "Only verified boolean settings are exposed. Style, thickness, date format, date position and color values are withheld until their valid ranges are confirmed.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun FeatureSection(
    title: String,
    subtitle: String,
    toggles: List<NativeToggle>,
    values: Map<String, Boolean?>,
    enabled: Boolean,
    onToggle: (String, Boolean) -> Unit
) {
    Card {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            HorizontalDivider()
            toggles.forEachIndexed { index, item ->
                val value = values[item.key]
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(item.title, style = MaterialTheme.typography.bodyLarge)
                        Text(
                            item.description + if (value == null) " · Unsupported or unset" else "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = value ?: false,
                        enabled = enabled && value != null,
                        onCheckedChange = { onToggle(item.key, it) }
                    )
                }
                if (index != toggles.lastIndex) HorizontalDivider()
            }
        }
    }
}
