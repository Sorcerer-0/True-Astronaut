package dev.sorcerer.trueastronaut.runtime.lsposed

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage

class TrueAstronautEntry : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        when (lpparam.packageName) {
            APP_PACKAGE -> {
                val installed = RestrictedSettingsReadableHook.install(lpparam.classLoader)
                XposedBridge.log("TrueAstronaut: app Settings bridge ${if (installed) "installed" else "FAILED"}")
            }
            SYSTEM_UI_PACKAGE -> {
                val probe = LegacyXposedRuntimeAdapter(lpparam.classLoader).probe()
                val clock = StatusBarClockFeature.install(lpparam.classLoader)
                XposedBridge.log(
                    "TrueAstronaut: SystemUI runtimeAvailable=${probe.available}; " +
                        "clockFeature=${clock}; runtime=${probe.runtimeVersion ?: "unknown"}"
                )
            }
        }
    }

    private companion object {
        const val APP_PACKAGE = "dev.sorcerer.trueastronaut"
        const val SYSTEM_UI_PACKAGE = "com.android.systemui"
    }
}
