package dev.sorcerer.trueastronaut.api

interface RuntimeAdapter {
    val id: String
    val displayName: String

    fun probe(): RuntimeProbe

    /** Install a hook. Implementations must make repeated hookId installs idempotent. */
    fun install(request: HookRequest, callback: HookCallback): HookInstallResult

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
    val parameterTypeNames: List<String> = emptyList(),
    val requiredFeature: FeatureId
)

interface HookCallback {
    fun before(invocation: HookInvocation) = Unit
    fun after(invocation: HookInvocation) = Unit
}

/** Framework-neutral view of a method-hook invocation. */
interface HookInvocation {
    val receiver: Any?
    val arguments: MutableList<Any?>
    var result: Any?
    var throwable: Throwable?
}

sealed interface HookInstallResult {
    data object Installed : HookInstallResult
    data object AlreadyInstalled : HookInstallResult
    data class Unsupported(val reason: String) : HookInstallResult
    data class Failed(val reason: String) : HookInstallResult
}
