package dev.sorcerer.trueastronaut.runtime.lsposed

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * Runtime entry point. No feature hooks are registered until target-ROM
 * method signatures have been verified and feature implementations exist.
 */
class TrueAstronautEntry : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != "com.android.systemui") return

        val probe = LegacyXposedRuntimeAdapter(lpparam.classLoader).probe()
        XposedBridge.log(
            "TrueAstronaut: SystemUI loaded; runtimeAvailable=${probe.available}; " +
                "runtime=${probe.runtimeVersion ?: "unknown"}; detail=${probe.detail.orEmpty()}"
        )
    }
}
