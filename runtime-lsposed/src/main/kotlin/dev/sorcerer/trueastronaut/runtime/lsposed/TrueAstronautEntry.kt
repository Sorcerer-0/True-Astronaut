package dev.sorcerer.trueastronaut.runtime.lsposed

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * Runtime entry point.
 *
 * The app process receives a narrowly allowlisted Settings readable-key hook.
 * SystemUI remains a separate future hook target for ROM UI features.
 */
class TrueAstronautEntry : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        when (lpparam.packageName) {
            APP_PACKAGE -> {
                val installed = RestrictedSettingsReadableHook.install(lpparam.classLoader)
                XposedBridge.log(
                    "TrueAstronaut: app-process Settings bridge " +
                        if (installed) "installed" else "FAILED"
                )
            }

            SYSTEM_UI_PACKAGE -> {
                val probe = LegacyXposedRuntimeAdapter(lpparam.classLoader).probe()
                XposedBridge.log(
                    "TrueAstronaut: SystemUI loaded; runtimeAvailable=${probe.available}; " +
                        "runtime=${probe.runtimeVersion ?: "unknown"}; detail=${probe.detail.orEmpty()}"
                )
            }
        }
    }

    private companion object {
        const val APP_PACKAGE = "dev.sorcerer.trueastronaut"
        const val SYSTEM_UI_PACKAGE = "com.android.systemui"
    }
}
