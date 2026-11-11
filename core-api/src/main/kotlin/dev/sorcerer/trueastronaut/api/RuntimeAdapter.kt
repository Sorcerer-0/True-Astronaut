package dev.sorcerer.trueastronaut.api

/**
 * Framework-neutral boundary between True Astronaut features and a hook runtime.
 *
 * Implementations belong in isolated adapter modules. Feature modules must not
 * depend on LSPosed/Xposed/other framework APIs directly.
 */
interface RuntimeAdapter {
    val id: String
    val displayName: String

    /** Detect whether this adapter can operate in the current process/runtime. */
    fun probe(): RuntimeProbe

    /** Install one hook through this runtime. Must be idempotent per hookId. */
    fun install(request: HookRequest): HookInstallResult

    /** Remove a previously installed hook when the runtime supports it. */
    fun uninstall(hookId: String): HookInstallResult
}

data class RuntimeProbe(
    val available: Boolean,
    val runtimeVersion: String? = null,
    val apiLevel: Int? = null,
    val detail: String? = null
)

data class HookRequest(
    val hookId: String,
    val processName: String,
    val targetClass: String,
    val targetMethod: String,
    val requiredFeature: FeatureId
)

sealed interface HookInstallResult {
    data object Installed : HookInstallResult
    data object AlreadyInstalled : HookInstallResult
    data class Unsupported(val reason: String) : HookInstallResult
    data class Failed(val reason: String) : HookInstallResult
}
