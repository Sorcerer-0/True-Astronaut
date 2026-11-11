package dev.sorcerer.trueastronaut

import android.content.Context
import android.provider.Settings

object NativeSystemSettings {
    const val BATTERY_BAR_ENABLED = "statusbar_battery_bar"
    const val BATTERY_BAR_ANIMATE = "statusbar_battery_bar_animate"
    const val BATTERY_BAR_CHARGING_COLOR_ENABLED = "statusbar_battery_bar_enable_charging_color"
    const val BATTERY_BAR_BLEND_COLOR = "statusbar_battery_bar_blend_color"
    const val BATTERY_BAR_BLEND_COLOR_REVERSE = "statusbar_battery_bar_blend_color_reverse"

    const val CLOCK_BOLD = "true_astronaut_clock_bold"
    const val CLOCK_SECONDS = "true_astronaut_clock_seconds"

    private val moduleKeys = setOf(CLOCK_BOLD, CLOCK_SECONDS)

    fun canWrite(context: Context): Boolean = Settings.System.canWrite(context)

    fun readBoolean(context: Context, key: String): Boolean? {
        return try {
            val raw = Settings.System.getString(context.contentResolver, key)
            if (raw == null && key in moduleKeys) return false
            when (raw) {
                "0" -> false
                "1" -> true
                else -> null
            }
        } catch (_: SecurityException) {
            null
        }
    }

    fun writeBoolean(context: Context, key: String, enabled: Boolean): Boolean {
        if (!canWrite(context)) return false
        return try {
            val written = Settings.System.putInt(context.contentResolver, key, if (enabled) 1 else 0)
            written && readBoolean(context, key) == enabled
        } catch (_: SecurityException) {
            false
        }
    }
}
