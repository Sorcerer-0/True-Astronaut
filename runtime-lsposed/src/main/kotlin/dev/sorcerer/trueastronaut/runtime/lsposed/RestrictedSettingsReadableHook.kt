package dev.sorcerer.trueastronaut.runtime.lsposed

import android.content.ContentResolver
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge

/** Allowlist only the exact settings keys read by the companion app. */
internal object RestrictedSettingsReadableHook {
    private const val CACHE_CLASS = "android.provider.Settings\$NameValueCache"
    private val allowedKeys = setOf(
        "statusbar_battery_bar",
        "statusbar_battery_bar_animate",
        "statusbar_battery_bar_enable_charging_color",
        "statusbar_battery_bar_blend_color",
        "statusbar_battery_bar_blend_color_reverse",
        "true_astronaut_clock_bold",
        "true_astronaut_clock_seconds"
    )

    fun install(classLoader: ClassLoader): Boolean = try {
        val cacheClass = Class.forName(CACHE_CLASS, false, classLoader)
        val readableField = cacheClass.getDeclaredField("mReadableFields").apply { isAccessible = true }
        val method = cacheClass.getDeclaredMethod(
            "getStringForUser", ContentResolver::class.java, String::class.java,
            Int::class.javaPrimitiveType
        ).apply { isAccessible = true }

        XposedBridge.hookMethod(method, object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                val key = param.args.getOrNull(1) as? String ?: return
                if (key !in allowedKeys) return
                try {
                    @Suppress("UNCHECKED_CAST")
                    val readable = readableField.get(param.thisObject) as? MutableSet<String>
                        ?: throw IllegalStateException("mReadableFields is not a mutable Set")
                    readable.add(key)
                } catch (t: Throwable) {
                    XposedBridge.log("TrueAstronaut: readable-key allowlist failed for $key: \${t.message.orEmpty()}")
                }
            }
        })
        XposedBridge.log("TrueAstronaut: restricted Settings readable-key hook installed")
        true
    } catch (t: Throwable) {
        XposedBridge.log("TrueAstronaut: Settings hook failed: \${t.javaClass.simpleName}: \${t.message.orEmpty()}")
        false
    }
}
