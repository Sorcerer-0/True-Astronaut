package dev.sorcerer.trueastronaut

import android.content.Context
import android.provider.Settings

/**
 * ProjectInfinity-X native settings backend.
 * Only boolean keys with observed 0/1 values are exposed as switches.
 * Enum and color keys remain hidden until accepted values are confirmed.
 */
object NativeSystemSettings {
    const val BATTERY_BAR_ENABLED = "statusbar_battery_bar"
    const val BATTERY_BAR_ANIMATE = "statusbar_battery_bar_animate"
    const val BATTERY_BAR_CHARGING_COLOR_ENABLED = "statusbar_battery_bar_enable_charging_color"
    const val BATTERY_BAR_BLEND_COLOR = "statusbar_battery_bar_blend_color"
    const val BATTERY_BAR_BLEND_COLOR_REVERSE = "statusbar_battery_bar_blend_color_reverse"
    const val CLOCK_BOLD = "status_bar_clock_bold"
    const val CLOCK_SECONDS = "status_bar_clock_seconds"

    fun canWrite(context: Context): Boolean = Settings.System.canWrite(context)

    fun readBoolean(context: Context, key: String): Boolean? {
        val raw = Settings.System.getString(context.contentResolver, key) ?: return null
        return when (raw) {
            "0" -> false
            "1" -> true
            else -> null
        }
    }

    fun writeBoolean(context: Context, key: String, enabled: Boolean): Boolean {
        if (!canWrite(context)) return false
        val written = Settings.System.putInt(context.contentResolver, key, if (enabled) 1 else 0)
        if (!written) return false
        return readBoolean(context, key) == enabled
    }
}
