package dev.sorcerer.trueastronaut.api

@JvmInline
value class FeatureId(val value: String) {
    init {
        require(value.matches(Regex("[a-z0-9_.-]+"))) {
            "FeatureId must be a stable lowercase identifier"
        }
    }
}

enum class FeatureState {
    ENABLED,
    DISABLED,
    UNAVAILABLE,
    ERROR
}

enum class CapabilityLevel {
    FULL,
    LIMITED,
    UNSUPPORTED,
    UNKNOWN
}

data class FeatureCapability(
    val level: CapabilityLevel,
    val reason: String? = null
)

data class FeatureStatus(
    val id: FeatureId,
    val state: FeatureState,
    val capability: FeatureCapability,
    val activeBackend: String? = null,
    val message: String? = null
)

/** User intent is not proof that the feature is actually active. */
data class FeatureConfig(
    val enabled: Boolean,
    val values: Map<String, String> = emptyMap()
)

sealed interface FeatureApplyResult {
    data object Applied : FeatureApplyResult
    data object NoChange : FeatureApplyResult
    data class Unsupported(val reason: String) : FeatureApplyResult
    data class Rejected(val reason: String) : FeatureApplyResult
    data class Failed(val reason: String) : FeatureApplyResult
}

interface FeatureBackend {
    val featureId: FeatureId

    fun probe(): FeatureCapability
    fun readStatus(): FeatureStatus
    fun apply(config: FeatureConfig): FeatureApplyResult
}
